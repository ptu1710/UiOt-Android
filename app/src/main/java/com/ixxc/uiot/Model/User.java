package com.ixxc.uiot.Model;

import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String realm;
    public String realmId;
    public String id;
    public boolean enabled;
    public long createdOn;

    public boolean serviceAccount;
    public String email = "";
    public String username;
    public String firstName = "";
    public String lastName = "";

    public JsonObject attributes;

    public String secret;

    // List users by query
    private static List<User> users;
    public static List<User> getUsersList() {
        return users;
    }
    public static void setUsersList(List<User> users) {
        User.users = users;
    }

    // Current logged in user
    private static User me;
    public static void setMe(User u) {
        me = u;
    }
    public static User getMe() {
        return me;
    }

    // List realm roles of a user's instance
    private List<Role> realmRoles;
    public void setRealmRoles(List<Role> realmRoles) {
        this.realmRoles = realmRoles;
    }
    public List<Role> getRealmRoles() {
        return realmRoles;
    }

    // Set roles and composite roles of a user's instance
    private final List<Role> roleList = new ArrayList<>();
    private final List<Role> compositeRoleList = new ArrayList<>();
    public void setUserRoles(List<Role> roles) {
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

    // Get roles and composite roles of a user's instance
    public List<Role> getRoleList() {
        return roleList;
    }

    public List<Role> getCompositeRoleList() {
        for (Role role :
                compositeRoleList) {
            role.compositeRoleIds = Role.getCompositeRoleByName(role.name).compositeRoleIds;
        }

        return compositeRoleList;
    }

    // Linked Devices of a user's instance
    private List<LinkedDevice> linkedDevices = new ArrayList<>();
    public void setLinkedDevices(List<LinkedDevice> devices) {
        linkedDevices = devices;
    }

    public List<LinkedDevice> getLinkedDevices() {
        return linkedDevices;
    }

    public int getNumofConsoles() {
        return (int) linkedDevices.stream().filter(linkedDevice -> linkedDevice.parentAssetName.equals("Consoles")).count();
    }

    public String getDisplayName() {
        if (firstName.equals("") && lastName.equals("")) {
            return username;
        } else if (!firstName.equals("") && !lastName.equals("")) {
            return String.join(" ", firstName, lastName);
        } else {
            return firstName.equals("") ? lastName : firstName;
        }
    }

    public boolean canReadDevices() {
        Role hasRole = roleList.stream()
                .filter(role -> role.name.contains("read:assets") && role.assigned)
                .findFirst().orElse(null);

        return hasRole != null;
    }

    public boolean canWriteDevices() {
        Role hasRole = roleList.stream()
                .filter(role -> role.name.contains("write:assets") && role.assigned)
                .findFirst().orElse(null);

        return hasRole != null;
    }

    public boolean canWriteUsers() {
        Role hasRole = roleList.stream()
                .filter(role -> role.name.contains("write:user") && role.assigned)
                .findFirst().orElse(null);

        return hasRole != null;
    }

    public boolean canWriteAdmin() {
        Role hasRole = roleList.stream()
                .filter(role -> role.name.contains("write:admin") && role.assigned)
                .findFirst().orElse(null);

        return hasRole != null;
    }

    public JsonObject toJson() {
        JsonObject o = new JsonObject();
        o.addProperty("realm", realm);
        o.addProperty("realmId", realmId);
        o.addProperty("id", id);
        o.addProperty("firstName", firstName);
        o.addProperty("lastName", lastName);
        o.addProperty("email", email);
        o.addProperty("enabled", enabled);
        o.addProperty("createdOn", createdOn);
        o.addProperty("serviceAccount", serviceAccount);
        o.addProperty("username", username);

        return o;
    }
}
