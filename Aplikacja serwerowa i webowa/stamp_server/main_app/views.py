# coding=utf-8
from __future__ import print_function
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from .forms import UploadFileForm, MagnetFileForm
from .models import Documents, Tokens
import os
from stamp_server.settings import MEDIA_ROOT, MEDIA_URL
import hashlib  # generowanie SHA384
from django.core.signing import Signer  # podpisywanie pliku magnetycznego
from django.core.signing import BadSignature
from django.contrib import messages  # powiadomienia, ktore mozna wyswietlic w HTMLu
from django.views.decorators.csrf import csrf_exempt  # wylaczenie CSRF tokenow
from django.http import JsonResponse  # zwracanie JSONow w odpowiedzi
from django.contrib.auth.models import User  # model User
from django.contrib.auth import authenticate  # metoda autoryzacji
from django.core.mail import send_mail  # wysylanie maili
import django.utils.timezone as timezone  # data i czas
from django.http import FileResponse  # zwracanie pliku - API

from stamp_server.settings import EMAIL_HOST_USER  # import maila tej apki
HOST_NAME = "127.0.0.1:8000"  # potrzebne podczas generowanie linku aktywacyjnego przy rejestracji

def start_page(request):
    if request.user.is_authenticated:
        # ZALOGOWANY
        upload_form = UploadFileForm(user=request.user)
        magnet_form = MagnetFileForm()
        data = {
            'uploadForm': upload_form,
            'magnetForm': magnet_form
        }
        return render(request, 'main_app/Home_logged.html', data)
    else:
        magnet_form = MagnetFileForm()
        data = {
            'magnetForm': magnet_form
        }
        # NIEZALOGOWANY
        return render(request, 'main_app/Home_notLogged.html', data)

@login_required
def archives(request):
    docs = Documents.objects.filter(owner=request.user)
    # bug - aplikacja nie rozpoznaje czy plik jest fizycznie na dysku
    data = {
        "docs": docs,
        "user": request.user,
        "MEDIA_URL": MEDIA_URL
    }
    return render(request, 'main_app/archives.html', data)

@login_required
def delete_file(request, file_id):
    # usuniecie pliku z archiwum
    user = request.user
    doc = get_object_or_404(Documents, pk=file_id)
    path = 'media/' + str(request.user.id)  # katalog w ktorym sa pliki

    # sprawdzam czy istnieje tez plik magnetyczny do dokumentu
    magnet_filename = str(doc.filename())+"-"+str(doc.timestamp.strftime('%d.%m.%Y-%H:%M'))+".magnet"
    magnet_path = os.path.join(path, magnet_filename)
    if os.path.isfile(magnet_path):
        # usuwam plik magnetyczny
        os.remove(magnet_path)

    if doc.owner == user:
        # usuniecie dokumentu z bazy danych
        doc.delete()

    doc_path = os.path.join(path, os.path.basename(doc.file.name))  # sciezka do dokumentu
    if os.path.isfile(doc_path):
        # usuniecie dokumentu z dysku
        os.remove(doc_path)
    messages.success(request,'Usunięto plik z archiwum.')
    return redirect('main_app:archives')

# --------------   metody pomocnicze    ----------
# obliczanie funkcji skrotu na pliku
def file_sha256(document):
    hasher = hashlib.sha256()  # to jest SHA-2
    for chunk in document.chunks():
        hasher.update(chunk)
    return hasher.hexdigest()

# obliczanie funkcji skrotu na lancuchu znakow
def string_sha256(s):
    hasher = hashlib.sha256()
    hasher.update(s)
    return hasher.hexdigest()

@login_required
def upload_file(request):
    # dodanie pliku do archiwum
    if request.method == 'POST':
        form = UploadFileForm(request.POST, request.FILES, user=request.user)
        if form.is_valid():
            plik = form.save(commit=False)
            plik.owner = request.user

            # generowanie hash'a
            h = file_sha256(plik.file)
            # timestamp z bazy danych
            ts = plik.timestamp.timestamp()  # zwraca sekundy.milisekundy od 01.01.1970
            ts = str(ts).split(".")[0]  # wybieram z timestampa sekundy
            # hash z h+ts
            hash_h_ts = string_sha256(str(h).encode('utf-8')+str(ts).encode('utf-8'))

            plik.hash = hash_h_ts
            plik.save()
            messages.success(request,'Dodano plik do archiwum.')
            return redirect('main_app:start_page')
        else:
            print("Bad form !!! File with the same name exist in database !!!")
            messages.error(request,'Plik o identycznej nazwie istnieje w archwium. Zmień nazwę.')
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')


