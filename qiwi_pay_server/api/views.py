from django.shortcuts import HttpResponse
import json
import requests
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import hashlib, qrcode
from PIL import Image
import os
from .models import UserInfo
from .SQL_transactions import SQLTransactions as sqlt
# Create your views here.

def hashing(password):
    ''' Foo hashs password entered by user '''
    password = 'kn485' + password + 'bn12mzx09'
    password = password.encode('utf-8')
    password = hashlib.sha256((password)).hexdigest()
    salt = password[3:6]
    salt = salt.encode('utf-8')
    password = password.encode('utf-8')
    hashed_password = hashlib.pbkdf2_hmac(
        hash_name='sha256', password=password, salt=salt, iterations=100000)
    return (hashed_password.hex())

'''

'''


@csrf_exempt
def token_create(request):
    info = json.loads(request.body)
    if sqlt(phone=info["phone"]).validatePhone() is False:
        sqlt(phone=info["phone"], requestid=info['requestId']).createUserbyPhone()
        accountid = sqlt(phone=info["phone"]).findAccountID()
        url = f'https://api.qiwi.com/partner/payin-tokenization-api/v1/sites/sa3khn-00/token-requests'  # тут всё ок
        headers = {"Authorization": "Bearer 74c4d440-fbce-4d13-8afa-198bc9619207"}
        print(info["requestId"])
        new_json = {"requestId": info["requestId"],
                    "phone": info["phone"],
                    "accountid": accountid
                    }
        api_answer = requests.post(url=url, json=new_json, headers=headers)
        #{
        #    "requestId": info["requestId"],
        #    "phone": info["phone"],
        #    "accountid": accountid}, headers=headers)
        api_answer = HttpResponse(api_answer).content.decode('utf-8')
        dict_api_answer = json.loads(api_answer)
        print(dict_api_answer)
        if dict_api_answer['status']['value'] == "WAITING_SMS":
            return HttpResponse(status=200)
        else:
            print(dict_api_answer)
            token = sqlt(phone=info['phone']).getAccessTokenByPhone()
            return JsonResponse({'token': token})
    token = sqlt(phone=info['phone']).getAccessTokenByPhone()
    return JsonResponse({'token': token})

@csrf_exempt
def sms(request):
    info = json.loads(request.body)
    new_json = {"requestId": info["requestId"],
                "smsCode": info["smsCode"]
                }
    print(f"NEW JSON: {new_json}")
    url = 'https://api.qiwi.com/partner/payin-tokenization-api/v1/sites/sa3khn-00/token-requests/complete'
    headers = {"Authorization": "Bearer 74c4d440-fbce-4d13-8afa-198bc9619207"}
    api_answer = requests.post(url=url, json=new_json, headers=headers)
    api_answer = HttpResponse(api_answer).content.decode('utf-8')
    dict_api_answer = json.loads(api_answer)
    print(f"\nDICT API: {dict_api_answer}")
    refresh_token = dict_api_answer['token']['value']
    access_token = hashing(refresh_token)[:20]
    sqlt(access_token=access_token, refresh_token=refresh_token, requestid=info['requestId']).addTokens()
    img = qrcode.make(access_token)
    img.save(f'QR_{access_token}')
    qr_path = str(f'{os.getcwd()}\QR_{access_token}')
    return JsonResponse({'path': qr_path})

@csrf_exempt
def payment(request):
    info = json.loads(request.body)
    currency = str(info['amount']['currency'])
    value = info['amount']['value']
    access_token = info['token']
    accountid = str(sqlt(access_token=access_token).getAccountByAccesToken())
    refresh_token = sqlt(access_token=access_token).getRefreshByAccess()
    print(refresh_token)
    print(accountid)
    new_json = {
                "amount": {
                    "currency": currency,
                    "value": value
                },
                "paymentMethod": {
                    "type": "TOKEN",
                    "paymentToken": refresh_token
                },
                "customer": {
                        "account": accountid
                }
                }
    print(f'NEW JSON: {new_json}')
    url = 'https://api.qiwi.com/partner/payin/v1/sites/sa3khn-00/payments/123'
    headers = {"Authorization": "Bearer 74c4d440-fbce-4d13-8afa-198bc9619207"}
    api_answer = requests.put(url=url, json=new_json, headers=headers)
    print(api_answer)
    return HttpResponse(status=200)