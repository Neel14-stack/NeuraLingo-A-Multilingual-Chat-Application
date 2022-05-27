package com.invincible.neuralingo.Api;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.invincible.neuralingo.Interfaces.ApiCallbacks;
import com.invincible.neuralingo.Model.ApiDataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiMainClass {
    DatabaseReference databaseReference;
    static String api_url = "";
    static Retrofit retrofit;
    static ApiEndPoints apiEndPoints;
    public static String[] allModelNames = {"eng-deu", "eng-spa", "eng-ben", "eng-hin", "eng-fra"};
    String LOG_TAG = ApiMainClass.class.getSimpleName();
    public static ApiMainClass obj;

    private ApiMainClass(DatabaseReference databaseReference, ApiCallbacks apiCallbacks)
    {
        this.databaseReference = databaseReference;
        databaseReference.child("ApiMetadata").child("url").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                api_url = dataSnapshot.getValue(String.class);
                Log.e(LOG_TAG, api_url);
                if(api_url != null)
                {
                    retrofit = new Retrofit.Builder().baseUrl(api_url).addConverterFactory(GsonConverterFactory.create()).build();
                    apiEndPoints = retrofit.create(ApiEndPoints.class);
                    apiCallbacks.onSuccessfulSetup();
                    Log.e(LOG_TAG, "Done Creating Api Object");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                apiCallbacks.onFailedSetup();
            }
        });
    }

    public static void getApiMainClassObject(ApiCallbacks apiCallbacks)
    {
        if(obj==null)
        {
            obj = new ApiMainClass(FirebaseDatabase.getInstance().getReference(), new ApiCallbacks() {
                @Override
                public void onSuccessfulSetup() {
                    super.onSuccessfulSetup();
                    apiCallbacks.onSuccessfulSetup();
                }
            });
        }else {
            apiCallbacks.onSuccessfulSetup();
        }
    }

    public void getAllTranslation(ApiDataModel apiDataModel, ApiCallbacks apiCallbacks)
    {
        Call<ResponseBody> call = apiEndPoints.getAllTranslation(apiDataModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Log.d(LOG_TAG, response.body().toString());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = generateJsonObject(response.body().string());
                        apiCallbacks.onMessageTranslated(jsonObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    apiCallbacks.onError("No response from Api");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
                apiCallbacks.onError("Failure on getting Response after Sending Translation Request");
            }
        });
    }

    public void sendMessage(ApiDataModel apiDataModel, ApiCallbacks apiCallbacks)
    {
        Call<ResponseBody> call = apiEndPoints.translate(apiDataModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.body() != null) {
                    Log.d(LOG_TAG, response.body().toString());
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = generateJsonObject(response.body().string());
                        apiCallbacks.onMessageTranslated(jsonObject);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    apiCallbacks.onError("No response from Api");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                System.out.println(t.getMessage());
                apiCallbacks.onError("Failure on getting Response after Sending Translation Request");
            }
        });
    }
    public JSONObject generateJsonObject(String inp_response) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(inp_response);
        } catch (JSONException jsonException) {
            Log.e(LOG_TAG, jsonException.toString());
        }
        return jsonObj;
    }
}
