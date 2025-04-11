package ir.useronlinemanagement.service;


import ir.useronlinemanagement.controller.request.UpdateRegisterUser;
import ir.useronlinemanagement.controller.response.*;
import ir.useronlinemanagement.controller.request.IsAuthorizeRequest;
import ir.useronlinemanagement.controller.request.LoginRequest;
import ir.useronlinemanagement.controller.request.RegisterRequest;

public interface UserService {
    RegisterResponse registerByAdmin(RegisterRequest request);
    RegisterResponse register(RegisterRequest request);
    UpdateUserResponse update(UpdateRegisterUser registerUser , Long id);
    Boolean isAuthorized(IsAuthorizeRequest request);
    LoginResponse login(LoginRequest request);
    UserDetailsRes getDetails();

}
