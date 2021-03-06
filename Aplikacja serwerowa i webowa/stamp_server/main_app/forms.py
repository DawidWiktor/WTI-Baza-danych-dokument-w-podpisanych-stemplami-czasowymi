from django import forms
from .models import Documents

class UploadFileForm(forms.ModelForm):
    # upload pliku do podpisania
    # formularz oparty na modelu Documents
    def __init__(self, *args, **kwargs):
        # musialem przeciazyc kontruktor zeby mozna bylo do formularza przekazac obiekt user'a
        # obiekt user'a potrzebny jest nizej w clean() do walidacji
        self.user = kwargs.pop('user', None)
        super(UploadFileForm, self).__init__(*args, **kwargs)

    def clean(self):
        # odpala sie automatycznie podczas 'form.is_valid()' w views.py
        # waliduje czy plik o takiej samej nazwie juz jest w bazie
        document = self.cleaned_data['file']  # obiekt Documents
        document = str(self.user.id) + "/" + str(document)  # np. '1/plik.txt'
        document = document.replace(" ","_")
        other_the_same = Documents.objects.filter(file=document)
        if other_the_same:
            # jezeli w bazie istnieje juz plik o takiej nazwie
            raise forms.ValidationError("File with the same name exist in database !!!")
        return self.cleaned_data

    class Meta:
        model = Documents  # jaki model
        fields = ('file', )  # jakie pola z modelu ma wziac

class MagnetFileForm(forms.Form):
    # upload pliku magnetycznego
    magnet_file = forms.FileField(label='Plik')  # help_text='' - dodatkowa informacja

    def clean_magnet_file(self):
        magnet_file = self.cleaned_data['magnet_file']
        mime = magnet_file.content_type
        if mime == 'application/octet-stream' and magnet_file.size <= 100:
            # plik jest typu application/octet-stream i ma miec <= 100 bajtow
            return magnet_file
        else:
            # rzucam blad, ale w views.py i tak go nie lapie, bo tam sprawdzam poprawnosc if'em
            raise forms.ValidationError('Bad magnet file !!!')