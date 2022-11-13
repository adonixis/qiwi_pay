from django.db import models

# Create your models here.


class UserInfo(models.Model):
    accountid = models.BigAutoField(null=False, blank=False,
                                    primary_key=True)
    requestid = models.CharField(max_length=200, null=True,
                                 blank=True)
    phone = models.CharField(max_length=25, null=True,
                             blank=True)
    refresh_token = models.CharField(max_length=200, null=True,
                                     blank=True)
    access_token = models.CharField(max_length=25, null=True,
                                    blank=True)
