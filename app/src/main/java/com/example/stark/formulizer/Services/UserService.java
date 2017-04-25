package com.example.stark.formulizer.Services;

import com.example.stark.formulizer.Models.GeneralResponseModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Stark on 01-03-2017.
 */

public interface UserService {
    @FormUrlEncoded
    @POST("user/login")
    Call<GeneralResponseModel<String>> login(@Field("email") String email, @Field("password") String pass);
}
