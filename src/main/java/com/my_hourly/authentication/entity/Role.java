package com.my_hourly.authentication.entity;
import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.common.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
        }
)
public class Role extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleName name;

    private String description;

    @Builder.Default
    private boolean active = true;
}