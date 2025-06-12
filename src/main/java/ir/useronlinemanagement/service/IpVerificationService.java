package ir.useronlinemanagement.service;

import ir.useronlinemanagement.model.User;

public interface IpVerificationService {
    void generateAndSendOtp(User user, String ipAddress);
    User getCurrentAuthenticatedUser();
    boolean verify( String ip, String code);
}
