package com.ixxc.myuit.Model;

import com.google.gson.JsonArray;

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



}
