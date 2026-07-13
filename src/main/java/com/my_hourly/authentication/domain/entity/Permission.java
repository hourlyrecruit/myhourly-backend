package com.my_hourly.authentication.domain.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "permissions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_permission_name", columnNames = "name")
        }
)
public class Permission extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;
}
