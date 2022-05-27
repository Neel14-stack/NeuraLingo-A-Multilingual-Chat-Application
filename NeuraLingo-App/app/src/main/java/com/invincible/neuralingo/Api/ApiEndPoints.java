package com.invincible.neuralingo.Api;

import com.invincible.neuralingo.Model.ApiDataModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiEndPoints{

    @POST("models/translate")
    Call<ResponseBody> translate(@Body ApiDataModel apiDataModel);

    @POST("models/translateAll")
    Call<ResponseBody> getAllTranslation(@Body ApiDataModel apiDataModel);
}
