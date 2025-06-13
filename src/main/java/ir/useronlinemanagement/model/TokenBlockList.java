package ir.useronlinemanagement.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "token_block_list")
@Data
public class TokenBlockList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", unique = true, nullable = false, length = 500)
    private String token;

    @Column(name = "blocked_at")
    private Instant blockedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;
}

