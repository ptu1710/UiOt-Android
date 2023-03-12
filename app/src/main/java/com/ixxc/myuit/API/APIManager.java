package com.ixxc.myuit.API;

import android.util.Log;

import com.google.gson.JsonObject;
import com.ixxc.myuit.GlobalVars;
import com.ixxc.myuit.Interface.APIInterface;
import com.ixxc.myuit.Model.CreateAssetReq;
import com.ixxc.myuit.Model.CreateAssetRes;
import com.ixxc.myuit.Model.Device;
import com.ixxc.myuit.Model.Model;
import com.ixxc.myuit.Model.Token;
import com.ixxc.myuit.Model.User;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class APIManager {
    private static final APIClient apiClient = new APIClient();
    private static final APIInterface publicAI = apiClient.getClient(true).create(APIInterface.class);
    private static APIInterface userAI;

    public static void getToken(boolean isPublic, String code) {
        Call<Token> call =  publicAI.getToken(GlobalVars.authType, code, GlobalVars.client, GlobalVars.redirectUrl);
        try {
            Response<Token> response = call.execute();
            if (response.isSuccessful()) {
                Token token = response.body();
                if (isPublic) {
                    apiClient.PublicToken = token.access_token;
                } else {
                    apiClient.UserToken = token.access_token;
                }
            }
            else { Log.d("API LOG", "getToken: Not Successful"); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean getUserInfo() {
        userAI = apiClient.getClient(false).create(APIInterface.class);
        Call<User> call = userAI.getUserInfo();

        boolean isSuccess = false;
        try {
            Response<User> response = call.execute();
            isSuccess = response.isSuccessful();
            User.setUser(response.body());
        } catch (IOException e) { e.printStackTrace(); }

        return isSuccess;
    }

    public static void getDevices() {
        Call<List<Device>> call = userAI.getUserDevices();
        try {
            Response<List<Device>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<Device> assets = response.body();
                Device.setDevicesList(assets);
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
}
