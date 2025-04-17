package ir.useronlinemanagement.service.impl;

import ir.useronlinemanagement.model.RefreshToken;
import ir.useronlinemanagement.model.User;
import ir.useronlinemanagement.repository.RefreshTokenRepository;
import ir.useronlinemanagement.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user, String refreshToken) {
        RefreshToken token = new RefreshToken();
        token.setToken(refreshToken);
        Instant expiresAt = Instant.now().plus(7, ChronoUnit.DAYS);
        token.setExpiryDate(expiresAt);
        token.setUser(user);
        return refreshTokenRepository.save(token);
    }
}
