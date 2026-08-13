package com.my_hourly.notification.service.impl;

import com.my_hourly.attendance.entity.Attendance;
import com.my_hourly.attendance.entity.AttendanceStatus;
import com.my_hourly.settings.attendance.entity.AttendanceSettings;
import com.my_hourly.settings.attendance.service.AttendanceSettingsService;
import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.BadRequestException;
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
import com.my_hourly.notification.api.response.UpcomingBirthdayResponse;
import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.entity.Notification;
import com.my_hourly.notification.enums.NotificationPriority;
import com.my_hourly.notification.enums.NotificationType;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.mapper.NotificationMapper;
import com.my_hourly.notification.repository.AnnouncementRepository;
import com.my_hourly.notification.repository.NotificationRepository;
import com.my_hourly.notification.service.NotificationService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public List<UpcomingBirthdayResponse> getUpcomingBirthdays(int days) {

        if (days < 1 || days > 365) {
            throw new BadRequestException(
                    "days must be between 1 and 365.",
                    ErrorCode.BAD_REQUEST
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate windowEnd = today.plusDays(days);

        return employeeRepository
                .findByActiveTrueAndDateOfBirthIsNotNull()
                .stream()
                .map(employee -> new UpcomingBirthday(
                        employee,
                        nextBirthday(employee.getDateOfBirth(), today)
                ))
                .filter(upcoming -> !upcoming.date().isAfter(windowEnd))
                .sorted(Comparator
                        .comparing(UpcomingBirthday::date)
                        .thenComparing(upcoming -> upcoming.employee().getFirstName())
                )
                .map(upcoming -> toUpcomingBirthdayResponse(
                        upcoming.employee(),
                        upcoming.date(),
                        today
                ))
                .toList();
    }

    /**
     * The next date on which {@code dateOfBirth} falls, taking the current
     * year (or the next one once this year's birthday has passed) into account.
     */
    static LocalDate nextBirthday(LocalDate dateOfBirth, LocalDate today) {

        LocalDate next = dateOfBirth.withYear(today.getYear());

        if (next.isBefore(today)) {
            next = next.plusYears(1);
        }

        return next;
    }

    private UpcomingBirthdayResponse toUpcomingBirthdayResponse(
            Employee employee,
            LocalDate upcomingBirthdayDate,
            LocalDate today
    ) {

        String employeeName = employee.getFirstName();

        if (employee.getLastName() != null
                && !employee.getLastName().isBlank()) {
            employeeName += " " + employee.getLastName();
        }

        return new UpcomingBirthdayResponse(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employeeName,
                employee.getDateOfBirth(),
                upcomingBirthdayDate,
                ChronoUnit.DAYS.between(today, upcomingBirthdayDate)
        );
    }

    private record UpcomingBirthday(
            Employee employee,
            LocalDate date
    ) {
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
    @Transactional
    public void createNotificationsBulk(List<NotificationItem> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        // Dedupe within the batch with first-wins semantics, matching the
        // per-call exists-check of createNotification.
        Set<String> seen = new HashSet<>();
        List<NotificationItem> uniqueItems = new ArrayList<>();

        for (NotificationItem item : items) {
            if (item.employee() == null || item.referenceId() == null) {
                continue;
            }
            String key = item.employee().getId()
                    + "|" + item.referenceType()
                    + "|" + item.notificationType()
                    + "|" + item.referenceId();
            if (seen.add(key)) {
                uniqueItems.add(item);
            }
        }

        if (uniqueItems.isEmpty()) {
            return;
        }

        // Group by (referenceType, notificationType) so the duplicate check runs
        // once per group instead of once per recipient.
        Map<NotificationGroupKey, List<NotificationItem>> groups = uniqueItems.stream()
                .collect(Collectors.groupingBy(
                        item -> new NotificationGroupKey(
                                item.referenceType(),
                                item.notificationType()
                        )
                ));

        Set<String> existingKeys = new HashSet<>();

        for (Map.Entry<NotificationGroupKey, List<NotificationItem>> entry
                : groups.entrySet()) {

            NotificationGroupKey key = entry.getKey();

            List<Long> employeeIds = entry.getValue().stream()
                    .map(item -> item.employee().getId())
                    .distinct()
                    .toList();

            List<Long> referenceIds = entry.getValue().stream()
                    .map(NotificationItem::referenceId)
                    .distinct()
                    .toList();

            for (Object[] row : notificationRepository
                    .findExistingEmployeeIdAndReferenceId(
                            employeeIds,
                            key.referenceType(),
                            key.notificationType(),
                            referenceIds
                    )) {

                existingKeys.add(row[0] + "|" + row[1]);
            }
        }

        List<Notification> toCreate = uniqueItems.stream()
                .filter(item -> !existingKeys.contains(
                        item.employee().getId() + "|" + item.referenceId()))
                .map(item -> Notification.builder()
                        .employee(item.employee())
                        .title(item.title())
                        .message(item.message())
                        .notificationType(item.notificationType())
                        .priority(item.priority())
                        .referenceType(item.referenceType())
                        .referenceId(item.referenceId())
                        .isRead(false)
                        .build())
                .toList();

        if (!toCreate.isEmpty()) {
            notificationRepository.saveAll(toCreate);
        }
    }

    private record NotificationGroupKey(
            ReferenceType referenceType,
            NotificationType notificationType
    ) {
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

        Announcement savedAnnouncement = announcementRepository.save(announcement);

        List<Employee> employees = employeeRepository.findAll();

        List<NotificationItem> items = employees.stream()
                .map(employee -> new NotificationItem(
                        employee,
                        request.getTitle(),
                        request.getMessage(),
                        NotificationType.ANNOUNCEMENT,
                        NotificationPriority.HIGH,
                        ReferenceType.ANNOUNCEMENT,
                        savedAnnouncement.getId()
                ))
                .toList();

        createNotificationsBulk(items);
    }

    @Override
    @Transactional
    public void processAttendanceNotifications() {

        LocalDate today = LocalDate.now();

        // Only rows that can produce a notification; avoids scanning every
        // attendance record of the day every 5 minutes.
        List<Attendance> attendances =
                attendanceRepository.findByAttendanceDateAndAttendanceStatusIn(
                        today,
                        List.of(
                                AttendanceStatus.LATE,
                                AttendanceStatus.ABSENT,
                                AttendanceStatus.MISSED_CHECKOUT
                        )
                );

        List<NotificationItem> items = new ArrayList<>();

        for (Attendance attendance : attendances) {

            switch (attendance.getAttendanceStatus()) {

                case LATE -> items.addAll(attendanceNotificationItems(
                        attendance,
                        NotificationType.LATE_CHECK_IN,
                        NotificationPriority.MEDIUM,
                        "Late Check-in",
                        "You checked in "
                                + attendance.getLateMinutes()
                                + " minutes late today."
                ));

                case ABSENT -> items.addAll(attendanceNotificationItems(
                        attendance,
                        NotificationType.ABSENT,
                        NotificationPriority.HIGH,
                        "Absent",
                        "You are marked absent today."
                ));

                case MISSED_CHECKOUT -> items.addAll(attendanceNotificationItems(
                        attendance,
                        NotificationType.MISSED_CHECKOUT,
                        NotificationPriority.HIGH,
                        "Missed Checkout",
                        "You forgot to checkout today."
                ));

                default -> {
                    // No notification required
                }
            }
        }

        createNotificationsBulk(items);
    }

    @Override
    @Transactional
    public void processCheckoutReminderNotifications() {

        LocalDate today = LocalDate.now();

        AttendanceSettings settings = attendanceSettingsService.getSettings();
        String officeEndTime = settings.getOfficeEndTime().toString();

        List<Attendance> attendances =
                attendanceRepository.findByAttendanceDateAndCheckInTimeIsNotNullAndCheckOutTimeIsNull(today);

        List<NotificationItem> items = new ArrayList<>();

        for (Attendance attendance : attendances) {

            if (attendance.getAttendanceStatus() == AttendanceStatus.MISSED_CHECKOUT
                    || attendance.getAttendanceStatus() == AttendanceStatus.LEAVE
                    || attendance.getAttendanceStatus() == AttendanceStatus.ABSENT) {
                continue;
            }

            items.addAll(attendanceNotificationItems(
                    attendance,
                    NotificationType.CHECKOUT_REMINDER,
                    NotificationPriority.MEDIUM,
                    "Checkout Reminder",
                    "Your work day ended at " + officeEndTime + ". Please don't forget to check out."
            ));
        }

        createNotificationsBulk(items);
    }

    private List<NotificationItem> attendanceNotificationItems(
            Attendance attendance,
            NotificationType type,
            NotificationPriority priority,
            String title,
            String message
    ) {

        List<NotificationItem> items = new ArrayList<>();

        items.add(new NotificationItem(
                attendance.getEmployee(),
                title,
                message,
                type,
                priority,
                ReferenceType.ATTENDANCE,
                attendance.getId()
        ));

        Employee manager = attendance.getEmployee().getReportingManager();

        if (manager != null) {

            items.add(new NotificationItem(
                    manager,
                    title,
                    "Attendance",
                    NotificationType.GENERAL,
                    NotificationPriority.MEDIUM,
                    ReferenceType.ATTENDANCE,
                    attendance.getId()
            ));
        }

        return items;
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

        List<NotificationItem> items = new ArrayList<>();

        for (LeaveRequest leaveRequest : leaveRequests) {

            switch (leaveRequest.getStatus()) {

                case APPROVED -> items.addAll(leaveNotificationItems(
                        leaveRequest,
                        NotificationType.APPROVED,
                        NotificationPriority.MEDIUM,
                        "Leave Approved",
                        "Your leave request has been approved by your manager."
                ));

//                case MANAGER_APPROVED -> items.addAll(leaveNotificationItems(
//                        leaveRequest,
//                        NotificationType.LEAVE_MANAGER_APPROVED,
//                        NotificationPriority.MEDIUM,
//                        "Leave Approved",
//                        "Your leave request has been approved by your manager."
//                ));

//                case HR_APPROVED -> items.addAll(leaveNotificationItems(
//                        leaveRequest,
//                        NotificationType.LEAVE_HR_APPROVED,
//                        NotificationPriority.MEDIUM,
//                        "Leave Approved",
//                        "Your leave request has been approved by HR."
//                ));

                case REJECTED -> items.addAll(leaveNotificationItems(
                        leaveRequest,
                        NotificationType.LEAVE_REJECTED,
                        NotificationPriority.HIGH,
                        "Leave Rejected",
                        "Your leave request has been rejected."
                ));

                case CANCELLED -> items.addAll(leaveNotificationItems(
                        leaveRequest,
                        NotificationType.LEAVE_CANCELLED,
                        NotificationPriority.MEDIUM,
                        "Leave Cancelled",
                        "Your leave request has been cancelled."
                ));

                default -> {
                    // Ignore PENDING and other statuses
                }
            }
        }

        createNotificationsBulk(items);
    }

    private List<NotificationItem> leaveNotificationItems(
            LeaveRequest leaveRequest,
            NotificationType notificationType,
            NotificationPriority priority,
            String title,
            String message
    ) {

        List<NotificationItem> items = new ArrayList<>();

        items.add(new NotificationItem(
                leaveRequest.getEmployee(),
                title,
                message,
                notificationType,
                priority,
                ReferenceType.LEAVE,
                leaveRequest.getId()
        ));

        Employee manager = leaveRequest.getEmployee().getReportingManager();

        if (manager == null) {
            return items;
        }

        String managerMessage = switch (notificationType) {

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

        if (managerMessage == null) {
            return items;
        }

        items.add(new NotificationItem(
                manager,
                "Leave Update",
                managerMessage,
                NotificationType.GENERAL,
                NotificationPriority.MEDIUM,
                ReferenceType.LEAVE,
                leaveRequest.getId()
        ));

        return items;
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
