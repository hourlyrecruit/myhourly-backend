package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.leave.enums.LeaveAllocationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "leave_types",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Integer allocatedDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LeaveAllocationType allocationType;

    @Column(nullable = false)
    @Builder.Default
    private boolean carryForwardAllowed = false;

    @Column(nullable = false)
    @Builder.Default
    private Integer maxCarryForwardDays = 0;

    @Column(nullable = false)
    @Builder.Default
    private boolean expireUnusedLeaves = true;

    @Column(nullable = false)
    private Boolean active;
}