@login_required
def download_magnet_file(request, file_id):
    # pobranie pliku magnetycznego z archiwum
    doc = get_object_or_404(Documents, id=file_id)
    if request.user == doc.owner:
        magnet_filename = str(doc.filename())+"-"+str(doc.timestamp.strftime('%d.%m.%Y-%H:%M'))+".magnet"  # MAGNET FILENAME
        magnet_path = 'media/'+str(request.user.id) + '/' + magnet_filename  # MAGNET FILE PATH

        if not os.path.isfile(magnet_path):
            # jezeli plik magnetyczny nie istnieje jeszcze to go utworz
            magnet = open(magnet_path,'w')
            # podpis pliku
            signer = Signer()  # salt='jakas_sol'
            signed_hash = signer.sign(doc.hash)

            magnet.write(signed_hash)
            magnet.close()
        return redirect('media', path=str(request.user.id)+'/'+magnet_filename)
    return redirect('main_app:start_page')


def magnet_file(request):
    # weryfikacja pliku magnetycznego
    if request.method == 'POST':
        form = MagnetFileForm(request.POST, request.FILES)
        if form.is_valid():
            lancuch = request.FILES['magnet_file'].read()
            lancuch = lancuch.decode("utf-8")  # konwersja kodowania z bytes na string utf-8

            # sprawdzenie podpisu
            signer = Signer()  # salt='jakas_sol'
            try:
                signed_hash = signer.unsign(lancuch)
            except BadSignature:
                print("Sign error !!!")
                messages.error(request,'Plik nie przeszedł weryfikacji.')
                return redirect('main_app:start_page')

            doc = Documents.objects.filter(hash=signed_hash).first()  # szukam pliku do ktorego kieruje magnet
            if doc and os.path.isfile("media/"+doc.file.name):
                data = {
                    'doc': doc,
                    "MEDIA_URL": MEDIA_URL
                }
                return render(request, 'main_app/Magnet_file_details.html', data)
            else:
                messages.warning(request,'Plik magnetyczny prowadzi do dokumentu, który już nie istnieje :( ')
                return render(request, 'main_app/Magnet_file_details.html')
        else:
            messages.error(request,'Maksymalny rozmiar 100 bajtów. Tylko pliki .magnet')
            print("Błędny formularz pliku magnetycznego !!!")
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')

#
#
#
# ----------------------------------------------  API  ---------------------------------------------------------
#
#

