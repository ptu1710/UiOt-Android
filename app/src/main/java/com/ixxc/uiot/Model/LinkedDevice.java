package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

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

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.add("id", id);
        o.addProperty("createdOn", createdOn);
        o.addProperty("assetName", assetName);
        if (!parentAssetName.equals("")) {
            o.addProperty("parentAssetName", parentAssetName);
        }
        o.addProperty("userFullName", userFullName);

        return o;
    }
}
