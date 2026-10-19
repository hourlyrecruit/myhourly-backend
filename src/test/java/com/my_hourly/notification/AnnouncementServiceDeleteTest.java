package com.my_hourly.notification;

import com.my_hourly.common.enums.ErrorCode;
import com.my_hourly.common.exception.ResourceNotFoundException;
import com.my_hourly.notification.entity.Announcement;
import com.my_hourly.notification.enums.ReferenceType;
import com.my_hourly.notification.enums.UploadType;
import com.my_hourly.notification.mapper.NotificationMapper;
import com.my_hourly.notification.repository.AnnouncementRepository;
import com.my_hourly.notification.repository.NotificationRepository;
import com.my_hourly.notification.service.impl.AnnouncementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceDeleteTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    private Announcement announcement;

    @BeforeEach
    void setUp() {
        announcement = Announcement.builder()
                .title("Office closed")
                .message("The office will remain closed tomorrow.")
                .uploadType(UploadType.POST)
                .build();
        announcement.setId(7L);
    }

    @Test
    @DisplayName("Deleting an announcement also deletes the notifications broadcast for it")
    void deleteAnnouncement_Success() {
        when(announcementRepository.findById(7L)).thenReturn(Optional.of(announcement));
        when(notificationRepository.deleteByReferenceTypeAndReferenceId(
                ReferenceType.ANNOUNCEMENT, 7L)).thenReturn(3);

        announcementService.deleteAnnouncement(7L);

        verify(notificationRepository)
                .deleteByReferenceTypeAndReferenceId(ReferenceType.ANNOUNCEMENT, 7L);
        verify(announcementRepository).delete(announcement);
    }

    @Test
    @DisplayName("Deleting a missing announcement fails without touching notifications")
    void deleteAnnouncement_NotFound() {
        when(announcementRepository.findById(99L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> announcementService.deleteAnnouncement(99L)
        );

        assertEquals(ErrorCode.ANNOUNCEMENT_NOT_FOUND, exception.getErrorCode());
        verifyNoInteractions(notificationRepository);
        verify(announcementRepository, never()).delete(any());
    }
}
