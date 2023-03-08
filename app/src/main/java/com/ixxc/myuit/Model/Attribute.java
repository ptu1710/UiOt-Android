package com.ixxc.myuit.Model;

import androidx.annotation.Nullable;

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

    @SerializedName("meta")
    public JsonObject meta;

    @SerializedName("optional")
    public boolean optional;

    @Nullable
    @SerializedName("value")
    public String value;

    @SerializedName("timestamp")
    public long timestamp;
}
