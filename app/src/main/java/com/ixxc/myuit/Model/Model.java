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
}
