package com.ixxc.uiot.Model;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RuleValue {
    public String predicateType;
    public Boolean negate;
    public String operator;
    public String value;
    public String rangeValue;
    public String match;
    public String ids;

    public String types;
    public String attribute;

    public String types_then;
    public String attribute_then;
    public String ids_then;
    public String value_then;
    public String action_then;
    public String notification_type;
    public String message_body;






    public RuleValue(String rules) {
        JsonObject jsonObject = JsonParser.parseString(rules).getAsJsonObject();

        JsonObject items = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("when").getAsJsonObject().get("groups").getAsJsonArray()
                .get(0).getAsJsonObject().get("items").getAsJsonArray()
                .get(0).getAsJsonObject().get("assets").getAsJsonObject().get("attributes").getAsJsonObject()
                .get("items").getAsJsonArray().get(0).getAsJsonObject();
        try {
            this.predicateType = items.get("value").getAsJsonObject().get("predicateType").getAsString();
        }
        catch (Exception e){}

        try {
            this.operator = items.get("value").getAsJsonObject().get("operator").getAsString();
        }
        catch (Exception e){}

        try {
            this.negate = Boolean.valueOf(items.get("value").getAsJsonObject().get("negate").getAsString());
        }
        catch (Exception e){}

        try {
            this.value = items.get("value").getAsJsonObject().get("value").getAsString();
        }
        catch (Exception e){}

        try {
            this.rangeValue = items.get("value").getAsJsonObject().get("rangeValue").getAsString();
        }
        catch (Exception e){}

        try {
            this.match = items.get("value").getAsJsonObject().get("match").getAsString();
        }
        catch (Exception e){}

        try {
            this.types = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("when").getAsJsonObject().get("groups").getAsJsonArray().get(0).getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("assets").getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString();
        }
        catch (Exception e){}

        try {
            this.attribute = items.get("name").getAsJsonObject().get("value").getAsString();
        }
        catch (Exception e){}

        try {
            this.ids = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("when").getAsJsonObject().get("groups").getAsJsonArray()
                    .get(0).getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject().get("assets").getAsJsonObject().get("ids").getAsJsonArray().get(0).getAsString();
        }
        catch (Exception e){}

        /// Then
        JsonObject target = new JsonObject();
        try {
             target = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray().get(0).getAsJsonObject().get("target").getAsJsonObject();
        }catch (Exception e){}

        try {
            this.types_then = target.get("assets").getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString();
            this.ids_then = target.get("assets").getAsJsonObject().get("ids").getAsJsonArray().get(0).getAsString();
        }
        catch (Exception e){}

        try {
            this.types_then = target.get("matchedAssets").getAsJsonObject().get("types").getAsJsonArray().get(0).getAsString();
        }catch (Exception e){}

        try {
            this.attribute_then = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray()
                    .get(0).getAsJsonObject().get("attributeName").getAsString();
        }catch (Exception e){}

        try {
            this.value_then = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray()
                    .get(0).getAsJsonObject().get("value").getAsString();
        }catch (Exception e){}

        try {
            this.action_then = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray()
                    .get(0).getAsJsonObject().get("action").getAsString();
        }catch (Exception e){}

        try {
            this.notification_type = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray()
                    .get(0).getAsJsonObject().get("notification").getAsJsonObject().get("message").getAsJsonObject().get("type").getAsString();
        }catch (Exception e){}

        try {
            this.message_body = jsonObject.get("rules").getAsJsonArray().get(0).getAsJsonObject().get("then").getAsJsonArray()
                    .get(0).getAsJsonObject().get("notification").getAsJsonObject().get("message").getAsJsonObject().get("body").getAsString();
        }catch (Exception e){}


    }

}
