package com.ixxc.myuit.Model;

import java.util.List;

public class Role {
    public String id;
    public String name;
    public String description;
    public boolean composite;
    public boolean assigned;
    private static List<Role> roleList;

    public static List<Role> getRoleList() {
        return roleList;
    }

    public static void setRoleList(List<Role> roles) {
        roleList = roles;
    }
}
