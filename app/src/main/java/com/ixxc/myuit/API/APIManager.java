package com.ixxc.myuit.API;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.Interface.APIInterface;
import com.ixxc.myuit.Model.CreateAssetRes;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.LinkedDevice;
import com.ixxc.myuit.Model.Model;
import com.ixxc.myuit.Model.Realm;
import com.ixxc.myuit.Model.Role;
import com.ixxc.myuit.Model.Token;
import com.ixxc.myuit.Model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;

public class APIManager {
    private static final APIClient apiClient = new APIClient();
    private static final APIInterface userAI = apiClient.getClient().create(APIInterface.class);;

    public static void getToken(String code) {
        Call<Token> call =  userAI.getToken(GlobalVars.authType, code, GlobalVars.client, GlobalVars.redirectUrl);
        try {
            Response<Token> response = call.execute();
            if (response.isSuccessful()) {
                Token token = response.body();
                apiClient.UserToken = token.access_token;
            }
            else { Log.d("API LOG", "getToken: Not Successful"); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getUserInfo() {
        Call<User> call = userAI.getUserInfo();

        boolean isSuccess = false;
        try {
            Response<User> response = call.execute();
            isSuccess = response.isSuccessful();
            User.setMe(response.body());
        } catch (IOException e) { e.printStackTrace(); }

        return isSuccess;
    }

    public static boolean getUserRoles() {
        Call<List<Role>> call = userAI.getUserRoles();

        boolean isSuccess = false;
        try {
            Response<List<Role>> response = call.execute();
            isSuccess = response.isSuccessful();
            User.getMe().setUserRoles(response.body());
        } catch (IOException e) { e.printStackTrace(); }

        return isSuccess;
    }

    public static void queryDevices(JsonObject body) {
        Call<List<Device>> call = userAI.queryDevices(body);
        try {
            Response<List<Device>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<Device> deviceList = response.body();
                Device.setDevicesList(deviceList);
            } else {
                Device.setDevicesList(null);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<Model> getDeviceModels() {
        Call<List<Model>> call = userAI.getDeviceModels();

        List<Model> models = null;
        try {
            Response<List<Model>> response = call.execute();
            if (response.isSuccessful()) {
                models = response.body();
            }
        } catch (IOException e) { e.printStackTrace(); }

        return models;
    }

    public static boolean delDevice(String deviceId){
        Call<String> call = userAI.delDevice(deviceId);
        try {
            Response<String> response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean updateDeviceInfo(String deviceId, JsonObject requestBody){
        Call<String> call = userAI.updateDeviceInfo(deviceId, requestBody);
        try {
            Response<String> response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Device getDevice(String deviceId){
        Call<Device> call = userAI.getDevice(deviceId);
        Device device = new Device();
        try {
            Response<Device> response = call.execute();
            if (response.isSuccessful()) {
                device = response.body();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void createDevice(JsonObject reqBody) {
        Call<CreateAssetRes> call = userAI.createDevice(reqBody);

        try {
            Response<CreateAssetRes> response = call.execute();
            if (response.isSuccessful()) {
                CreateAssetRes res = response.body();
                Log.d("API LOG", res.name);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static int queryUsers(JsonObject query) {
        Call<List<User>> call = userAI.queryUsers(query);

        int returnCode = 200;
        try {
            Response<List<User>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<User> users = response.body();
                User.setUsersList(users);
            } else {
                returnCode = response.code();
                Log.d(GlobalVars.LOG_TAG, "queryUsers: code "+ returnCode);
                User.setUsersList(null);
            }
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public static User getUser(String userId){
        Call<User> call = userAI.getUser(userId);
        User user = new User();
        try {
            Response<User> response = call.execute();
            if (response.isSuccessful()) {
                user = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void getRealm(){
        Call<List<Realm>> call = userAI.getRealm();
        List<Realm> realms = null;
        try {
            Response<List<Realm>> response = call.execute();
            if(response.isSuccessful()){
                Realm.setRealmList(response.body());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getRoles(){
        Call<List<Role>> call = userAI.getRoles();

        try {
            Response<List<Role>> response = call.execute();
            if(response.isSuccessful()) Role.setRoleList(response.body(), false);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static List<Role> getRoles(String userId){
        Call<List<Role>> call = userAI.getRoles(userId);

        List<Role> roles = new ArrayList<>();
        try {
            Response<List<Role>> response = call.execute();
            if(response.isSuccessful()){
                roles = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return roles;
    }

    public static void setRoles(String userId, JsonArray body){
        Call<String> call = userAI.setRoles(userId, body);

        try {
            Response<String> response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void getRealmRoles(){
        Call<List<Role>> call = userAI.getRealmRoles();

        try {
            Response<List<Role>> response = call.execute();
            if(response.isSuccessful()){
                Role.setRoleList(response.body(), true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Role> getRealmRoles(String userId){
        Call<List<Role>> call = userAI.getRealmRoles(userId);

        List<Role> roles = new ArrayList<>();
        try {
            Response<List<Role>> response = call.execute();
            if(response.isSuccessful()){
                roles = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return roles;
    }

    public static void setRealmRoles(String userId, JsonArray body){
        Call<String> call = userAI.setRealmRoles(userId, body);

        try {
            Response<String> response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static int updateRole(JsonArray requestBody){
        Call<String> call = userAI.updateRole(requestBody);

        int code = -1;
        try {
            Response<String> response = call.execute();
            code = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }
    
    public static List<LinkedDevice> getLinkedDevices(String userId){
        Call<List<LinkedDevice>> call = userAI.getLinkedDevices("master", userId);

        List<LinkedDevice> devices = new ArrayList<>();
        try {
            Response<List<LinkedDevice>> response = call.execute();
            if (response.isSuccessful()) {
                devices = response.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return devices;
    }

    public static int setLinkedDevices(JsonArray body){
        Call<String> call = userAI.setLinkedDevices(body);

        int statusCode = -1;
        try {
            Response<String> response = call.execute();
            statusCode = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return statusCode;
    }

    public static int setUnlinkedDevices(JsonArray body){
        Call<String> call = userAI.setUnlinkedDevices(body);

        int statusCode = -1;
        try {
            Response<String> response = call.execute();
            statusCode = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return statusCode;
    }

    public static int updateUserInfo(JsonObject query) {
        Call<User> call = userAI.updateUserInfo(query);

        int returnCode = -1;
        try {
            Response<User> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public static int updatePassword(String id, JsonObject query) {
        Call<String> call = userAI.updatePassword(id, query);

        int returnCode = -1;
        try {
            Response<String> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public static int createRealm(JsonObject body){
        Call<String> call = userAI.createRealm(body);

        int returnCode = -1;
        try {
            Response<String> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  returnCode;


    }
}
