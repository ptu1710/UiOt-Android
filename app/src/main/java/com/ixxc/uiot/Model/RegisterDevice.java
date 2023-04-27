package com.ixxc.uiot.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RegisterDevice {
    private String id = "";
    private final String name;
    private final JsonArray apps = new JsonArray();

    public RegisterDevice(String name) {
        this.name = name;
        this.apps.add("manager");
    }

    public RegisterDevice(String id, String name) {
        this.id = id;
        this.name = name;
        this.apps.add("manager");
    }

    public String getId() { return id; }

    public String getName() { return name; }

    public JsonObject toJson(String token) {
        JsonObject json = new JsonObject();

        if (!id.isEmpty()) json.addProperty("id", id);

        json.addProperty("name", name);
        json.addProperty("version", "4.0");
        json.addProperty("platform", "Android 10");
        json.addProperty("model", "Android SDK built for x86");

        json.add("apps", apps);

        json.add("providers", getPushProvider(token));

        return json;
    }

    public JsonObject getPushProvider(String token) {
        JsonObject pushJson = new JsonObject();

        JsonObject json = new JsonObject();

        if (token.isEmpty()) {
            json.addProperty("enabled", false);
            json.addProperty("disabled", false);
            json.addProperty("requiresPermission", false);
            json.addProperty("hasPermission", true);
            json.addProperty("success", true);
            json.addProperty("version", "fcm");

        } else {
            json.addProperty("version", "fcm");
            json.addProperty("requiresPermission", false);
            json.addProperty("hasPermission", true);
            json.addProperty("success", true);
            json.addProperty("enabled", true);
            json.addProperty("disabled", false);

            JsonObject data = new JsonObject();
            data.addProperty("token", token);

            json.add("data", data);
        }

        pushJson.add("push", json);

        return pushJson;
    }
}
