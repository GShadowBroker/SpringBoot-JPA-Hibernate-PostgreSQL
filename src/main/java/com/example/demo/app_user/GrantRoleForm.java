package com.example.demo.app_user;

public class GrantRoleForm {

    private String email;
    private String roleName;

    public GrantRoleForm() {
    }

    public GrantRoleForm(String email, String roleName) {
        this.email = email;
        this.roleName = roleName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "GrantRoleForm{" +
                "email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
