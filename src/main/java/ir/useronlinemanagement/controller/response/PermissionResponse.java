package ir.useronlinemanagement.controller.response;

public class PermissionResponse {
    private Long id;
    private String permission;

    public PermissionResponse() {
    }
    public PermissionResponse(Long id, String permission) {
        this.id = id;
        this.permission = permission;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
