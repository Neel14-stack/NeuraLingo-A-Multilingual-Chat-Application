package com.invincible.neuralingo.Interfaces;

import android.util.Log;

import org.json.JSONObject;

public abstract class ApiCallbacks {
    public final String NO_RESPONSE = "No Response from Server";
    String LOG_TAG = ApiCallbacks.class.getSimpleName();

    public void onSuccessfulSetup()
    {

    }

    public void onFailedSetup()
    {}

    public void onMessageTranslated(JSONObject jsonObject){
        Log.d(LOG_TAG, jsonObject.toString());
    }

    public void onError(String errorMessage)
    {
        Log.e(LOG_TAG, errorMessage);
    }
}
