# coding=utf-8
from __future__ import print_function
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from .forms import UploadFileForm, MagnetFileForm
from .models import Documents
import os
from stamp_server.settings import MEDIA_ROOT, MEDIA_URL
import hashlib  # generowanie SHA384
from django.core.signing import Signer  # podpisywanie pliku magnetycznego
from django.core.signing import BadSignature

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
    magnet_filename = 'plik_' + str(file_id) + ".magnet"
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
            return redirect('main_app:start_page')
        else:
            print("Bad form !!! File with the same name exist in database !!!")
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')


@login_required
def download_magnet_file(request, file_id):
    # pobranie pliku magnetycznego z archiwum
    doc = get_object_or_404(Documents, id=file_id)
    if request.user == doc.owner:
        magnet_filename = 'plik_'+str(file_id)+".magnet"  # MAGNET FILENAME
        magnet_path = 'media/'+str(request.user.id) + '/' + magnet_filename # MAGNET FILE PATH

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
                return redirect('main_app:start_page')

            doc = Documents.objects.filter(hash=signed_hash).first()  # szukam pliku do ktorego kieruje magnet
            if doc and os.path.isfile("media/"+doc.file.name):
                data = {
                    'doc': doc,
                    "MEDIA_URL": MEDIA_URL
                }
                return render(request, 'main_app/Magnet_file_details.html', data)
            else:
                return render(request, 'main_app/Magnet_file_details.html')
        else:
            print("Błędny formularz pliku magnetycznego !!!")
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')