package ir.useronlinemanagement.controller;

import ir.useronlinemanagement.controller.request.VerifyIpRequest;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.service.IpVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ip-verification")
public class IpVerificationController {

    private final IpVerificationService verificationService;

    @Autowired
    public IpVerificationController(IpVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyIpRequest request) {
        User user = verificationService.getCurrentAuthenticatedUser();

        boolean result = verificationService.verify(user.getId() , request.getIp(), request.getOtp());
        if (!result) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("کد نامعتبر یا منقضی شده است");
        }
        return ResponseEntity.ok("IP با موفقیت تأیید شد");
    }
}

