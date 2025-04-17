package ir.useronlinemanagement.service;

import ir.useronlinemanagement.model.RefreshToken;
import ir.useronlinemanagement.model.User;

import java.util.Optional;

public interface RefreshTokenService {
    Optional<RefreshToken> findByToken(String token);
    RefreshToken createRefreshToken(User user, String refreshToken);
}
