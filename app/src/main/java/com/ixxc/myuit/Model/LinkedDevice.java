package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class LinkedDevice {
    public JsonObject id;
    public long createdOn;
    public String assetName;
    public String parentAssetName;
    public String userFullName;

    private static List<LinkedDevice> linkedDeviceList;

    public static List<LinkedDevice> getLinkedDeviceList() {
        return linkedDeviceList;
    }

    public static void setLinkedDeviceList(List<LinkedDevice> linkedDevices) {
        linkedDeviceList = linkedDevices;
    }
}
