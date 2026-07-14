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
        name = "revoked_tokens",
        indexes = {
                @Index(
                        name = "idx_revoked_token",
                        columnList = "token"
                )
        }
)
public class RevokedToken extends BaseEntity {

    @Column(nullable = false, unique = true, length = 1000)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

}
