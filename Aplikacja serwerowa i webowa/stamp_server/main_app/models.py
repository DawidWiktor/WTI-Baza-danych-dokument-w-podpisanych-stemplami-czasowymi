from django.db import models
from django.contrib.auth.models import User
import os  # for filename in Documents
import django.utils.timezone as timezone  # data i czas

def user_directory_path(instance, filename):
    # ustawia katalog zapisu pliku
    # rozny dla kazdego uzytkownika
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return '{0}/{1}'.format(instance.owner.id, filename)

class Documents(models.Model):
    owner = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name="właściciel")
    file = models.FileField(upload_to=user_directory_path, verbose_name="plik")
    timestamp = models.DateTimeField(default=timezone.now)
    hash = models.CharField(max_length=64, null=True)

    def filename(self):
        # return filename of the file, because field 'file' return path
        return os.path.basename(self.file.name)
