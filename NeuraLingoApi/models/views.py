from django.http import JsonResponse
from keras.models import load_model
from . modelclass import ModelClass
from . import config
import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from . attentionlayers import *
from django.views.decorators.csrf import csrf_exempt
import json

if not firebase_admin._apps:
    cred = credentials.Certificate(config.service_key)
    firebase_admin.initialize_app(cred,
                                  {'databaseURL': config.DATABASE_URL})
database = db.reference()
# Create your views here.

models = {}

for key in config.TRANSLATION_MODELS:
    models[key] = load_model("models/Models/" + key + '.h5',
                             custom_objects={'PositionalEmbedding': PositionalEmbedding,
                                             "TransformerEncoder": TransformerEncoder,
                                             "TransformerDecoder": TransformerDecoder})
model = ModelClass(models)

@csrf_exempt
def getInputString(request):
    if request.method == "POST":
        print(request.body)
        post_data = json.loads(request.body.decode("utf-8"))
        message = post_data.get("message", "")
        translate_keys = post_data.get("t_keys")
        room_name = post_data.get("room_name", "")
        message_id = post_data.get("message_id", "")
        del post_data["room_name"]
        del post_data["message_id"]
        del post_data["message"]
        del post_data["t_keys"]
        print("Starting Execution")
        print(message, translate_keys, room_name)
        output_translated = model.translate(translate_keys, message)
        print(output_translated)
        message_map = {}
        for output_trans in output_translated:
            message_map[output_trans] = output_translated[output_trans]
        message_map[translate_keys[0].split("-")[1] + "-" + translate_keys[0].split("-")[0]] = message
        post_data["messageMap"] = message_map
        # output_translated[translate_keys[0].split("-")[1] + "-" + translate_keys[0].split("-")[0]] = message
        # output_translated["sender"] = sender_id
        saveToDb(room_name, message_id, post_data)
        # output_translated["position"] = position
        return JsonResponse(post_data)
    else:
        return JsonResponse({"data": "Get Request are not accepted"})

def saveToDb(room, mess_id, result):
    db.reference("Rooms").child(room).child("Chats").child(mess_id).set(result)


@csrf_exempt
def translate_all(request):
    if request.method == "POST":
        post_data = json.loads(request.body.decode("utf-8"))
        print(post_data)
        translate_keys = post_data.get("t_keys", [])
        message = post_data.get("message", "")
        output_translated = model.translate(translate_keys, message)
        return JsonResponse(output_translated)
    else:
        return JsonResponse({"data": "Get Request Not Accepted"})
    pass
