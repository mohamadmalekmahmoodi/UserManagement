package ir.useronlinemanagement.aop;

import ir.useronlinemanagement.annotation.IsAuthorized;
import ir.useronlinemanagement.controller.request.IsAuthorizeRequest;
import ir.useronlinemanagement.service.UserService;
import ir.useronlinemanagement.service.impl.UtilService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAspect {

    private final UserService userService;
    private final UtilService utilService;
    @Autowired
    public AuthorizationAspect(UserService userService, UtilService utilService) {
        this.userService = userService;
        this.utilService = utilService;
    }

    @Around("@annotation(isAuthorized)")
    public Object checkAuthorization(ProceedingJoinPoint joinPoint, IsAuthorized isAuthorized) throws Throwable {
        String permissionName = isAuthorized.permission();

        String jwt = utilService.extractJwtFromContext();
        if (jwt == null || jwt.isEmpty()) {
            throw new SecurityException("توکن وجود ندارد یا نامعتبر است.");
        }

        IsAuthorizeRequest request = new IsAuthorizeRequest();
        request.setJwt(jwt);
        request.setPermissionName(permissionName);

        Boolean isAuthorizedResponse = userService.isAuthorized(request);

        if (Boolean.FALSE.equals(isAuthorizedResponse)) {
            throw new SecurityException("شما اجازه دسترسی به این منبع را ندارید.");
        }
        return joinPoint.proceed();
    }
}

