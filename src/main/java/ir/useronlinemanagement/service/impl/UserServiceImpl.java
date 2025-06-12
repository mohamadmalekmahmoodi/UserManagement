package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.controller.request.*;
import ir.useronlinemanagement.controller.response.*;
import ir.useronlinemanagement.exception.ResponseException;
import ir.useronlinemanagement.model.RefreshToken;
import ir.useronlinemanagement.model.Role;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.otp.OtpService;
import ir.useronlinemanagement.repository.RoleRepository;
import ir.useronlinemanagement.repository.UserRepository;
import ir.useronlinemanagement.service.RefreshTokenService;
import ir.useronlinemanagement.service.UserIpService;
import ir.useronlinemanagement.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final StringRedisTemplate stringRedisTemplate;
    private final RefreshTokenService refreshTokenService;
    private final UserIpService userIpService;
    private final UtilService utilService;
    private final UserDetailsService userDetailsService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final OtpService otpService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, JwtService jwtService, StringRedisTemplate stringRedisTemplate, RefreshTokenService refreshTokenService, UserIpService userIpService, UtilService utilService, UserDetailsService userDetailsService, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.stringRedisTemplate = stringRedisTemplate;
        this.refreshTokenService = refreshTokenService;
        this.userIpService = userIpService;
        this.utilService = utilService;
        this.userDetailsService = userDetailsService;
        this.otpService = otpService;
    }

    @Override
    public RegisterResponse registerByAdmin(RegisterRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail.isPresent()) {
            throw new ResponseException("شما از قبل ثبت نام کرده اید!!", 400);
        } else {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseException("هیچ نقشی با این شناسه یافت نشد!", 404));

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(List.of(role));  // <<< اینجا اصلاح شد
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhoneNumber());
            user.setDeleted(false);
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            userRepository.save(user);
            String token = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            RegisterResponse response = new RegisterResponse();
            response.setToken(token);
            response.setRefreshToken(refreshToken);
            return response;
        }
    }


    @Override
    public RegisterResponse register(RegisterRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        if (byEmail.isPresent()) {
            throw new ResponseException("شما از قبل ثبت نام کرده اید!!", 400);
        } else {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseException("هیج نقشی با این شناسه یافت نشد!", 404));

            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword())); // ✅ هش‌شده
            user.setRoles(List.of(role));  // <<< اینجا اصلاح شد
            user.setDeleted(false);
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhoneNumber());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());

            userRepository.save(user);
            String token = jwtService.generateToken(user);
            RegisterResponse response = new RegisterResponse();
            response.setToken(token);
            return response;
        }
    }


    @Override
    public UpdateUserResponse update(UpdateRegisterUser req, Long id) {
        Role role = roleRepository.findById(req.getRoleId()).orElseThrow(() -> new ResponseException("نقشی با این شناسه یافن نشد", 404));
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseException("کاربری با این شناسه یافن نشد", 404));
        if (role.getName().equals("ADMIN_USER") || role.getName().equals("SUPER_ADMIN")) {
            user.setDeleted(req.getDeleted());
            user.setRoles(List.of(role));  // <<< اینجا اصلاح شد
        }
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setPhone(req.getPhone());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setUsername(req.getUsername());
        user.setLastPasswordReset(Instant.now());
        userRepository.save(user);
        return new UpdateUserResponse("کاربر به روز رسانی شد");
    }

    @Override
    public LoginResponse login(LoginRequest request, HttpServletRequest servletRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new ResponseException("هیچ کاربری با این شناسه یافت نشد", 404);
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        if (userDetails == null || !passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            throw new IllegalArgumentException("یوزرنیم یا پسورد اشتباه است");
        }
        User user = optionalUser.get();
        updateLastLogin(user);

        //getting ip while logging
        String clientIp = getClientIpFromRequest(servletRequest); // باید پیاده‌سازی‌اش کنی یا بگیری از پارامتر
        userIpService.registerUserIp(user.getId(), clientIp);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        return response;
    }

    public RefreshTokenResponse refreshAccessToken(String refreshToken) {
        // رفرش توکن تا 7 روز تاریخ دارد اگه گذشته بود دوباره باید لاگین کند
        RefreshToken token = refreshTokenService.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token expired");
        }
        User user = token.getUser();
        String newAccessToken = jwtService.generateToken(user);
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccess_token(newAccessToken);
        response.setRefresh_token(refreshToken);
        return response;
    }


    @Override
    @Transactional
    public UserDetailsRes getDetails() {
        User user = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new ResponseException("شخصی با این نام یافت نشد", 404));

        UserDetailsRes userDetailsRes = new UserDetailsRes();
        userDetailsRes.setFirstName(user.getFirstName());
        userDetailsRes.setLastName(user.getLastName());
        userDetailsRes.setEmail(user.getEmail());
        userDetailsRes.setUsername(user.getUsername());
        userDetailsRes.setPhone(user.getPhone());
        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        userDetailsRes.setRoleName(roleNames);

        return userDetailsRes;
    }


    @Override
    public Boolean isAuthorized(IsAuthorizeRequest request) {
        try {
            String token = utilService.extractJwtFromContext();
            String username = jwtService.extractUsername(token);

            if (!jwtService.isTokenValid(request.getJwt())) {
                return false;
            }

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseException("شخصی با این نام کاربری یافت نشد", 404));

            return user.getRoles().stream()
                    .flatMap(role -> role.getPermissions().stream())
                    .anyMatch(permission -> permission.getName().equals(request.getPermissionName()));

        } catch (Exception e) {
            return false;
        }
    }


    private void updateLastLogin(User user) {
        user.setLastLogin(Instant.now());
        userRepository.save(user);
    }

    private LoginResponse buildLoginResponse(String token, String refreshToken) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Override
    public ForgetPasswordRes forgetPassword(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new ResponseException("حساب کاربری یافت نشد!", 404));
        otpService.generateOtp(email);
//        return new ForgetPasswordRes("رمز یکبارمصرف جنریت شد");
        ForgetPasswordRes f = new ForgetPasswordRes();
        f.setMessage("رمز یکبارمصرف جنریت شد");
        return f;
    }

    @Override
    public ResetPasswordRes resetPassword(ResetPasswordRequest request) {
        String verifiedKey = "otp:verified:" + request.getEmail();
        if (!Boolean.TRUE.equals(stringRedisTemplate.hasKey(verifiedKey))) {
            throw new ResponseException("باید رمز یکبار مصرف تایید شود", 403);
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseException("شما ابتدا باید ثبت نام کنید!", 404));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setLastPasswordReset(Instant.now());
        userRepository.save(user);
//        return new ResetPasswordRes("پسورد شما به روز رسانی شد");
        ResetPasswordRes f = new ResetPasswordRes();
        f.setMessage("پسورد شما به روز رسانی شد");
        return f;
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
