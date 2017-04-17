from django import forms
from .models import Documents
import magic  # walidacja formularzy

class UploadFileForm(forms.ModelForm):
    # upload pliku do podpisania
    # formularz oparty na modelu Documents
    class Meta:
        model = Documents
        fields = ('file', )

class MagnetFileForm(forms.Form):
    # upload pliku magnetycznego
    file = forms.FileField(label='Plik')  # help_text='' - dodatkowa informacja

    def clean_file(self):
        file = self.cleaned_data['file']
        mime = magic.from_buffer(file.read(), mime=True)
        if (mime == 'application/pdf' or mime == 'text/plain') and file._size <= 5242880:
            return file
        else:
            raise forms.ValidationError('Tylko PDF i plain text oraz mniejsze niz 5 MB !!!')