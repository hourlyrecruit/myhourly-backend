package com.my_hourly.notification.repository;

import com.my_hourly.employee.entity.Employee;
import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.entity.Notification;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Get employee notifications ordered by latest first.
     */
    Page<Notification> findByEmployeeOrderByCreatedAtDesc(
            Employee employee,
            Pageable pageable
    );

    /**
     * Count unread notifications.
     */
    long countByEmployeeAndIsReadFalse(
            Employee employee
    );

    /**
     * Check duplicate notification.
     */
    boolean existsByEmployeeIdAndReferenceTypeAndReferenceIdAndNotificationType(
            Long employeeId,
            ReferenceType referenceType,
            Long referenceId,
            NotificationType notificationType
    );

    /**
     * Find notification by employee.
     */
    Notification findByIdAndEmployee(
            Long id,
            Employee employee
    );

    /**
     * Mark one notification as read.
     */
    @Modifying
    @Query("""
            UPDATE Notification n
               SET n.isRead = true
             WHERE n.id = :notificationId
               AND n.employee = :employee
            """)
    int markAsRead(
            @Param("notificationId") Long notificationId,
            @Param("employee") Employee employee
    );

    /**
     * Mark all notifications as read.
     */
    @Modifying
    @Query("""
            UPDATE Notification n
               SET n.isRead = true
             WHERE n.employee = :employee
               AND n.isRead = false
            """)
    int markAllAsRead(
            @Param("employee") Employee employee
    );


    List<Notification> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndReferenceTypeNotIn(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            List<ReferenceType> excludedTypes
    );
}