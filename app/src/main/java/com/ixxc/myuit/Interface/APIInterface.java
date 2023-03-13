package com.ixxc.myuit.Interface;

import com.google.gson.JsonObject;
import com.ixxc.myuit.Model.CreateAssetReq;
import com.ixxc.myuit.Model.CreateAssetRes;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;
import com.ixxc.myuit.Model.Realm;
import com.ixxc.myuit.Model.Role;
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
import retrofit2.http.PUT;
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

    // Get user roles
    @GET("api/master/user/userRoles")
    Call<List<Role>> getUserRoles();

    // Get all devices
    @Headers("Content-Type: application/json")
    @POST("api/master/asset/query")
    Call<List<Device>> getUserDevices();

    // Get all models
    @GET("api/master/model/assetInfos")
    Call<List<Model>> getDeviceModels();

    // Create device
    @POST("api/master/asset")
    Call<CreateAssetRes> createDevice(@Body JsonObject body);

    // Delete device
    @DELETE("api/master/asset")
    Call<String> delDevice(@Query("assetId") String deviceId);

    // Get a device
    @GET("api/master/asset/{assetId}")
    Call<Device> getDevice(@Path("assetId") String deviceId);

    // Update  a device
    @PUT("api/master/asset/{assetId}")
    Call<String> updateDeviceInfo(@Path("assetId") String deviceId, @Body JsonObject requestBody);

    @GET("api/master/user/master/roles")
    Call<List<Role>> getRoles();

    @GET("api/master/realm")
    Call<List<Realm>> getRealm();

    @PUT("api/master/user/master/roles")
    Call<String> updateRole(@Body JsonObject requestBody);


}
