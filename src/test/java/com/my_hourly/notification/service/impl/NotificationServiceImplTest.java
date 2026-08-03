package com.my_hourly.notification.service.impl;

import com.my_hourly.common.payload.response.PageResponse;
import com.my_hourly.common.service.FileStorageServiceB2;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.employee.repository.EmployeeRepository;
import com.my_hourly.employee.service.EmployeeService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Spy
    private NotificationMapper notificationMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private FileStorageServiceB2 fileStorageService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
    }

    @Test
    void createAnnouncement_withAttachments_createsAnnouncementAndNotifications() {
        AnnouncementRequest request = new AnnouncementRequest();
        request.setTitle("Company Outing");
        request.setMessage("We are going on a trip!");

        MockMultipartFile file = new MockMultipartFile("attachments", "itinerary.pdf", "application/pdf", "dummy data".getBytes());
        List<MultipartFile> attachments = List.of(file);

        when(fileStorageService.upload(any(MultipartFile.class), eq("announcements")))
                .thenReturn("http://cloudinary.com/itinerary.pdf");

        Announcement savedAnnouncement = Announcement.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .attachmentUrls(List.of("http://cloudinary.com/itinerary.pdf"))
                .build();
        savedAnnouncement.setId(100L);

        when(announcementRepository.save(any(Announcement.class))).thenReturn(savedAnnouncement);
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        when(notificationRepository.existsByEmployeeIdAndReferenceTypeAndReferenceIdAndNotificationType(
                eq(1L), eq(ReferenceType.ANNOUNCEMENT), eq(100L), eq(NotificationType.ANNOUNCEMENT)
        )).thenReturn(false);

        notificationService.createAnnouncement(request, attachments);

        verify(fileStorageService, times(1)).upload(file, "announcements");
        verify(announcementRepository, times(1)).save(any(Announcement.class));

        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(notificationCaptor.capture());

        Notification captured = notificationCaptor.getValue();
        assertEquals("Company Outing", captured.getTitle());
        assertEquals("We are going on a trip!", captured.getMessage());
        assertEquals(ReferenceType.ANNOUNCEMENT, captured.getReferenceType());
        assertEquals(100L, captured.getReferenceId());
    }

    @Test
    void getMyNotifications_populatesAttachmentUrls_forAnnouncementNotifications() {
        when(employeeService.getCurrentEmployee()).thenReturn(employee);

        Notification announcementNotification = Notification.builder()
                .employee(employee)
                .title("Company Outing")
                .message("We are going on a trip!")
                .notificationType(NotificationType.ANNOUNCEMENT)
                .priority(NotificationPriority.HIGH)
                .referenceType(ReferenceType.ANNOUNCEMENT)
                .referenceId(100L)
                .isRead(false)
                .build();
        announcementNotification.setId(10L);

        PageImpl<Notification> page = new PageImpl<>(List.of(announcementNotification), PageRequest.of(0, 10), 1);
        when(notificationRepository.findByEmployeeOrderByCreatedAtDesc(eq(employee), any(Pageable.class)))
                .thenReturn(page);

        Announcement announcement = Announcement.builder()
                .title("Company Outing")
                .message("We are going on a trip!")
                .attachmentUrls(List.of("http://cloudinary.com/itinerary.pdf"))
                .build();
        announcement.setId(100L);

        when(announcementRepository.findAllById(Set.of(100L))).thenReturn(List.of(announcement));

        PageResponse<NotificationResponse> response = notificationService.getMyNotifications(0, 10);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        NotificationResponse notificationResponse = response.getContent().get(0);
        assertEquals("Company Outing", notificationResponse.getTitle());
        assertEquals(List.of("http://cloudinary.com/itinerary.pdf"), notificationResponse.getAttachmentUrls());
        verify(announcementRepository, times(1)).findAllById(Set.of(100L));
    }
}
