package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;

public class CreateAssetReq {
    String name, type, realm;
    JsonObject attributes;

    public CreateAssetReq(String name, String type, String realm, JsonObject attributes) {
        this.name = name;
        this.type = type;
        this.realm = realm;
        this.attributes = attributes;
    }

    public JsonObject getJsonObj() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("type", type);
        obj.addProperty("realm", realm);
        obj.add("attributes", attributes);

        return obj;
    }
}
