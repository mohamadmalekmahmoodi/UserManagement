package ir.useronlinemanagement.controller.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor

public class PermissionRequest {
    @NotNull(message = "نام دسترسی نباید نال باشد!")
    @NotEmpty(message = "نام دسترسی نباید خالی باشد!")
    private String permissionName;
//    private List<Long> roleIds;

//    public List<Long getRoleIds() {
//        return roleIds;
//    }
//
//    public void setRoleIds(Long roleIds) {
//        this.roleIds = roleIds;
//    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(@NotNull(message = "نام دسترسی نباید نال باشد!") @NotEmpty(message = "نام دسترسی نباید خالی باشد!") String permissionName) {
        this.permissionName = permissionName;
    }


}