@csrf_exempt
def api_login(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        user = authenticate(username=username, password=password)
        if user is None or not user.is_active:
            return JsonResponse({"token": "error"})

        tok, created = Tokens.objects.get_or_create(user=user)
        return JsonResponse({"token": tok.key})
    else:
        return JsonResponse({"token":"error"})

@csrf_exempt
def api_logout(request):
    if request.method == 'POST':
        try:
            token = Tokens.objects.get(key=request.POST.get('token'))
            token.delete()
            return JsonResponse({"status":"ok"})
        except Tokens.DoesNotExist:
            return JsonResponse({"status":"error"})
    else:
        return JsonResponse({"status":"error"})

@csrf_exempt
def api_register(request):
    if request.method == 'POST':
        username = request.POST.get('username')
        password = request.POST.get('password')
        email = request.POST.get('email')
        if User.objects.filter(username=username).exists():
            return JsonResponse({"status":"Username exists"})

        new_user = User.objects.create_user(username, email, password, is_active=False)
        new_user.save()

        activation_code = hashlib.md5(email.encode('utf-8') + b'dUzOSoLi').hexdigest()
        subject = "Stamp service - Activation Code"
        text = (
            """
            Siemka {},
            \npotwierdz e-mail klikajac w link
            \n{}/account/activate/{}/{}/
            \nMasters of Masters
            """.format(username, HOST_NAME, username, activation_code))

        send_mail(subject, text, EMAIL_HOST_USER, [email], fail_silently=False)  # wyslanie maila
        return JsonResponse({"status": "ok", "info": "activate email by link"})

@csrf_exempt
def api_del_account(request):
    tok = Tokens.objects.get(key=request.POST.get('token'))
    if tok is None:
        return JsonResponse({"status": "error"})
    user = tok.user
    user.is_active = False
    # user.delete()  # usuwa uzytkownika i wszystkie dane z nim powiazane
    return JsonResponse({"status": "ok"})

@csrf_exempt
def api_change_mail(request):
    if request.method == 'POST' and request.POST['email']:
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})
        new_email = request.POST.get('email')
        obj_user = tok.user
        obj_user.email = new_email
        obj_user.is_active = False  # uzytkownik bedzie musial potwierdzic nowego maila
        obj_user.save()
        username = obj_user.username

        #  to samo co podczas rejestracji: wysyla na maila link aktywacyjny
        activation_code = hashlib.md5(new_email.encode('utf-8') + b'dUzOSoLi').hexdigest()
        subject = "Stamp service - Change email - activation"
        text = (
            """
            Siemka {},
            \npotwierdz e-mail klikajac w link
            \n{}/account/activate/{}/{}/
            \nMasters of Masters
            """.format(username, HOST_NAME, username, activation_code))

        send_mail(subject,text,EMAIL_HOST_USER,[new_email],fail_silently=False)  # wyslanie maila
        tok.delete()
        return JsonResponse({"status": "ok"})

@csrf_exempt
def api_change_password(request):
    if request.method == 'POST' and request.POST['old_password'] and request.POST['new_pass1'] \
            and request.POST['new_pass2']:
        old_password = request.POST.get('old_password')

        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})
        user = tok.user

        user_check = authenticate(username=tok.user.username, password=old_password)
        if user_check != user:
            return JsonResponse({"status": "bad old password"})

        new_pass1 = request.POST.get('new_pass1')
        new_pass2 = request.POST.get('new_pass2')
        if new_pass1 != new_pass2:
            return JsonResponse({"status": "not the same passwords"})

        user.set_password(new_pass1)
        user.save()
        tok.delete()
        return JsonResponse({"status": "ok", "info": "please login with new password"})
    return JsonResponse({"status": "error"})


@csrf_exempt
def api_archives(request):
    tok = Tokens.objects.get(key=request.POST.get('token'))
    if tok is None:
        return JsonResponse({"status": "error"})
    user = tok.user

    tmp_docs = Documents.objects.filter(owner=user)
    docs = []
    for doc in tmp_docs:
        one_doc = {
                "id": str(doc.id),
                "nazwa": str(doc.file).split("/")[1],
                "timestamp": str(doc.timestamp),
                "autor": str(doc.owner.username),
                "pobierz": str(request.get_host()+MEDIA_URL+doc.file.name)
        }
        docs.append(one_doc)
    data = {
        "docs": docs
    }
    return JsonResponse(data)
    
@csrf_exempt
def api_delete_file(request):
    if request.method == 'POST':
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})
        user = tok.user

        file_id = request.POST.get('file_id')
        doc = Documents.objects.filter(pk=file_id).first()
        if doc is None or doc.owner != user:
            return JsonResponse({"status": "error"})

        path = 'media/' + str(user.id)  # katalog w ktorym sa pliki

        # sprawdzam czy istnieje tez plik magnetyczny do dokumentu
        magnet_filename = str(doc.filename()) + " " + str(doc.timestamp.strftime('%d.%m.%Y %H:%M')) + ".magnet"
        magnet_path = os.path.join(path, magnet_filename)
        if os.path.isfile(magnet_path):
            # usuwam plik magnetyczny
            os.remove(magnet_path)

        if doc.owner == user:
            # usuniecie dokumentu z bazy danych
            doc.delete()

        doc_path = os.path.join(path, os.path.basename(doc.file.name))  # sciezka do dokumentu
        if os.path.isfile(doc_path):
            # usuniecie dokumentu z dysku
            os.remove(doc_path)
        return JsonResponse({"status": "ok"})
    return JsonResponse({"status": "error"})

