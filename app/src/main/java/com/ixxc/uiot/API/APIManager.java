package com.ixxc.uiot.API;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ixxc.uiot.Interface.APIInterface;
import com.ixxc.uiot.Model.CreateDeviceRes;
import com.ixxc.uiot.Model.Device;
import com.ixxc.uiot.Model.DeviceModel;
import com.ixxc.uiot.Model.LinkedDevice;
import com.ixxc.uiot.Model.Map;
import com.ixxc.uiot.Model.MetaItem;
import com.ixxc.uiot.Model.Realm;
import com.ixxc.uiot.Model.RegisterDevice;
import com.ixxc.uiot.Model.Role;
import com.ixxc.uiot.Model.Rule;
import com.ixxc.uiot.Model.Token;
import com.ixxc.uiot.Model.User;
import com.ixxc.uiot.Utils.Util;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Path;

public class APIManager {
    APIInterface apiInterface = new APIClient().getClient().create(APIInterface.class);

    public void getUserToken(String username, String password) {
        String client = "openremote";
        String authType = "password";

        Call<Token> call =  apiInterface.getUserToken(authType, client, username, password);
        try {
            Response<Token> response = call.execute();
            if (response.isSuccessful()) {
                Token token = response.body();
                assert token != null;
                APIClient.userToken = TextUtils.isEmpty(token.access_token) ? "" : token.access_token;
            } else {
                Log.d("API LOG", "getUserToken: Not successful, code = " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getUserInfo() {
        Call<User> call = apiInterface.getUserInfo();

        try {
            Response<User> response = call.execute();
            User.setMe(response.body());
        } catch (IOException e) { e.printStackTrace(); }

    }

    public void getUserRoles() {
        Call<List<Role>> call = apiInterface.getUserRoles();

        try {
            Response<List<Role>> response = call.execute();
            if (response.body() != null && response.isSuccessful()) {
                User.getMe().setUserRoles(response.body());
            }
        } catch (IOException e) { e.printStackTrace(); }

    }

    public void getDeviceModels() {
        Call<List<DeviceModel>> call = apiInterface.getDeviceModels();

        try {
            Response<List<DeviceModel>> response = call.execute();
            if (response.isSuccessful()) {
                DeviceModel.setModelList(response.body());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void queryDevices(JsonObject body) {
        Call<List<Device>> call = apiInterface.queryDevices(body);
        try {
            Response<List<Device>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<Device> deviceList = response.body();
                Device.setDeviceList(deviceList);
            }

            Device.devicesLoaded = true;

        } catch (IOException e) { e.printStackTrace(); }
    }

    public boolean delDevice(String deviceId){
        Call<String> call = apiInterface.delDevice(deviceId);
        try {
            Response<String> response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateDevice(String deviceId, JsonObject requestBody){

        Call<String> call = apiInterface.updateDevice(deviceId, requestBody);
        try {
            Response<String> response = call.execute();
            return response.isSuccessful();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Device getDevice(String deviceId){
        Call<Device> call = apiInterface.getDevice(deviceId);
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

    public void createDevice(JsonObject reqBody) {
        Call<CreateDeviceRes> call = apiInterface.createDevice(reqBody);

        try {
            Response<CreateDeviceRes> response = call.execute();
            if (response.isSuccessful()) {
                CreateDeviceRes res = response.body();
                assert res != null;
                Log.d("API LOG", res.name);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public int queryUsers(JsonObject query) {
        Call<List<User>> call = apiInterface.queryUsers(query);

        int returnCode = 200;
        try {
            Response<List<User>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                List<User> users = response.body();
                User.setUsersList(users);
            } else {
                returnCode = response.code();
                Log.d(Util.LOG_TAG, "queryUsers: code "+ returnCode);
                User.setUsersList(null);
            }
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public User getUser(String userId){
        Call<User> call = apiInterface.getUser(userId);
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

    public void getRealm(){
        Call<List<Realm>> call = apiInterface.getRealm();

        try {
            Response<List<Realm>> response = call.execute();
            if(response.isSuccessful()){
                Realm.setRealmList(response.body());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void getRoles(){
        Call<List<Role>> call = apiInterface.getRoles();

        try {
            Response<List<Role>> response = call.execute();
            if(response.isSuccessful() && response.body() != null) Role.setRoleList(response.body(), false);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public List<Role> getRoles(String userId){
        Call<List<Role>> call = apiInterface.getRoles(userId);

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

    public void setRoles(String userId, JsonArray body){
        Call<String> call = apiInterface.setRoles(userId, body);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<Role> getRealmRoles(String userId){
        Call<List<Role>> call = apiInterface.getRealmRoles(userId);

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

    public void setRealmRoles(String userId, JsonArray body){
        Call<String> call = apiInterface.setRealmRoles(userId, body);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int updateRole(JsonArray requestBody){
        Call<String> call = apiInterface.updateRole(requestBody);

        int code = -1;
        try {
            Response<String> response = call.execute();
            code = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }
    
    public List<LinkedDevice> getLinkedDevices(String userId){
        Call<List<LinkedDevice>> call = apiInterface.getLinkedDevices("master", userId);

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

    public void setLinkedDevices(JsonArray body){
        Call<String> call = apiInterface.setLinkedDevices(body);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUnlinkedDevices(JsonArray body){
        Call<String> call = apiInterface.setUnlinkedDevices(body);

        try {
            call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public int deleteUser(String id) {
        Call<String> call = apiInterface.deleteUser(id);

        int returnCode = -1;
        try {
            Response<String> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public int updateUserInfo(JsonObject query) {
        Call<User> call = apiInterface.updateUserInfo(query);

        int returnCode = -1;
        try {
            Response<User> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return returnCode;
    }

    public void updatePassword(String id, JsonObject query) {
        Call<String> call = apiInterface.updatePassword(id, query);

        try {
            call.execute();
        } catch (IOException e) { e.printStackTrace(); }

    }

    public String getNewSecret(String userId){
        Call<ResponseBody> call = apiInterface.getNewSecret(userId);

        try {
            Response<ResponseBody> response = call.execute();
            if(response.code() == 200 && response.body() != null){
                return response.body().string();
            }
        } catch (IOException e) { e.printStackTrace(); }

        return "";
    }

    public int createRealm(JsonObject body){
        Call<String> call = apiInterface.createRealm(body);

        int returnCode = -1;
        try {
            Response<String> response = call.execute();
            returnCode = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return  returnCode;
    }

    public int updateRealm(String name, JsonObject body){
        Call<String> call = apiInterface.updateRealm(name, body);

        int code = -1;
        try {
            Response<String> response = call.execute();
            code = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return code;
    }

    public int deleteRealm(String realmName){
        Call<String> call = apiInterface.deleteRealm(realmName);

        int code = -1;
        try {
            Response<String> response = call.execute();
            code = response.code();
        } catch (IOException e) { e.printStackTrace(); }

        return code;
    }

    public void getMetaItem(String parentId){
        Call<JsonObject> call = apiInterface.getMetaItem(parentId);

        try {
            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) MetaItem.setMetaItemList(response.body());
        } catch (IOException e) { e.printStackTrace(); }

    }

    public void getMap() {
        Call<Map> call = apiInterface.getMap();
        try {
            Response<Map> response = call.execute();
            if (response.isSuccessful()) { Map.setMapObj(response.body()); }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void registerDevice(String token){

        // TODO: Store device id in shared preferences
        try {
            String id = "4HlJmMDuAasniaKDhyz0ry";

            if (!id.isEmpty()) {
                RegisterDevice register = new RegisterDevice(id, "Android Browser");

                JsonObject body = register.toJson(token);

                Call<RegisterDevice> call2 = apiInterface.registerDevice(body);
                call2.execute();

            } else {
                RegisterDevice register1 = new RegisterDevice("Android Browser");

                Call<RegisterDevice> call1 = apiInterface.registerDevice(register1.toJson(""));

                Response<RegisterDevice> response1 = call1.execute();

                if (response1.code() == 200) {

                    RegisterDevice registerResponse = response1.body();
                    assert registerResponse != null;

                    RegisterDevice register2 = new RegisterDevice(registerResponse.getId(), registerResponse.getName());

                    JsonObject body2 = register2.toJson(token);

                    Log.d(Util.LOG_TAG, "register2: " + body2);

                    Call<RegisterDevice> call2 = apiInterface.registerDevice(body2);
                    call2.execute();

                } else {
                    Log.d(Util.LOG_TAG, "Error: " + response1.message());
                }
            }

        } catch (IOException e) { e.printStackTrace(); }
    }

    public JsonArray getDatapoint(String assetId, String attributeName, JsonObject body){
        Call<JsonArray> call = apiInterface.getDataPoint(assetId, attributeName, body);

        try {
            Response<JsonArray> response = call.execute();
            if (response.isSuccessful()) return response.body();
        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    public int createRule(JsonObject body){
        Call<Integer> call = apiInterface.createRule(body);

        try {
            Response<Integer> response = call.execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body();
            }
        } catch (IOException e) { e.printStackTrace(); }

        return -1;
    }

    public List<Rule> queryRules() {
        Call<List<Rule>> call = apiInterface.queryRules();
        try {
            Response<List<Rule>> response = call.execute();
            if (response.isSuccessful() && response.code() == 200) {
                return response.body();
            }

        } catch (IOException e) { e.printStackTrace(); }

        return new ArrayList<>();
    }

    public JsonObject uploadImage(@Body RequestBody image){
        Call<JsonObject> call = apiInterface.uploadImage(image);

        try {
            Response<JsonObject> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) { e.printStackTrace(); }

        return null;
    }

    public String getPredictedRain(@Path("assetId") String assetId){
        // Get UTC time in ISO 8601 format
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        String formattedUtcTime = utc.format(DateTimeFormatter.ISO_INSTANT);
        System.out.println(formattedUtcTime);

        // {
        //  "fromTimestamp": 0,
        //  "toTimestamp": 0,
        //  "fromTime": "2023-12-13T10:55:37.466Z",
        //  "toTime": "2023-12-13T10:55:37.466Z",
        //  "type": "string"
        //}

        JsonObject body = new JsonObject();
        body.addProperty("fromTimestamp", 0);
        body.addProperty("toTimestamp", 0);
        body.addProperty("fromTime", formattedUtcTime);
        body.addProperty("toTime", formattedUtcTime);
        body.addProperty("type", "string");

        Call<String> call = apiInterface.getPredictedRain(assetId, body);

        /*try {
            Response<String> response = call.execute();
            if (response.isSuccessful()) {
                return response.body();
            }
        } catch (IOException e) { e.printStackTrace(); }*/

        return "0";
    }
}
