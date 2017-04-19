# coding=utf-8
from django.shortcuts import render, redirect, get_object_or_404
from django.contrib.auth.decorators import login_required
from .forms import UploadFileForm, MagnetFileForm
from .models import Documents
import os
from stamp_server.settings import MEDIA_ROOT, MEDIA_URL

def start_page(request):
    if request.user.is_authenticated:
        # ZALOGOWANY
        uploadForm = UploadFileForm()
        magnetForm = MagnetFileForm()
        data = {
            'uploadForm': uploadForm,
            'magnetForm': magnetForm
        }
        return render(request, 'main_app/sign_and_verify.html', data)
    else:
        # NIEZALOGOWANY
        return render(request, 'main_app/descr_and_verify.html')

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

@login_required
def upload_file(request):
    if request.method == 'POST':
        form = UploadFileForm(request.POST, request.FILES)
        if form.is_valid():
            plik = form.save(commit=False)
            plik.owner = request.user
            plik.save()
            return redirect('main_app:start_page')
        else:
            print("Błędny formularz !!!")
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