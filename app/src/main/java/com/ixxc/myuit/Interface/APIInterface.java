package com.ixxc.myuit.Interface;

import com.google.gson.JsonObject;
import com.ixxc.myuit.Model.CreateAssetReq;
import com.ixxc.myuit.Model.CreateAssetRes;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;
import com.ixxc.myuit.Model.Token;
import com.ixxc.myuit.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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

    @POST("api/master/asset")
    Call<CreateAssetRes> createDevice(@Body JsonObject body);

    @DELETE("api/master/asset")
    Call<String> delDevice(@Query("assetId") String deviceId);
}
