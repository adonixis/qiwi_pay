from django.urls import path
from . import views

urlpatterns = [
    path('phone', views.token_create),
    path('sms', views.sms),
    path('payment', views.payment)
]
