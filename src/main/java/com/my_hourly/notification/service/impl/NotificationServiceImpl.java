package com.my_hourly.notification.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.common.service.FileStorageServiceB2;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;
import com.my_hourly.attendance.repository.AttendanceRepository;
import com.my_hourly.leave.entity.LeaveRequest;
import com.my_hourly.leave.repository.LeaveRequestRepository;
import com.my_hourly.notification.api.request.AnnouncementRequest;
import com.my_hourly.notification.api.response.NotificationResponse;
import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.entity.Notification;
import com.my_hourly.notification.enums.NotificationPriority;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.mapper.NotificationMapper;
import com.my_hourly.notification.repository.AnnouncementRepository;
import com.my_hourly.notification.repository.NotificationRepository;
import com.my_hourly.notification.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeService employeeService;
    private final AnnouncementRepository announcementRepository;
    private final FileStorageServiceB2 fileStorageServiceB2;
    private final AttendanceSettingsService attendanceSettingsService;

    private Employee getCurrentEmployee() {
        Employee currentEmployee = employeeService.getCurrentEmployee();
        return currentEmployee;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getMyNotifications(
            int page,
            int size
    ) {

        Employee employee = getCurrentEmployee();

        Pageable pageable = PageRequest.of(page, size);

        Page<Notification> notifications =
                notificationRepository.findByEmployeeOrderByCreatedAtDesc(
                        employee,
                        pageable
                );

        Set<Long> announcementIds = notifications.getContent().stream()
                .filter(n -> ReferenceType.ANNOUNCEMENT.equals(n.getReferenceType()) && n.getReferenceId() != null && n.getReferenceId() > 0)
                .map(Notification::getReferenceId)
                .collect(Collectors.toSet());

        Map<Long, List<String>> announcementAttachmentMap = new HashMap<>();
        if (!announcementIds.isEmpty()) {
            List<Announcement> announcements = announcementRepository.findAllById(announcementIds);
            for (Announcement announcement : announcements) {
                announcementAttachmentMap.put(announcement.getId(), announcement.getAttachmentUrls());
            }
        }

        return PageResponse.<NotificationResponse>builder()
                .content(
                        notifications.getContent()
                                .stream()
                                .map(notification -> {
                                    List<String> urls = null;
                                    if (ReferenceType.ANNOUNCEMENT.equals(notification.getReferenceType()) && notification.getReferenceId() != null) {
                                        urls = announcementAttachmentMap.getOrDefault(notification.getReferenceId(), Collections.emptyList());
                                    }
                                    return notificationMapper.toResponse(notification, urls);
                                })
                                .toList()
                )
                .page(notifications.getNumber())
                .size(notifications.getSize())
                .totalElements(notifications.getTotalElements())
                .totalPages(notifications.getTotalPages())
                .first(notifications.isFirst())
                .last(notifications.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount() {

        return notificationRepository.countByEmployeeAndIsReadFalse(
                getCurrentEmployee()
        );
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {

        Employee employee = getCurrentEmployee();

        Notification notification =
                notificationRepository.findByIdAndEmployee(
                        notificationId,
                        employee
                );

        if (notification == null) {
            throw new ResourceNotFoundException(
                    "Notification not found.", ErrorCode.RESOURCE_NOT_FOUND
            );
        }

        notificationRepository.markAsRead(
                notificationId,
                employee
        );
    }

    @Override
    @Transactional
    public void markAllAsRead() {

        notificationRepository.markAllAsRead(
                getCurrentEmployee()
        );
    }

    @Override
    @Transactional
    public void createNotification(
            Employee employee,
            String title,
            String postType,
            String message,
            NotificationType notificationType,
            NotificationPriority priority,
            ReferenceType referenceType,
            Long referenceId
    ) {

        if (employee == null) {
            return;
        }

        boolean exists = notificationRepository
                .existsByEmployeeIdAndReferenceTypeAndReferenceIdAndNotificationType(
                        employee.getId(),
                        referenceType,
                        referenceId,
                        notificationType
                );

        if (exists) {
            return;
        }

        Notification notification = Notification.builder()
                .employee(employee)
                .title(title)
                .message(message)
                .notificationType(notificationType)
                .priority(priority)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void createAnnouncement(AnnouncementRequest request, List<MultipartFile> attachments) {

        List<String> attachmentUrls = new ArrayList<>();
        if (attachments != null && !attachments.isEmpty()) {
            for (MultipartFile file : attachments) {
                if (file != null && !file.isEmpty()) {
                    String fileUrl = fileStorageServiceB2.upload(file, "announcements");
                    attachmentUrls.add(fileUrl);
                }
            }
        }

        saveAnnouncementAndBroadcast(request, attachmentUrls);
    }

    @Transactional
    protected void saveAnnouncementAndBroadcast(AnnouncementRequest request, List<String> attachmentUrls) {

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .uploadType(request.getUploadType())
                .message(request.getMessage())
                .attachmentUrls(attachmentUrls)
                .build();

        announcement = announcementRepository.save(announcement);

        List<Employee> employees = employeeRepository.findAll();

        for (Employee employee : employees) {

            createNotification(
                    employee,
                    request.getTitle(),
                    request.getUploadType().toString(),
                    request.getMessage(),
                    NotificationType.ANNOUNCEMENT,
                    NotificationPriority.HIGH,
                    ReferenceType.ANNOUNCEMENT,
                    announcement.getId()
            );
        }
    }

    @Override
    @Transactional
    public void processAttendanceNotifications() {

        LocalDate today = LocalDate.now();

        List<Attendance> attendances =
                attendanceRepository.findByAttendanceDate(today);

        for (Attendance attendance : attendances) {

            switch (attendance.getAttendanceStatus()) {

                case LATE -> createAttendanceNotification(
                        attendance,
                        NotificationType.LATE_CHECK_IN,
                        NotificationPriority.MEDIUM,
                        "Late Check-in",
                        "You checked in "
                                + attendance.getLateMinutes()
                                + " minutes late today."
                );

                case ABSENT -> createAttendanceNotification(
                        attendance,
                        NotificationType.ABSENT,
                        NotificationPriority.HIGH,
                        "Absent",
                        "You are marked absent today."
                );

                case MISSED_CHECKOUT -> createAttendanceNotification(
                        attendance,
                        NotificationType.MISSED_CHECKOUT,
                        NotificationPriority.HIGH,
                        "Missed Checkout",
                        "You forgot to checkout today."
                );

                default -> {
                    // No notification required
                }
            }
        }
    }

    @Override
    @Transactional
    public void processCheckoutReminderNotifications() {

        LocalDate today = LocalDate.now();

        AttendanceSettings settings = attendanceSettingsService.getSettings();
        String officeEndTime = settings.getOfficeEndTime().toString();

        List<Attendance> attendances =
                attendanceRepository.findByAttendanceDateAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(today);

        for (Attendance attendance : attendances) {

            if (attendance.getAttendanceStatus() == AttendanceStatus.MISSED_CHECKOUT
                    || attendance.getAttendanceStatus() == AttendanceStatus.LEAVE
                    || attendance.getAttendanceStatus() == AttendanceStatus.ABSENT) {
                continue;
            }

            createAttendanceNotification(
                    attendance,
                    NotificationType.CHECKOUT_REMINDER,
                    NotificationPriority.MEDIUM,
                    "Checkout Reminder",
                    "Your work day ended at " + officeEndTime + ". Please don't forget to check out."
            );
        }
    }

    private void createAttendanceNotification(
            Attendance attendance,
            NotificationType type,
            NotificationPriority priority,
            String title,
            String message
    ) {

        createNotification(
                attendance.getEmployee(),
                title,
                "Attendance",
                message,
                type,
                priority,
                ReferenceType.ATTENDANCE,
                attendance.getId()
        );

        notifyReportingManager(attendance, title);
    }

    private void notifyReportingManager(
            Attendance attendance,
            String title
    ) {

        Employee manager = attendance.getEmployee().getReportingManager();

        if (manager == null) {
            return;
        }

        createNotification(
                manager,
                title,
                attendance.getEmployee().getFirstName()
                        + " "
                        + attendance.getEmployee().getLastName()
                        + " has "
                        + title.toLowerCase()
                        + ".",
                "Attendance",
                NotificationType.GENERAL,
                NotificationPriority.MEDIUM,
                ReferenceType.ATTENDANCE,
                attendance.getId()
        );
    }

    @Override
    @Transactional
    public void processLeaveNotifications() {

        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusMinutes(5);

        List<LeaveRequest> leaveRequests =
                leaveRequestRepository.findByUpdatedAtBetween(
                        startTime,
                        endTime
                );

        for (LeaveRequest leaveRequest : leaveRequests) {

            switch (leaveRequest.getStatus()) {

                case APPROVED -> createLeaveNotification(
                        leaveRequest,
                        NotificationType.APPROVED,
                        NotificationPriority.MEDIUM,
                        "Leave Approved",
                        "Your leave request has been approved by your manager."
                );

//                case MANAGER_APPROVED -> createLeaveNotification(
//                        leaveRequest,
//                        NotificationType.LEAVE_MANAGER_APPROVED,
//                        NotificationPriority.MEDIUM,
//                        "Leave Approved",
//                        "Your leave request has been approved by your manager."
//                );

//                case HR_APPROVED -> createLeaveNotification(
//                        leaveRequest,
//                        NotificationType.LEAVE_HR_APPROVED,
//                        NotificationPriority.MEDIUM,
//                        "Leave Approved",
//                        "Your leave request has been approved by HR."
//                );

                case REJECTED -> createLeaveNotification(
                        leaveRequest,
                        NotificationType.LEAVE_REJECTED,
                        NotificationPriority.HIGH,
                        "Leave Rejected",
                        "Your leave request has been rejected."
                );

                case CANCELLED -> createLeaveNotification(
                        leaveRequest,
                        NotificationType.LEAVE_CANCELLED,
                        NotificationPriority.MEDIUM,
                        "Leave Cancelled",
                        "Your leave request has been cancelled."
                );

                default -> {
                    // Ignore PENDING and other statuses
                }
            }
        }
    }

    private void createLeaveNotification(
            LeaveRequest leaveRequest,
            NotificationType notificationType,
            NotificationPriority priority,
            String title,
            String message
    ) {

        createNotification(
                leaveRequest.getEmployee(),
                title,
                "leave",
                message,
                notificationType,
                priority,
                ReferenceType.LEAVE,
                leaveRequest.getId()
        );

        notifyManagerForLeave(leaveRequest, notificationType);
    }

    private void notifyManagerForLeave(
            LeaveRequest leaveRequest,
            NotificationType notificationType
    ) {

        Employee manager = leaveRequest.getEmployee().getReportingManager();

        if (manager == null) {
            return;
        }

        String message = switch (notificationType) {

            case APPROVED, LEAVE_MANAGER_APPROVED -> leaveRequest.getEmployee().getFirstName()
                    + " leave request has been approved by the manager.";

            case LEAVE_HR_APPROVED -> leaveRequest.getEmployee().getFirstName()
                    + " leave request has been approved by HR.";

            case LEAVE_REJECTED -> leaveRequest.getEmployee().getFirstName()
                    + " leave request has been rejected.";

            case LEAVE_CANCELLED -> leaveRequest.getEmployee().getFirstName()
                    + " leave request has been cancelled.";

            default -> null;
        };

        if (message == null) {
            return;
        }

        createNotification(
                manager,
                "Leave Update",
                "leave",
                message,
                NotificationType.GENERAL,
                NotificationPriority.MEDIUM,
                ReferenceType.LEAVE,
                leaveRequest.getId()
        );
    }

//    @Override
//    public void processBirthdayNotifications() {
//
//        LocalDate today = LocalDate.now();
//
//        List<Employee> employees = employeeRepository.findByActiveTrue()
//                .stream()
//                .filter(employee ->
//                        employee.getDateOfBirth() != null
//                                && employee.getDateOfBirth().getMonthValue() == today.getMonthValue()
//                                && employee.getDateOfBirth().getDayOfMonth() == today.getDayOfMonth()
//                )
//                .toList();
//
//        for (Employee employee : employees) {
//
//            createNotification(
//                    employee,
//                    "Happy Birthday 🎉",
//                    "Wishing you a wonderful birthday. Have a fantastic year ahead!",
//                    NotificationType.BIRTHDAY,
//                    NotificationPriority.LOW,
//                    ReferenceType.EMPLOYEE,
//                    employee.getId()
//            );
//        }
//    }
//
//    @Override
//    public void processWorkAnniversaryNotifications() {
//
//        LocalDate today = LocalDate.now();
//
//        List<Employee> employees = employeeRepository.findByActiveTrue()
//                .stream()
//                .filter(employee ->
//                        employee.getDateOfJoining() != null
//                                && employee.getDateOfJoining().getMonthValue() == today.getMonthValue()
//                                && employee.getDateOfJoining().getDayOfMonth() == today.getDayOfMonth()
//                )
//                .toList();
//
//        for (Employee employee : employees) {
//
//            createNotification(
//                    employee,
//                    "Happy Work Anniversary 🎉",
//                    "Congratulations on your work anniversary. Thank you for being part of the organization.",
//                    NotificationType.WORK_ANNIVERSARY,
//                    NotificationPriority.LOW,
//                    ReferenceType.EMPLOYEE,
//                    employee.getId()
//            );
//        }
//    }
//
//    @Override
//    public void processHolidayNotifications() {
//
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//
//        holidayRepository.findByHolidayDate(tomorrow)
//                .ifPresent(holiday -> {
//
//                    List<Employee> employees =
//                            employeeRepository.findAll();
//
//                    for (Employee employee : employees) {
//
//                        createNotification(
//                                employee,
//                                "Holiday Tomorrow",
//                                "Tomorrow is "
//                                        + holiday.getHolidayName()
//                                        + ". Enjoy your holiday!",
//                                NotificationType.HOLIDAY,
//                                NotificationPriority.MEDIUM,
//                                ReferenceType.HOLIDAY,
//                                holiday.getId()
//                        );
//                    }
//                });
//    }
}
