package ir.useronlinemanagement.service;

import ir.useronlinemanagement.controller.request.PermissionRequest;
import ir.useronlinemanagement.controller.response.PermissionResponse;
import ir.useronlinemanagement.controller.response.Response;

import java.util.List;

public interface PermissionService {
    Response save(PermissionRequest request);
    Response update (PermissionRequest request,Long id);
    Response delete (Long id);
    List<PermissionResponse> findAll();
    PermissionResponse findById(Long id);

}
