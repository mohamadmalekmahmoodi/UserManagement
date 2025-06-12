package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.model.IpVerification;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.model.UserIp;
import ir.useronlinemanagement.repository.IpVerificationRepository;
import ir.useronlinemanagement.repository.UserIpRepository;
import ir.useronlinemanagement.repository.UserRepository;
import ir.useronlinemanagement.service.IpVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
public class IpVerificationServiceImpl implements IpVerificationService {

    private final IpVerificationRepository repository;
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UserIpRepository userIpRepository;

    @Autowired
    public IpVerificationServiceImpl(IpVerificationRepository repository, JavaMailSender mailSender, UserRepository userRepository, UserIpRepository userIpRepository, UserIpRepository userIpRepository1) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.userIpRepository = userIpRepository1;
    }

    public void generateAndSendOtp(User user, String ipAddress) {
        String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6 رقمی

        IpVerification verification = new IpVerification();
        verification.setIpAddress(ipAddress);
        verification.setExpiration(Instant.now().plus(Duration.ofMinutes(10)));
        verification.setOtpCode(otp);
        verification.setUser(user);

        repository.save(verification);

        // ارسال ایمیل (یا SMS اگر خواستی)
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("کد تایید IP جدید");
        message.setText("کد تایید IP شما: " + otp);
        mailSender.send(message);
    }

    public boolean verify(String code,String ip) {
//        User user = userRepository.findById(userId).orElse(null);
        IpVerification optional = repository.findByIpAddressAndOtpCode(ip, code).orElse(null);

        if (optional.getExpiration().isBefore(Instant.now())) {
            return false; // منقضی شده
        }

        // فعال‌سازی IP در دیتابیس
        UserIp ipEntity = userIpRepository.findFirstByIpAddress(ip).orElse(null);
//                user.getUserIps().stream()
//                .filter(i -> i.getIpAddress().equals(ip))
//                .findFirst()
//                .orElseThrow(() -> new RuntimeException("IP not found"));

        ipEntity.setStatus(true);
        optional.getUser().getUserIps().removeIf(i -> i.getIpAddress().equals(ip)); // به‌روزرسانی local
        optional.getUser().getUserIps().add(ipEntity); // مجدد اضافه

        repository.delete(optional); // یک بار مصرف
        return true;
    }

    public User getCurrentAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return user;
    }
}

