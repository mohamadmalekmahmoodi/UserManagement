package ir.useronlinemanagement.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor@NoArgsConstructor

public class RoleRequest {
    @NotNull(message = "نام نقش نباید نال باشد!")
    @NotEmpty(message = "نام نقش نباید خالی باشد!")
    private String roleName;
    private List<Long> permissionIds;
    private String description;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName( String roleName) {
        this.roleName = roleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getPermissionIds() {
        return permissionIds;
    }

    public void setPermissionIds(List<Long> permissionIds) {
        this.permissionIds = permissionIds;
    }
}
