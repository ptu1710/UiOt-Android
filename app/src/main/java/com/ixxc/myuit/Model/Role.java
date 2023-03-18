package com.ixxc.myuit.Model;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ixxc.myuit.GlobalVars;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Role {
    public String id;
    public String name;
    public String description = "";
    public boolean composite;
    public boolean assigned;
    public ArrayList<String> compositeRoleIds = new ArrayList<>();

    private static final List<Role> roleList = new ArrayList<>();
    private static final List<Role> compositeRoleList = new ArrayList<>();
    private static List<Role> realmRoleList = new ArrayList<>();

    public static List<Role> getRoleList() {
        return roleList;
    }

    public static void setRoleList(List<Role> roles, boolean isRealm) {
        roles.sort((r1, r2) -> r2.name.compareTo(r1.name));

        if (isRealm) {
            realmRoleList.clear();
            realmRoleList = roles;
        } else {
            roleList.clear();
            compositeRoleList.clear();

            for (Role role : roles) {
                if (role.composite) {
                    compositeRoleList.add(role);
                } else {
                    roleList.add(role);
                }
            }
        }
    }

    public static Role getCompositeRoleByName(String roleName) {
        return compositeRoleList.stream().filter(r -> r.name.equals(roleName)).collect(Collectors.toList()).get(0);
    }

    public static List<Role> getCompositeRoleList() {
        return compositeRoleList;
    }

    public static String getNameByID(List<Role> roles, String id){
        for (Role role : roles) {
            if(role.id.equals(id)) { return role.name; }
        }

        return null;
    }

    public static String getIdByDescription(String description){
        for (Role role : Role.getRoleList()) {
            if(role.description.equals(description)){ return role.id; }
        }

        return null;
    }

    public Role() {

    }

    public Role(String id, String name, String description, boolean composite, ArrayList<String> compositeRoleIds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.composite = composite;
        this.compositeRoleIds = compositeRoleIds;
    }

    public JsonObject toJSON() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("name", name);
        if (!TextUtils.isEmpty(description)) o.addProperty("description", description);
        o.addProperty("composite", composite);
        if (composite) {
            JsonArray arr = new JsonArray();
            for (String id : compositeRoleIds) arr.add(id);
            o.add("compositeRoleIds", arr);
        }

        return o;
    }
}
