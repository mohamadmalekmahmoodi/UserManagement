package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.model.IpVerification;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.model.UserIp;
import ir.useronlinemanagement.repository.IpVerificationRepository;
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

    @Autowired
    public IpVerificationServiceImpl(IpVerificationRepository repository, JavaMailSender mailSender, UserRepository userRepository) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.userRepository = userRepository;
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

    public boolean verify(Long userId, String ip, String code) {
        User user = userRepository.findById(userId).orElse(null);
        Optional<IpVerification> optional = repository.findByUserAndIpAddressAndOtpCode(user, ip, code);
        if (optional.isEmpty()) return false;

        IpVerification verification = optional.get();
        if (verification.getExpiration().isBefore(Instant.now())) {
            return false; // منقضی شده
        }

        // فعال‌سازی IP در دیتابیس
        UserIp ipEntity = user.getUserIps().stream()
                .filter(i -> i.getIpAddress().equals(ip))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("IP not found"));

        ipEntity.setStatus(true);
        verification.getUser().getUserIps().removeIf(i -> i.getIpAddress().equals(ip)); // به‌روزرسانی local
        verification.getUser().getUserIps().add(ipEntity); // مجدد اضافه

        repository.delete(verification); // یک بار مصرف
        return true;
    }

    public User getCurrentAuthenticatedUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        return user;
    }
}

