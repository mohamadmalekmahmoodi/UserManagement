package ir.useronlinemanagement.controller;

import ir.useronlinemanagement.annotation.IsAuthorized;
import ir.useronlinemanagement.controller.request.RoleRequest;
import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")

public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Response> saveRole(@RequestBody RoleRequest request) {
        Response response = roleService.save(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}

