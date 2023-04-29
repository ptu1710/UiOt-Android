package com.ixxc.uiot.Interface;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.uiot.Model.CreateAssetRes;
import com.ixxc.uiot.Model.DataPoint;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.LinkedDevice;
import com.ixxc.uiot.Model.Map;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.Model.Model;
import com.ixxc.uiot.Model.Realm;
import com.ixxc.uiot.Model.RegisterDevice;
import com.ixxc.uiot.Model.Role;
import com.ixxc.uiot.Model.Token;
import com.ixxc.uiot.Model.User;

import java.util.List;

import okhttp3.ResponseBody;
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
    Call<List<Device>> queryDevices(@Body JsonObject body);

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

    // Query all users
    @Headers("Content-Type: application/json")
    @POST("api/master/user/query")
    Call<List<User>> queryUsers(@Body JsonObject body);

    @GET("api/master/user/master/roles")
    Call<List<Role>> getRoles();

    @GET("api/master/user/master/userRoles/{userId}")
    Call<List<Role>> getRoles(@Path("userId") String userId);

    @Headers("Content-Type: application/json")
    @PUT("api/master/user/master/userRoles/{userId}")
    Call<String> setRoles(@Path("userId") String userId, @Body JsonArray body);

    @GET("api/master/realm")
    Call<List<Realm>> getRealm();

    @PUT("api/master/user/master/roles")
    Call<String> updateRole(@Body JsonArray requestBody);

    // Get a user by id
    @GET("api/master/user/master/userRealmRoles/{userId}")
    Call<List<Role>> getRealmRoles(@Path("userId") String userId);

    // Update realm roles
    @Headers("Content-Type: application/json")
    @PUT("api/master/user/master/userRealmRoles/{userId}")
    Call<String> setRealmRoles(@Path("userId") String userId, @Body JsonArray body);

    // Get a user by id
    @GET("api/master/user/master/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    // Query linked device(s)
    @GET("api/master/asset/user/link")
    Call<List<LinkedDevice>> getLinkedDevices(@Query("realm") String realm, @Query("userId") String userId);

    // Set link device(s)
    @Headers("Content-Type: application/json")
    @POST("api/master/asset/user/link")
    Call<String> setLinkedDevices(@Body JsonArray body);

    // Delete linked device(s)
    @Headers("Content-Type: application/json")
    @POST("api/master/asset/user/link/delete")
    Call<String> setUnlinkedDevices(@Body JsonArray body);

    // Delete User
    @DELETE("api/master/user/master/users/{userId}")
    Call<String> deleteUser(@Path("userId") String userId);

    // Update user's info
    @Headers("Content-Type: application/json")
    @PUT("api/master/user/master/users")
    Call<User> updateUserInfo(@Body JsonObject body);

    // Update user's password
    @Headers("Content-Type: application/json")
    @PUT("api/master/user/master/reset-password/{userId}")
    Call<String> updatePassword(@Path("userId") String userId, @Body JsonObject body);

    // Update secret for service user
    @GET("api/master/user/master/reset-secret/{userId}")
    Call<ResponseBody> getNewSecret(@Path("userId") String userId);

    //Create a realm
    @POST("api/master/realm")
    Call<String> createRealm(@Body JsonObject body);

    // Update a realm
    @Headers("Content-Type: application/json")
    @PUT("api/master/realm/{realmName}")
    Call<String> updateRealm(@Path("realmName") String name, @Body JsonObject body);

    // Delete Realm
    @DELETE("api/master/realm/{realmName}")
    Call<String> deleteRealm(@Path("realmName") String name);

    // Get metaItemDescriptors
    @GET("api/master/model/metaItemDescriptors")
    Call<List<MetaItem>> getMetaItem(@Query("parentId") String parentId);

    // Get Maps data
    @GET("api/master/map")
    Call<Map> getMap();

    // Register a new device (push notification)
    @POST("api/master/console/register")
    Call<RegisterDevice> registerDevice(@Body JsonObject body);

    // Get Data point
    @GET("api/master/asset/datapoint/{assetId}/attribute/{attributeName}")
    Call<List<DataPoint>> getDataPoint(@Path("assetId") String assetId, @Path("attributeName") String attributeName,
                                 @Query("interval") String interval, @Query("fromTimestamp") Long from, @Query("toTimestamp") Long to);
}
