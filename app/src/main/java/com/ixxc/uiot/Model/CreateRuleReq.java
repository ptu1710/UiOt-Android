package com.ixxc.uiot.Model;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.bindgen.Value;

import java.util.List;

public class CreateRuleReq {
    String ruleName, ruleAction,ruleAsset,value_then,attributeName_then;

    JsonObject recurrence;
    JsonObject attributeName;
    JsonObject attributeValue;

    public void setValue_then(String value_then) {
        this.value_then = value_then;
    }

    public void setAttributeName_then(String attributeName_then) {
        this.attributeName_then = attributeName_then;
    }

    JsonObject messageObj;
    JsonArray ruleTypes, deviceIds, targetIds;

    public void setRecurrence(JsonObject recurrence) {
        this.recurrence = recurrence;
    }

    public void setRuleAsset(String ruleAsset) {
        this.ruleAsset = ruleAsset;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleTypes(String types) {
        JsonArray ruleTypes = new JsonArray();
        ruleTypes.add(types);

        this.ruleTypes = ruleTypes;
    }

    public void setAttributeName(String value) {
        JsonObject attributeName = new JsonObject();
        attributeName.addProperty("predicateType", "string");
        attributeName.addProperty("match", "EXACT");
        attributeName.addProperty("value", value);

        this.attributeName = attributeName;
    }

    public void setAttributeValue(Integer predicateType, String valueObject, String value) {
        JsonObject attributeValue = new JsonObject();

        switch (predicateType) {
            case 2:
                attributeValue.addProperty("predicateType", "number");
                attributeValue.addProperty("value",Float.valueOf(value));
                break;
            default:
                attributeValue.addProperty("predicateType", "string");
                attributeValue.addProperty("value", value);

        }
        attributeValue.addProperty("match","EXACT");
        attributeValue.addProperty("negate", false);
        attributeValue.addProperty("operator", valueObject.toUpperCase());

        switch (valueObject){
            case "Is true":
            case "Is false":
                attributeValue.addProperty("predicateType","boolean");
                boolean bool = ((String) valueObject).contains("true");
                attributeValue.addProperty("value", bool);
                break;
            case "Has no value":
                attributeValue.addProperty("predicateType","value-empty");
                attributeValue.remove("value");
                break;
            case "Has a value":
                attributeValue.addProperty("predicateType","value-empty");
                attributeValue.addProperty("negate", true);
                attributeValue.remove("value");

        }


        this.attributeValue = attributeValue;
    }

    public void setDeviceIds(List<String> ids) {
        JsonArray deviceIds = new JsonArray();
        for (String deviceId : ids) {
            deviceIds.add(deviceId);
        }

        this.deviceIds = deviceIds;
    }

    public void setRuleAction(String ruleAction) {
        this.ruleAction = ruleAction;
    }

    public void setTargetIds(String ids) {
        JsonArray targetIds = new JsonArray();
        targetIds.add(ids);

        this.targetIds = targetIds;
    }

    public void setMessageObj(String type, String mess){
        JsonObject message = new JsonObject();
        if(type.equals("Email")){
            message.addProperty("type", "email");
            message.addProperty("subject", "Subject Email");
            message.addProperty("html", mess);
        } else if (type.equals("Push Notification")) {
            JsonObject action = new JsonObject();
            action.addProperty("openInBrowser", true);
            action.addProperty("url", "website URL");

            JsonObject action_btn1 = action;
            action_btn1.remove("openInBrowser");

            JsonObject buttons_1= new JsonObject();
            buttons_1.add("action",action_btn1);
            buttons_1.addProperty("title","action button");

            JsonObject buttons_2= new JsonObject();
            buttons_2.addProperty("title","decline button");

            JsonArray buttons = new JsonArray();
            buttons.add(buttons_1);
            buttons.add(buttons_2);

            message.addProperty("type", "push");
            message.addProperty("title", "Title Notification");
            message.addProperty("body", mess);
            message.add("action",action);
            message.add("buttons",buttons);

        }

        this.messageObj = message;
    }

    public Boolean isNumeric(String str) {
        Log.d("AAA", "isNumeric: " + str);
        if (str == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public JsonObject toJson() {
        // level 8
        JsonObject attributes = new JsonObject();
        JsonArray attributeItems = new JsonArray();

        JsonObject attributeItem = new JsonObject();
        attributeItem.add("name", attributeName);
        attributeItem.add("value", attributeValue);

        attributeItems.add(attributeItem);
        attributes.add("items", attributeItems);

//        JsonObject action = new JsonObject();
//        action.addProperty("openInBrowser", true);

        // level 7
        JsonObject assets = new JsonObject();
        assets.add("types", ruleTypes);
        assets.add("attributes", attributes);
        if(deviceIds != null) assets.add("ids", deviceIds);


        JsonObject users = new JsonObject();
        users.add("ids", targetIds);

        JsonArray types = new JsonArray();
        types.add(ruleAsset);

        JsonObject matchedAssets = new JsonObject();
        matchedAssets.add("types",types);

        // level 6
        JsonArray groupItems = new JsonArray();
        JsonObject groupItem = new JsonObject();
        groupItem.add("assets", assets);
        groupItems.add(groupItem);

        JsonObject target = new JsonObject();
        target.add("users", users);
        target.add("matchedAssets",matchedAssets);

        JsonObject notification = new JsonObject();
        notification.add("message", messageObj);

        // level 5
        JsonObject group = new JsonObject();
        group.addProperty("operator", "AND");
        group.add("items", groupItems);

        // level 4
        JsonArray groups = new JsonArray();
        groups.add(group);

        // level 3
        JsonObject when = new JsonObject();
        when.addProperty("operator", "OR");
        when.add("groups", groups);

        JsonArray then = new JsonArray();
        JsonObject thenObject = new JsonObject();
        thenObject.addProperty("action", ruleAction);
        thenObject.add("target", target);
        thenObject.add("notification",notification);
        thenObject.addProperty("value",value_then);

        try {
            if(isNumeric(value_then)){
                thenObject.addProperty("value", Double.parseDouble(value_then));
            } else if (value_then.contains("true") || value_then.contains("false")) {
                thenObject.addProperty("value",Boolean.parseBoolean(value_then));
            }
        }
        catch (Exception e){}

        thenObject.addProperty("attributeName",attributeName_then);


        then.add(thenObject);

        // level 2
        JsonObject rule = new JsonObject();
        rule.add("recurrence", recurrence);
        rule.add("when", when);
        rule.add("then", then);
        rule.addProperty("name", ruleName);

        // level 1
        JsonArray rules = new JsonArray();
        rules.add(rule);

        // max
        JsonObject returnJson = new JsonObject();
        returnJson.add("rules", rules);

        return returnJson;
    }
}
