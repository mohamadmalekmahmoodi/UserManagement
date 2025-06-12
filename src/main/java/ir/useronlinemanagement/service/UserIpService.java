package ir.useronlinemanagement.service;

import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.model.User;

import java.util.List;

public interface UserIpService {
    void registerUserIp(Long userId, String ipAddress);
    boolean isIpAllowed(User user, String ipAddress);
    Long getAllActiveIpAddresses(Long userId);
    List<String> getAllActiveIpAddress(Long userId);
    Response deleteUserStatus(Long userId);
}
