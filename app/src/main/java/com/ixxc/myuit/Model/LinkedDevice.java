package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class LinkedDevice {
    public JsonObject id;
    public long createdOn;
    public String assetName;
    public String parentAssetName = "";
    public String userFullName;

    public static LinkedDevice LinkDevice(User user, Device device) {
        LinkedDevice returnDevice = new LinkedDevice();

        JsonObject id = new JsonObject();
        id.addProperty("realm", device.realm);
        id.addProperty("userId", user.id);
        id.addProperty("assetId", device.id);

        returnDevice.id = id;
        returnDevice.createdOn = device.createdOn;
        returnDevice.assetName = device.name;
        returnDevice.userFullName = user.username + " (" + user.firstName + " " + user.lastName + ")";

        return returnDevice;
    }
}
