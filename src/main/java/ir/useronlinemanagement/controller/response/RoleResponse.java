package ir.useronlinemanagement.controller.response;

public class RoleResponse {
    private String roleName;
    public String getRoleName() {return roleName;}
    public void setRoleName(String roleName) {this.roleName = roleName;}
    public RoleResponse(String roleName) {
        this.roleName = roleName;
    }
    public RoleResponse() {}

}
