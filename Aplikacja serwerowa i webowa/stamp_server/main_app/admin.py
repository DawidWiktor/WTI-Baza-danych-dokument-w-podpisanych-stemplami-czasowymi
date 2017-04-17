from django.contrib import admin
from .models import Documents

class DocumentsAdmin(admin.ModelAdmin):
    model = Documents
    list_display = ('id','owner','file')

admin.site.register(Documents, DocumentsAdmin)
