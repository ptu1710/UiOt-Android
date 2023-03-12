package com.ixxc.myuit.Model;

import android.text.InputType;

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

    public static int GetType(String type){
        switch (type.trim()){
            case "JSONObject":
            case "JSONArray":
            case "JSON":
            case "booleanMap":
            case "integerMap":
            case "numberMap":
            case "multivaluedTextMap":
                return InputType.TYPE_TEXT_FLAG_MULTI_LINE;
            case "timestamp":
            case "timestampISO8601":
            case "dateAndTime":
            case "timeDurationISO8601":
            case "periodDurationISO8601":
            case "timeAndPeriodDurationISO8601":
            case "integer":
            case "long":
            case "bigInteger":
            case "number":
            case "bigNumber":
            case "TCP_IPPortNumber":
            case "positiveInteger":
            case "positiveNumber":
            case "negativeInteger":
            case "negativeNumber":
            case "integerByte":
            case "byte":
                return InputType.TYPE_CLASS_NUMBER;
            case "attributeLink":
            case "agentLink":
            case "HTTP_URL":
            case "WS_URL":
                return InputType.TYPE_TEXT_VARIATION_URI;
            case "email":
                return InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
            default:
                return InputType.TYPE_CLASS_TEXT;
        }
    }
}
