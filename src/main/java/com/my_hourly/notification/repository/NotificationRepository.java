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
import java.util.Collection;
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
     * Bulk duplicate check used by schedulers / broadcasts.
     *
     * <p>Returns the (employeeId, referenceId) pairs that already have a
     * notification for the given reference type / notification type, so the
     * caller can insert only the missing ones instead of running one
     * exists-check + save per recipient.</p>
     */
    @Query("""
            SELECT n.employee.id, n.referenceId
            FROM Notification n
            WHERE n.employee.id IN :employeeIds
              AND n.referenceType = :referenceType
              AND n.notificationType = :notificationType
              AND n.referenceId IN :referenceIds
            """)
    List<Object[]> findExistingEmployeeIdAndReferenceId(
            @Param("employeeIds") Collection<Long> employeeIds,
            @Param("referenceType") ReferenceType referenceType,
            @Param("notificationType") NotificationType notificationType,
            @Param("referenceIds") Collection<Long> referenceIds
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


    List<Notification> findByCreatedAtGreaterThanEqualAndCreatedAtLessThanAndReferenceTypeNotInOrderByCreatedAtDesc(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            List<ReferenceType> excludedTypes
    );
}