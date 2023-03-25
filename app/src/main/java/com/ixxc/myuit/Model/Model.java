package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ixxc.myuit.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Model {
    @SerializedName("assetDescriptor")
    public JsonObject assetDescriptor;

    @SerializedName("attributeDescriptors")
    public ArrayList<Attribute> attributeDescriptors;

    @SerializedName("metaItemDescriptors")
    public ArrayList<String> metaItemDescriptors;

    @SerializedName("valueDescriptors")
    public ArrayList<String> valueDescriptors;

    private static List<Model> modelList;

    public static List<Model> getModelList() {
        return modelList;
    }

    public static void setModelList(List<Model> modelList) {
        Model.modelList = modelList;
    }

    public static Model getDeviceModel(String name) {
        List<Model> result = Model.getModelList().stream()
                .filter(item -> item.assetDescriptor.get("name").getAsString().equals(name))
                .collect(Collectors.toList());

        return result.get(0);
    }

    public List<Attribute> getOptional() {
        return attributeDescriptors.stream()
                .filter(item -> item.optional)
                .collect(Collectors.toList());
    }

    public Attribute getOptional(String name) {
        return getOptional().stream()
                .filter(a -> a.name.equals(name))
                .collect(Collectors.toList()).get(0);
    }
}
