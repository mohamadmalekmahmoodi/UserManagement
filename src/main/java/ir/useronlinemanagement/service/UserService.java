package ir.useronlinemanagement.service;


import ir.useronlinemanagement.controller.request.*;
import ir.useronlinemanagement.controller.response.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Date;

public interface UserService {
    RegisterResponse registerByAdmin(RegisterRequest request);
    RegisterResponse register(RegisterRequest request);
    UpdateUserResponse update(UpdateRegisterUser registerUser , Long id);
    Boolean isAuthorized(IsAuthorizeRequest request);
    LoginResponse login(LoginRequest request, HttpServletRequest servletRequest);
    UserDetailsRes getDetails();
    ForgetPasswordRes forgetPassword(String email);
    ResetPasswordRes resetPassword(ResetPasswordRequest request);
    RefreshTokenResponse refreshAccessToken(String refreshToken);
    Response logout(String token);
}