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
        name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_name", columnNames = "name")
        }
)
public class Role extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    @Builder.Default
    private Boolean active = true;
}