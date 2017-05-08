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
    url(r'^api/test_post/$', views.test_post, name='test_post'),
    url(r'^api/test_get/$', views.test_get, name='test_get'),
]