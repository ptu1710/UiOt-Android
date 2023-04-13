package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

public class CreateAssetReq {
    String name, type, realm, parentId = "";
    JsonObject attributes;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setAttributes(JsonObject attributes) {
        this.attributes = attributes;
    }

    public CreateAssetReq() {
        this.name = "";
        this.type = "";
        this.realm = "master";
        this.parentId = "";
        this.attributes = null;
    }

    public JsonObject getJsonObj() {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        obj.addProperty("type", type);
        obj.addProperty("realm", realm);
        if (!parentId.equals("None")) {
            obj.addProperty("parentId", parentId);
        }
        obj.add("attributes", attributes);

        return obj;
    }
}
