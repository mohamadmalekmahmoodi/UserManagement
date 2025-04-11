package ir.useronlinemanagement.controller;

import ir.useronlinemanagement.controller.request.PermissionRequest;
import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/permissions")
 // Lombok برای ساخت کانستراکتور خودکار
public class PermissionController {

    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<Response> savePermission(@RequestBody PermissionRequest request) {
        Response response = permissionService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

