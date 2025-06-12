package ir.useronlinemanagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import ir.useronlinemanagement.annotation.IsAuthorized;
import ir.useronlinemanagement.controller.request.*;
import ir.useronlinemanagement.controller.response.*;
import ir.useronlinemanagement.exception.ResponseException;
import ir.useronlinemanagement.otp.OtpService;
import ir.useronlinemanagement.service.RefreshTokenService;
import ir.useronlinemanagement.service.UserService;
import ir.useronlinemanagement.service.impl.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final OtpService otpService;
    private final JwtService jwtService;

    @Autowired
    public UserController(UserService userService, OtpService otpService, JwtService jwtService) {
        this.userService = userService;
        this.otpService = otpService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register-admin")
    @Operation(summary = "ذخیره کاربر در دیتابیس با نقش ادمین")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegisterResponse> registerByAdmin(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.registerByAdmin(request));
    }

    @PostMapping("/register")
    @Operation(summary = "ذخیره کاربر در دیتابیس")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "ورود کاربر")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(userService.login(request,servletRequest));
    }

    @GetMapping("/current/details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailsRes> getCurrentUserDetails() {
        UserDetailsRes userDetailsRes = userService.getDetails();
        return ResponseEntity.ok(userDetailsRes);
    }

    @PostMapping("/isAuthorized")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Boolean> checkAuthorization(@RequestBody IsAuthorizeRequest request) {
        Boolean response = userService.isAuthorized(request);
        return new ResponseEntity<>(response, response ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestBody TokenRequest request) {
        boolean isValid = jwtService.isTokenValid(request.getToken());

        if (isValid) {
            return ResponseEntity.ok("✅ توکن درسته داش!");
        } else {
            return ResponseEntity.badRequest().body("❌ توکنت جعلیه یا تاریخش گذشته!");
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@RequestBody @Valid VerifyOtpRequest request) {
        boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());
        if (!isValid) {
            throw new ResponseException("کد وارد شده صحیح نیست یا منقضی شده", 400);
        }
        return ResponseEntity.ok(true);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordRes> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
        ResetPasswordRes res = userService.resetPassword(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/forget-password")
    public ResponseEntity<ForgetPasswordRes> forgetPassword(@RequestBody String email) {
        ForgetPasswordRes res = userService.forgetPassword(email);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = userService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

