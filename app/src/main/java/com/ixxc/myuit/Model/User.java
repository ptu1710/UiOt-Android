package com.ixxc.myuit.Model;

public class User {
    public String realm;
    public String realmId;
    public String id;
    public boolean enabled;
    public long createdOn;
    public boolean serviceAccount;
    public String username;

    private static User user;

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }
}
