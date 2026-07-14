package com.my_hourly.organization.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "designations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Designation extends BaseEntity {

    @Column(name = "designation_code", nullable = false, unique = true)
    private String designationCode;

    @Column(name = "designation_name", nullable = false)
    private String designationName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 255)
    private String description;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}