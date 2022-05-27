from django.urls import path
from . import views

urlpatterns = [
    path("translate", views.getInputString),
    path("translateAll", views.translate_all)
]
