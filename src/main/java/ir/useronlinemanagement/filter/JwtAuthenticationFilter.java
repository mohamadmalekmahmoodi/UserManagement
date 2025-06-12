package ir.useronlinemanagement.filter;

import io.micrometer.common.lang.NonNull;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.repository.UserRepository;
import ir.useronlinemanagement.service.UserIpService;
import ir.useronlinemanagement.service.impl.JwtService;
import ir.useronlinemanagement.util.RedisRateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RedisRateLimiterService rateLimiterService;
    private final UserRepository userRepository;
    private final UserIpService userIpService;


    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, RedisRateLimiterService rateLimiterService, UserRepository userRepository, UserIpService userIpService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.rateLimiterService = rateLimiterService;
        this.userRepository = userRepository;
        this.userIpService = userIpService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader;
        final String username;
        final String jwt;

        authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        /* rate limiting for not requesting too much */
        if (!rateLimiterService.isAllowed("rate_limit:user:" + username, 100, Duration.ofMinutes(10))) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("too many request, try again later");
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            if (jwtService.isTokenValid(jwt, userDetails)) {

                //checking ip
                User user = userRepository.findByUsername(username).orElse(null);
                String clientIp = request.getRemoteAddr();
                if (!userIpService.isIpAllowed(user, clientIp)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("لطفا کد یکبار مصرف را وارد کنید , نیاز به تایید دارد");
                    return;
                }

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, jwt, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
