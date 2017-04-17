from django import forms
from .models import Documents

class UploadFileForm(forms.ModelForm):
    # formularz oparty na modelu Documents
    class Meta:
        model = Documents
        fields = ('file', )