package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.controller.request.PermissionRequest;
import ir.useronlinemanagement.controller.response.PermissionResponse;
import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.exception.ResponseException;
import ir.useronlinemanagement.model.Permission;
import ir.useronlinemanagement.model.Role;
import ir.useronlinemanagement.repository.PermissionRepository;
import ir.useronlinemanagement.repository.RoleRepository;
import ir.useronlinemanagement.repository.UserRepository;
import ir.useronlinemanagement.service.PermissionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public PermissionServiceImpl(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

        @Override
        public Response save(PermissionRequest request){
            Optional<Permission> existingPermission = permissionRepository.findByName(request.getPermissionName());

            if (existingPermission.isPresent()) {
                Response response = new Response();
                response.setMessage("این دسترسی قبلاً ثبت شده است");
                response.setErrorCode(400); // یا هر کد مناسب دیگه مثل 409 (Conflict)
                return response;
            }
            Permission permission = new Permission();
            permission.setName(request.getPermissionName());
            permission.setDeleted(false);

            permissionRepository.save(permission);

            Response response = new Response();
            response.setMessage("permission saved successfully");
            response.setErrorCode(200);
            return response;
    }

    @Override
    public Response update(PermissionRequest request, Long id) {
        return null;
    }

    @Override
    public Response delete(Long id) {
        return null;
    }

    @Override
    public List<PermissionResponse> findAll() {
        return null;
    }

    @Override
    public PermissionResponse findById(Long id) {
        return null;
    }

}
