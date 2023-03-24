package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    private static final List<Device> deviceListFiltered = new ArrayList<>();

    public static List<Device> getAllDevices() {
        return deviceListFiltered;
    }

    public static void setDevicesList(List<Device> list) {
        if (list == null) return;

        deviceListFiltered.clear();
        if (list.get(0).type.equals("GroupAsset") && list.get(0).name.equals("Consoles")) list.remove(0);

        for (Device device : list) {
            if (!device.type.contains("ConsoleAsset")) {
                deviceListFiltered.add(device);
            }
        }
    }

    public static Device getDeviceById(String id) {
        for (Device device : deviceListFiltered) {
            if (Objects.equals(device.id, id)) {
                return device;
            }
        }
        return null;
    }

    public static List<String> getDeviceNames() {
        return deviceListFiltered.stream().map(d -> d.name + "(" + d.id + ")").collect(Collectors.toList());
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
