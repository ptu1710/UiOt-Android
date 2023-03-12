package com.ixxc.myuit.Model;

import java.util.ArrayList;
import java.util.List;

public class User {
    public String realm;
    public String realmId;
    public String id;
    public boolean enabled;
    public long createdOn;
    public boolean serviceAccount;
    public String username;
    public String firstName = "";
    public String lastName = "";

    private List<Role> userRoles;

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
            return firstName + lastName;
        } else {
            return firstName.equals("") ? lastName : firstName;
        }
    }
}
