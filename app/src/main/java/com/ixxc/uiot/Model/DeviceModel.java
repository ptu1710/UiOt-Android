package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceModel {
    @SerializedName("assetDescriptor")
    public JsonObject assetDescriptor;

    @SerializedName("attributeDescriptors")
    public ArrayList<Attribute> attributeDescriptors;

    @SerializedName("metaItemDescriptors")
    public ArrayList<String> metaItemDescriptors;

    @SerializedName("valueDescriptors")
    public ArrayList<String> valueDescriptors;

    private static List<DeviceModel> modelList = new ArrayList<>();

    public static List<DeviceModel> getModelList() {
        return modelList;
    }

    public static void setModelList(List<DeviceModel> modelList) {
        DeviceModel.modelList = modelList;
    }

    public static DeviceModel getDeviceModel(String name) {
        return  DeviceModel.getModelList().stream()
                .filter(item -> item.assetDescriptor.get("name").getAsString().equals(name))
                .findFirst().orElse(null);
    }

    public List<Attribute> getOptional() {
        return attributeDescriptors.stream()
                .filter(Attribute::isOptional)
                .collect(Collectors.toList());
    }

    public Attribute getAttributeModel(String name) {
        return attributeDescriptors.stream()
                .filter(a -> a.getName().equals(name))
                .findFirst().orElse(null);
    }
}
