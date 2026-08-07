package com.my_hourly.notification.api.request;

import com.my_hourly.notification.enums.UploadType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementRequest {

    @NotBlank(message = "Title is required.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @NotBlank(message = "Message is required.")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters.")
    private String message;

    @NotBlank(message = "Message is required.")
    private UploadType uploadType;
}