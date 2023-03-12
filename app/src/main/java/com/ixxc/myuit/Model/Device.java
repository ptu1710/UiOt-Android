package com.ixxc.myuit.Model;

import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ixxc.myuit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Device {
    @SerializedName("id")
    public String id;
    @SerializedName("version")
    public String version;
    @SerializedName("createdOn")
    public long createdOn;
    @SerializedName("name")
    public String name;
    @SerializedName("accessPublicRead")
    public Boolean accessPublicRead;
    @SerializedName("realm")
    public String realm;
    @SerializedName("type")
    public String type;
    @SerializedName("attributes")
    public JsonObject attributes;

    @SerializedName("path")
    public ArrayList<String> path;

    private static List<Device> deviceList;

    public static List<Device> getAllDevices() {
        return deviceList;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        if (deviceList != null) {
            deviceList.clear();
        } else {
            deviceList = new ArrayList<>();
        }

        if (list.get(0).type.equals("GroupAsset") && list.get(0).name.equals("Consoles")) list.remove(0);

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                deviceList.add(device);
            }
        }
    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceList) {
            if (Objects.equals(device.id, id)) {
                return device;
            }
        }
        return null;
    }

    public List<JsonObject> getDeviceAttribute() {
        List<JsonObject> attributeList = new ArrayList<>();

        for (String key : attributes.keySet()) {
            JsonObject attribute = attributes.get(key).getAsJsonObject();
            attributeList.add(attribute);
        }

        return attributeList;
    }

    public String getParentId() {
        String parentId = "";

        for (String path : path) {
            if (!path.equals(id)) {
                parentId = path;
                break;
            }
        }

        return parentId;
    }
}
