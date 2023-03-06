package com.ixxc.myuit.Interface;

import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;
import com.ixxc.myuit.Model.Token;
import com.ixxc.myuit.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {
    // Get token
    @FormUrlEncoded
    @POST("auth/realms/master/protocol/openid-connect/token")
    Call<Token> getToken(@Field("grant_type") String type, @Field("code") String code, @Field("client_id") String client, @Field("redirect_uri") String redirect);

    // Get user info
    @GET("api/master/user/user")
    Call<User> getUserInfo();

    @GET("api/master/asset/user/current")
    Call<List<Device>> getUserDevices();

    @GET("api/master/model/assetInfos")
    Call<List<Model>> getDeviceModels();
}
