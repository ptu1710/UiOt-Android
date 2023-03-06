package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
    @SerializedName("name")
    public String name;

    @SerializedName("type")
    public String type;

    @SerializedName("format")
    public JsonObject format;

    @SerializedName("optional")
    public boolean optional;
}
