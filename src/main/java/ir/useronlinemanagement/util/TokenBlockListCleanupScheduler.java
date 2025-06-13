package ir.useronlinemanagement.util;

import ir.useronlinemanagement.repository.TokenBlockListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class TokenBlockListCleanupScheduler {
    private final TokenBlockListRepository tokenBlockListRepository;

    @Scheduled(cron = "0 0 0 * * ?") // هر شب ساعت 12
    public void cleanExpiredTokens() {
        tokenBlockListRepository.deleteAllExpiredTokens(Instant.now());
    }
}

