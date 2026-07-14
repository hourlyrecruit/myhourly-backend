package com.my_hourly.authentication.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(
                        name = "idx_refresh_token",
                        columnList = "token"
                )
        }
)
public class RefreshToken extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_refresh_token_user")
    )
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    private LocalDateTime revokedAt;


}