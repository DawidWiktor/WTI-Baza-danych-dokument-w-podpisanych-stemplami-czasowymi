from django.conf.urls import url, include

from . import views

app_name = 'main_app'
urlpatterns = [
    url(r'^$', views.start_page, name='start_page'),
    url(r'^user_account/', include("user_account.urls", namespace="user_account")),
    url(r'^archives/$', views.archives, name='archives'),
    url(r'^upload/$', views.upload_file, name='upload_file'),
    url(r'^magnet/$', views.magnet_file, name='magnet_file'),
    url(r'^archives/delete/(?P<file_id>[0-9]+)/$', views.delete_file, name='delete_file'),
    url(r'^download/magnet/(?P<file_id>[0-9]+)/$', views.download_magnet_file, name='download_magnet_file'),

    # API
    url(r'^api/login/$', views.api_login, name='api_login'),
    url(r'^api/logout/$', views.api_logout, name='api_logout'),
    url(r'^api/register/$', views.api_register, name='api_register'),
    url(r'^api/del_account/$', views.api_del_account, name='api_del_account'),
    url(r'^api/change_mail/$', views.api_change_mail, name='api_change_mail'),
    url(r'^api/change_password/$', views.api_change_password, name='api_change_password'),
    url(r'^api/archives/$', views.api_archives, name='api_archives'),
    url(r'^api/del_file/$', views.api_delete_file, name='api_delete_file'),
    url(r'^api/upload/$', views.api_upload_file, name='api_upload_file'),
    url(r'^api/download_magnet/$', views.api_download_magnet_file, name='api_download_magnet_file'),
    url(r'^api/check_magnet/$', views.api_check_magnet, name='api_check_magnet'),
    url(r'^api/download_file/$', views.api_download_file, name='api_download_file'),
]