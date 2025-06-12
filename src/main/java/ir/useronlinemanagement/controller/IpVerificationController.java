package ir.useronlinemanagement.controller;

import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.service.IpVerificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ip-verification")
public class IpVerificationController {

    private final IpVerificationService verificationService;

    @Autowired
    public IpVerificationController(IpVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp,HttpServletRequest servletRequest) {
//        User user = verificationService.getCurrentAuthenticatedUser();
        String clientIpFromRequest = getClientIpFromRequest(servletRequest);

        boolean result = verificationService.verify(otp,clientIpFromRequest);
        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("کد نامعتبر یا منقضی شده است");
        }
        return ResponseEntity.ok("IP با موفقیت تأیید شد");
    }

    public String getClientIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // اگر لیست IP بود، اولین IP واقعی کلاینت رو می‌گیریم
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

