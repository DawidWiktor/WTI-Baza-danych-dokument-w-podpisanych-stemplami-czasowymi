# coding=utf-8
from django.db import models
from django.contrib.auth.models import User
import os  # for filename in Documents
import django.utils.timezone as timezone  # data i czas
import hashlib

def user_directory_path(instance, filename):
    # ustawia katalog zapisu pliku
    # rozny dla kazdego uzytkownika
    # file will be uploaded to MEDIA_ROOT/user_<id>/<filename>
    return '{0}/{1}'.format(instance.owner.id, filename)

class Documents(models.Model):
    owner = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name="właściciel")
    file = models.FileField(upload_to=user_directory_path, verbose_name="plik")
    timestamp = models.DateTimeField(default=timezone.now)
    hash = models.CharField(max_length=64, unique=True, db_index=True)

    def filename(self):
        # return filename of the file, because field 'file' return path
        # w archives.html z tego korzystam
        return os.path.basename(self.file.name)

class Tokens(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE, verbose_name="właściciel")
    key = models.CharField(max_length=64, unique=True)
    timestamp = models.DateTimeField(default=timezone.now)

    @staticmethod
    def generate_key(username, timestamp):
        hasher = hashlib.sha256()
        hasher.update(str(username).encode('utf-8')+str(timestamp).encode('utf-8'))
        return hasher.hexdigest()

    def save(self, *args, **kwargs):
        self.key = self.generate_key(self.user.username, self.timestamp)
        super(Tokens, self).save(*args, **kwargs)
