from django.conf.urls import url, include

from . import views

app_name = 'main_app'
urlpatterns = [
    url(r'^$', views.start_page, name='start_page'),
    url(r'^user_account/', include("user_account.urls", namespace="user_account")),
    url(r'^archives/$', views.archives, name='archives'),
    url(r'^upload/$', views.upload_file, name='upload_file'),
    url(r'^magnet/$', views.magnet_file, name='magnet_file'),
]