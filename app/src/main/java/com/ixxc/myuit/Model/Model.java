package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ixxc.myuit.R;

import java.util.ArrayList;
import java.util.List;

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
}
