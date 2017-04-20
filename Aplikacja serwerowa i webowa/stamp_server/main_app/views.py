# coding=utf-8
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from .forms import UploadFileForm, MagnetFileForm
from .models import Documents
import os
from stamp_server.settings import MEDIA_ROOT, MEDIA_URL
import hashlib  # generowanie SHA384

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
        # NIEZALOGOWANY
        return render(request, 'main_app/Home_notLogged.html')

@login_required
def archives(request):
    docs = Documents.objects.filter(owner=request.user)
    data = {
        "docs": docs,
        "user": request.user,
        "MEDIA_URL": MEDIA_URL
    }
    return render(request, 'main_app/archives.html', data)

@login_required
def delete_file(request, file_id):
    user = request.user
    doc = get_object_or_404(Documents, pk=file_id)
    if doc.owner == user:
        doc.delete()  # usuniecie pliku z bazy danych
        os.remove(os.path.join(MEDIA_ROOT, doc.file.name))  # usuniecie pliku z dysku
    return redirect('main_app:archives')

def file_sha256(document):
    hasher = hashlib.sha256()  # to jest SHA-2
    for chunk in document.chunks():
        hasher.update(chunk)
    return hasher.hexdigest()

def string_sha256(s):
    hasher = hashlib.sha256()
    hasher.update(s)
    return hasher.hexdigest()

@login_required
def upload_file(request):
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
            print("Hash pliku: " + str(h))
            print("Timestamp: " + str(ts))
            print("Hash razem: " + str(hash_h_ts))

            plik.hash = hash_h_ts
            plik.save()
            print(plik.hash)
            return redirect('main_app:start_page')
        else:
            print("Bad form !!! File with the same name exist in database !!!")
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')


@login_required
def magnet_file(request):
    if request.method == 'POST':
        form = MagnetFileForm(request.POST, request.FILES)
        if form.is_valid():
            print("Sprawdzene pliku magnetycznego......")  # TODO zrobic
            print(request.FILES)
            plik = request.FILES['file'].read()
            print(plik.decode("utf-8"))
            return redirect('main_app:start_page')
        else:
            print("Błędny formularz !!!")
            return redirect('main_app:start_page')
    else:
        return redirect('main_app:start_page')