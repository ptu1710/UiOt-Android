package com.ixxc.myuit.Model;

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
    public ArrayList<String> compositeRoleIds;

    public Role(String id, String name, String description, boolean composite, boolean assigned, ArrayList compositeRolelds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.composite = composite;
        this.assigned = assigned;
        this.compositeRoleIds = compositeRolelds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public ArrayList getCompositeRolelds() {
        return compositeRoleIds;
    }

    public void setCompositeRolelds(ArrayList compositeRolelds) {
        this.compositeRoleIds = compositeRolelds;
    }

    public static String getNameByID(List<Role> roles, String id){
        for (Role role:roles) {
            if(role.id.equals(id) ){
                return role.name;
            }

        }
        return null;
    }


}
