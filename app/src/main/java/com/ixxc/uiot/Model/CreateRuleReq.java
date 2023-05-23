package com.ixxc.uiot.Model;

import com.google.errorprone.annotations.Var;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mapbox.bindgen.Value;

import java.util.List;

public class CreateRuleReq {
    String ruleName, ruleAction;

    JsonObject recurrence, attributeName, attributeValue;
    JsonArray ruleTypes, deviceIds, targetIds;

    public void setRecurrence(JsonObject recurrence) {
        this.recurrence = recurrence;
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
                attributeValue.addProperty("predicateType", "text");
                attributeValue.addProperty("value", valueObject);

        }

        switch (valueObject){
            case "Is true":
            case "Is false":
                boolean bool = ((String) valueObject).contains("true");
                attributeValue.addProperty("value", bool);
                break;
            case "Has no value":
            case "Has a value":
                attributeValue.remove("value");

        }

        attributeValue.addProperty("negate", false);
        attributeValue.addProperty("operator", valueObject.toUpperCase());

        if(value.equals("null")){
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

    public JsonObject toJson() {
        // level 8
        JsonObject attributes = new JsonObject();
        JsonArray attributeItems = new JsonArray();

        JsonObject attributeItem = new JsonObject();
        attributeItem.add("name", attributeName);
        attributeItem.add("value", attributeValue);

        attributeItems.add(attributeItem);
        attributes.add("items", attributeItems);

        JsonObject action = new JsonObject();
        action.addProperty("openInBrowser", true);

        // level 7
        JsonObject assets = new JsonObject();
        assets.add("types", ruleTypes);
        assets.add("attributes", attributes);
        assets.add("ids", deviceIds);

        JsonObject users = new JsonObject();
        users.add("ids", targetIds);

        JsonObject message = new JsonObject();
        message.addProperty("type", "push");
        message.addProperty("title", "Rule Notification");
        message.addProperty("body", "Click to view the rule details.");
        message.add("action", action);

        // level 6
        JsonArray groupItems = new JsonArray();
        JsonObject groupItem = new JsonObject();
        groupItem.add("assets", assets);
        groupItems.add(groupItem);

        JsonObject target = new JsonObject();
        target.add("users", users);

        JsonObject notification = new JsonObject();
        notification.add("message", message);

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

//        switch (ruleAction){
//            case "target":
//                thenObject.add("target", target);
//                break;
//            case "notification":
//                thenObject.add("notification", notification);
//                break;
//        }



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
