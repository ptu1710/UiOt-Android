package com.ixxc.myuit.Model;

import android.util.Log;

import com.google.gson.JsonObject;
import com.ixxc.myuit.GlobalVars;

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

    private List<Role> userRoles;

    public static List<User> getUsersList() {
        return users;
    }

    public static void setUsersList(List<User> users) {
        User.users = users;
    }

    private static List<User> users;
    private static User user;

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public List<Role> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<Role> userRoles) {
        this.userRoles = userRoles;
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
}
