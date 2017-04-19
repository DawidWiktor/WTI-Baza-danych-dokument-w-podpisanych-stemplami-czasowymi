from django.db import models
from django.contrib.auth.models import User
import os  # for filename in Documents
from datetime import datetime

def user_directory_path(instance, filename):
    # ustawia katalog zapisuj pliku
    # rozny dla kazdego uzytkownika
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return '{0}/{1}'.format(instance.owner.id, filename)

class Documents(models.Model):
    owner = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name="właściciel")
    file = models.FileField(upload_to=user_directory_path, verbose_name="plik")
    date = models.DateTimeField(default=datetime.now())

    def filename(self):
        # return filename of the file, becaouse field 'file' return relative path
        return os.path.basename(self.file.name)
