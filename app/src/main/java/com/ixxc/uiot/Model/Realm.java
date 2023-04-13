package com.ixxc.uiot.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class Realm {

    public String id;
    public String name;
    public String displayName;
    public Boolean enabled;
    public Integer notBefore;
    public Boolean resetPasswordAllowed;
    public Boolean duplicateEmailsAllowed;
    public Boolean rememberMe;
    public Boolean registrationAllowed;
    public Boolean registrationEmailAsUsername;
    public Boolean verifyEmail;
    public Boolean loginWithEmail;
    public String loginTheme;
    public String accountTheme;
    public String emailTheme;
    public Integer accessTokenLifespan;
    public JsonArray realmRoles;

    private static List<Realm> realmList = new ArrayList<>();
    public static List<Realm> getRealmList() {
        return realmList;
    }

    public static void setRealmList(List<Realm> realms) {
        realmList.clear();
        realmList = realms;
    }

    public Realm(String name, String displayName, Boolean enabled) {
        this.name = name;
        this.displayName = displayName;
        this.enabled = enabled;
    }

    public JsonObject toJsonFull() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("name", name);
        o.addProperty("displayName", displayName);
        o.addProperty("enabled", enabled);
        o.addProperty("notBefore", notBefore);
        o.addProperty("resetPasswordAllowed", resetPasswordAllowed);
        o.addProperty("duplicateEmailsAllowed", duplicateEmailsAllowed);
        o.addProperty("rememberMe", rememberMe);
        o.addProperty("registrationAllowed", registrationAllowed);
        o.addProperty("registrationEmailAsUsername", registrationEmailAsUsername);
        o.addProperty("verifyEmail", verifyEmail);
        o.addProperty("loginWithEmail", loginWithEmail);
        o.addProperty("loginTheme", loginTheme);
        o.addProperty("accountTheme", accountTheme);
        o.addProperty("emailTheme", emailTheme);
        o.addProperty("accessTokenLifespan", accessTokenLifespan);
        o.add("realmRoles", realmRoles);

        return o;
    }

    public JsonObject toJsonMin() {
        JsonObject o = new JsonObject();
        o.addProperty("name", name);
        o.addProperty("displayName", displayName);
        o.addProperty("enabled", enabled);

        return o;
    }
}