def handle_uploaded_file(f, user):
    hasher = hashlib.sha256()
    with open(MEDIA_ROOT+"/"+str(user.id)+"/"+f.name, 'wb+') as destination:
        for chunk in f.chunks():
            destination.write(chunk)
            hasher.update(chunk)
    return hasher.hexdigest()


@csrf_exempt
def api_upload_file(request):
    if request.method == 'POST' and request.FILES['file']:
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})
        user = tok.user

        name = request.FILES['file'].name
        name = name.replace(" ","_")
        if Documents.objects.filter(file=name).exists():
            return JsonResponse({"status": "file exists"})

        # zapisywanie pliku i generowania hasha
        h = handle_uploaded_file(request.FILES['file'], user)

        # timestamp z bazy danych
        ts_orig = timezone.now()
        ts = ts_orig.timestamp()  # zwraca sekundy.milisekundy od 01.01.1970
        ts = str(ts).split(".")[0]  # wybieram z timestampa sekundy
        # hash z h+ts
        hash_h_ts = string_sha256(str(h).encode('utf-8')+str(ts).encode('utf-8'))

        tmp = Documents.objects.create(owner=user, file=str(user.id)+"/"+request.FILES['file'].name,
                                       hash=hash_h_ts, timestamp=ts_orig)
        tmp.save()
        return JsonResponse({"status": "ok"})
    else:
        return JsonResponse({"status": "error"})

@csrf_exempt
def api_download_magnet_file(request):
    if request.method == 'POST':
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})
        user = tok.user
        doc = Documents.objects.filter(id=request.POST.get('file_id')).first()
        if doc is None:
            return JsonResponse({"status": "error"})
        if user == doc.owner:
            magnet_filename = str(doc.filename()) + "-" + str(
                doc.timestamp.strftime('%d.%m.%Y-%H:%M')) + ".magnet"  # MAGNET FILENAME
            magnet_path = 'media/' + str(user.id) + '/' + magnet_filename  # MAGNET FILE PATH
            if not os.path.isfile(magnet_path):
                # jezeli plik magnetyczny nie istnieje jeszcze to go utworz
                magnet = open(magnet_path, 'w')
                # podpis pliku
                signer = Signer()  # salt='jakas_sol'
                signed_hash = signer.sign(doc.hash)
                magnet.write(signed_hash)
                magnet.close()
            return FileResponse(open(magnet_path, 'rb'))
        else:
            return JsonResponse({"status": "bad file id"})

@csrf_exempt
def api_magnet_file(request):
    if request.method == 'POST' and request.FILES['file']:
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})

        lancuch = request.FILES['file'].read()
        lancuch = lancuch.decode("utf-8")  # konwersja kodowania z bytes na string utf-8

        # sprawdzenie podpisu
        signer = Signer()  # salt='jakas_sol'
        try:
            signed_hash = signer.unsign(lancuch)
        except BadSignature:
            return JsonResponse({"status": "bad sign"})

        doc = Documents.objects.filter(hash=signed_hash).first()  # szukam pliku do ktorego kieruje magnet
        if doc and os.path.isfile("media/" + doc.file.name):
            data = {
                "id": str(doc.id),
                "nazwa": str(doc.file).split("/")[1],
                "timestamp": str(doc.timestamp),
                "autor": str(doc.owner.username)
            }
            return JsonResponse(data)
        else:
            return JsonResponse({"status": "file not exists"})

@csrf_exempt
def api_download_file(request):
    if request.method == 'POST' and request.FILES['file']:
        tok = Tokens.objects.get(key=request.POST.get('token'))
        if tok is None:
            return JsonResponse({"status": "error"})

        lancuch = request.FILES['file'].read()
        lancuch = lancuch.decode("utf-8")  # konwersja kodowania z bytes na string utf-8

        # sprawdzenie podpisu
        signer = Signer()
        try:
            signed_hash = signer.unsign(lancuch)
        except BadSignature:
            return JsonResponse({"status": "bad sign"})

        doc = Documents.objects.filter(hash=signed_hash).first()  # szukam pliku do ktorego kieruje magnet
        path = "media/" + doc.file.name
        if doc and os.path.isfile(path):
            return FileResponse(open(path, 'rb'))
        else:
            return JsonResponse({"status": "file not exists"})