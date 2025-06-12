package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.controller.response.Response;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.model.UserIp;
import ir.useronlinemanagement.repository.UserIpRepository;
import ir.useronlinemanagement.repository.UserRepository;
import ir.useronlinemanagement.service.IpVerificationService;
import ir.useronlinemanagement.service.UserIpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class UserIpServiceImpl implements UserIpService {

    private final UserIpRepository userIpRepository;
    private final IpVerificationService ipVerificationService;
    private final UserRepository userRepository;
    private static final int MAX_IPS_PER_USER = 5;

    @Autowired
    public UserIpServiceImpl(UserIpRepository userIpRepository, IpVerificationService ipVerificationService, UserRepository userRepository) {
        this.userIpRepository = userIpRepository;
        this.ipVerificationService = ipVerificationService;
        this.userRepository = userRepository;
    }

    @Override
    public void registerUserIp(Long userId, String ipAddress) {
        boolean exists = userIpRepository.existsByUserIdAndIpAddressAndStatusTrue(userId, ipAddress);
        if (exists) return;

        List<UserIp> ips = userIpRepository.findByUserIdAndStatusTrueOrderByRegisterDateAsc(userId);
        if (ips.size() >= MAX_IPS_PER_USER) {
            userIpRepository.delete(ips.get(0));
        }
        User user = userRepository.findById(userId).orElse(null);

        UserIp userIp = new UserIp();
        userIp.setUser(user);
        userIp.setIpAddress(ipAddress);
        userIp.setRegisterDate(Instant.now());
        userIp.setStatus(false);
        userIpRepository.save(userIp);

        ipVerificationService.generateAndSendOtp(user, ipAddress);
    }


    @Override
    public boolean isIpAllowed(User user, String ipAddress) {
        // لیست IP های فعال کاربر رو می‌گیره
        List<UserIp> allowedIps = userIpRepository.findByUserAndStatusTrue(user);
        // چک می‌کنه IP درخواست تو این لیست هست یا نه
        return allowedIps.stream()
                .anyMatch(userIp -> userIp.getIpAddress().equals(ipAddress));
    }

    @Override
    public Long getAllActiveIpAddresses(Long userId) {
        return userIpRepository.countByUserIdAndStatusTrue(userId);
    }

    @Override
    public List<String> getAllActiveIpAddress(Long userId) {
        List<UserIp> activeIp = userIpRepository.findAllByUserIdAndStatusTrue(userId);
        return activeIp.stream().map(UserIp::getIpAddress).toList();
    }

    @Override
    @Transactional
    public Response deleteUserStatus(Long userId) {
        List<UserIp> statusTrue = userIpRepository.findAllByUserIdAndStatusTrue(userId);
        for (UserIp userIp : statusTrue) {
            userIp.setStatus(false);
            userIpRepository.save(userIp);
        }
        Response response = new Response();
        response.setErrorCode(200);
        response.setMessage("delete user ip status");
        return response;
    }


}
