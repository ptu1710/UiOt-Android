package com.ixxc.myuit.Model;

import com.google.gson.JsonObject;

public class MetaItem {
    public String name;
    public String type;
    public JsonObject toJson() {
        JsonObject object = new JsonObject();
        object.add(name,null);
        return object;
    }

}
