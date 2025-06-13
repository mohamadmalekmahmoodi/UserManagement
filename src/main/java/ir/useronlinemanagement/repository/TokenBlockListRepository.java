package ir.useronlinemanagement.repository;

import ir.useronlinemanagement.model.TokenBlockList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface TokenBlockListRepository extends JpaRepository<TokenBlockList, Long> {
    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("delete from TokenBlockList t where t.expiresAt < ?1")
    void deleteAllExpiredTokens(Instant now);
}

