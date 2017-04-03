from django.shortcuts import render
from django.contrib.auth.decorators import login_required

def start_page(request):
    if request.user.is_authenticated:
        return render(request, 'main_app/sign_and_verify.html')
    else:
        return render(request, 'main_app/descr_and_verify.html')

@login_required
def archives(request):
    return render(request, 'main_app/archives.html')