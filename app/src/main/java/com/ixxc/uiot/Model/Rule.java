package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Rule {
    String name;

    public String getName() {
        return name;
    }

    public String type;
    public Integer id;
    public Integer version;
    public Long createdOn;
    public Long lastModified;
    public Boolean enabled;
    public String rules;
    public String lang;
    public String status;
    public String realm;
    public Boolean accessPublicRead;


    public Rule(String name, String type, Integer id, Integer version, Long createdOn, Long lastModified, Boolean enabled, String rules, String lang, String status, String realm, Boolean accessPublicRead) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.version = version;
        this.createdOn = createdOn;
        this.lastModified = lastModified;
        this.enabled = enabled;
        this.rules = rules;
        this.lang = lang;
        this.status = status;
        this.realm = realm;
        this.accessPublicRead = accessPublicRead;
    }

    public static Rule rule_selected ;
}
