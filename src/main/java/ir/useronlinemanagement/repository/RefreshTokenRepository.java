package ir.useronlinemanagement.repository;

import ir.useronlinemanagement.model.RefreshToken;
import ir.useronlinemanagement.service.RefreshTokenService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
