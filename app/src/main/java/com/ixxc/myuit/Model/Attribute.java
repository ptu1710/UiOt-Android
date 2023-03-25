package com.ixxc.myuit.Model;

import android.text.InputType;

import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

    @SerializedName("value")
    public JsonElement value = new JsonParser().parse("");;

    public Attribute(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @SerializedName("timestamp")
    public long timestamp;

    public int getValueType() {
        if (value.isJsonNull()) return 0;
        else if (value.isJsonObject()) return 1;
        else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isNumber()) return 2;
        else return -1;
    }

    public static int GetType(String type){
        switch (type.trim()){
            case "JSONObject":
            case "JSONArray":
            case "GEO_JSONPoint":
            case "JSON":
            case "booleanMap":
            case "integerMap":
            case "numberMap":
            case "multivaluedTextMap":
                return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
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

    public static String formatJsonValue(String text){
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    if (i != 0) {
                        json.append("\n");
                    }
                    json.append(indentString).append(letter).append("\n");
                    indentString = indentString + "\t\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t\t", "");
                    json.append("\n").append(indentString).append(letter);
                    break;
                case ',':
                    json.append(letter).append("\n").append(indentString);
                    break;
                default:
                    json.append(letter);
                    break;
            }
        }

        return json.toString();
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("type", type);
        o.addProperty("name", name);
        o.addProperty("timestamp", timestamp);
        o.add("value", value);
        o.add("meta", meta);

        return o;
    }
}
