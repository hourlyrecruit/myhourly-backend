package com.my_hourly.leave.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.leave.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class LeaveRequest extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Integer totalDays;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private LeaveStatus status;

//    @Column(length = 500)
//    private String rejectionReason;

    /**
     * JSON array of dates that should be counted as working days even if they fall on weekends.
     * Used for sandwich leave policy enforcement.
     */
    @Column(name = "forced_working_days_json", columnDefinition = "TEXT")
    private String forcedWorkingDaysJson;

    /**
     * Transient field for in-memory use - deserialized from forcedWorkingDaysJson.
     */
    @Transient
    private Set<LocalDate> forcedWorkingDays;

    /**
     * Deserializes forcedWorkingDaysJson to forcedWorkingDays Set.
     */
    @PostLoad
    @PostPersist
    @PostUpdate
    private void deserializeForcedWorkingDays() {
        if (forcedWorkingDaysJson != null && !forcedWorkingDaysJson.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                this.forcedWorkingDays = mapper.readValue(
                    forcedWorkingDaysJson, 
                    new TypeReference<Set<LocalDate>>() {}
                );
            } catch (Exception e) {
                log.warn("Failed to deserialize forced working days for leave request {}: {}", 
                    this.getId(), e.getMessage());
                this.forcedWorkingDays = new HashSet<>();
            }
        } else {
            this.forcedWorkingDays = new HashSet<>();
        }
    }

    /**
     * Serializes forcedWorkingDays Set to forcedWorkingDaysJson.
     */
    @PrePersist
    @PreUpdate
    private void serializeForcedWorkingDays() {
        if (forcedWorkingDays != null && !forcedWorkingDays.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                this.forcedWorkingDaysJson = mapper.writeValueAsString(forcedWorkingDays);
            } catch (Exception e) {
                log.error("Failed to serialize forced working days for leave request {}: {}", 
                    this.getId(), e.getMessage());
                this.forcedWorkingDaysJson = null;
            }
        } else {
            this.forcedWorkingDaysJson = null;
        }
    }

    /**
     * Gets the forced working days set, initializing if null.
     */
    public Set<LocalDate> getForcedWorkingDays() {
        if (forcedWorkingDays == null) {
            forcedWorkingDays = new HashSet<>();
        }
        return forcedWorkingDays;
    }

}