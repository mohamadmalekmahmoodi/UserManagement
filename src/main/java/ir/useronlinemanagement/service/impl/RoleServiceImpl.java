package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.controller.request.RoleRequest;
import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.controller.response.RoleResponse;
import ir.useronlinemanagement.exception.ResponseException;
import ir.useronlinemanagement.model.Permission;
import ir.useronlinemanagement.model.Role;
import ir.useronlinemanagement.repository.PermissionRepository;
import ir.useronlinemanagement.repository.RoleRepository;
import ir.useronlinemanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Response save(RoleRequest request) {
        Optional<Role> existingRole = roleRepository.findByName(request.getRoleName());

        if (existingRole.isPresent()) {
            Response response = new Response();
            response.setMessage("Role with the same name already exists");
            response.setErrorCode(400);
            return response;
        }

        Role role = new Role();
        role.setName(request.getRoleName());
        role.setDescription(request.getDescription());
        role.setPermissions(retrievePermission(request.getPermissionIds()));
        role.setDeleted(false);
        roleRepository.save(role);
        Response response = new Response();
        response.setMessage("role saved successfully");
        response.setErrorCode(200);
        return response;
    }

    @Override
    public Response delete(RoleRequest request) {
        return null;
    }

    @Override
    public RoleResponse findById(Long id) {
        return null;
    }

    @Override
    public Response update(RoleRequest request, Long id) {
        return null;
    }

    @Override
    public List<RoleResponse> findAll() {
        return List.of();
    }

    private List<Permission> retrievePermission(List<Long> permissionIds) throws ResponseException {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return new ArrayList<>();
        }
        return permissionIds.stream()
                .map(id -> permissionRepository.findByIdAndIsDeletedFalse(id)
                        .orElseThrow(() -> new ResponseException("Tag not found for ID : " + id, 601)))
                .collect(Collectors.toList());
    }
}
