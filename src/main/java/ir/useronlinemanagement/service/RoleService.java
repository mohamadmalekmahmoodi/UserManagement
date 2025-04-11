package ir.useronlinemanagement.service;

import ir.useronlinemanagement.controller.request.RoleRequest;
import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.controller.response.RoleResponse;

import java.util.List;

public interface RoleService {
    Response save(RoleRequest request);
    Response delete(RoleRequest request);
    RoleResponse findById(Long id);
    Response update(RoleRequest request,Long id);
    List<RoleResponse> findAll();
}
