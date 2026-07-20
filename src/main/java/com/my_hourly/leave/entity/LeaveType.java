package com.my_hourly.leave.entity;

import com.my_hourly.common.entity.BaseEntity;
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

    /**
     * Whether this leave type is paid.
     */
    @Column(nullable = false)
    private Boolean paid;

    /**
     * Total annual days allocated for this leave type (e.g. 24).
     */
    @Column(nullable = false)
    private Integer allocatedDays;

    /**
     * Recommended leave days per month for expiry calculation (e.g. 2).
     * Only used by the month-end scheduler when carryForwardAllowed = false.
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer monthlyGuideline = 2;

    /**
     * When true, unused monthly guideline does NOT expire at month-end.
     * When false, (monthlyGuideline - usedThisMonth) is deducted from the balance.
     */
    @Column(nullable = false)
    @Builder.Default
    private boolean carryForwardAllowed = false;

    @Column(nullable = false)
    private Boolean active;
}