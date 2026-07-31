package com.my_hourly.notification.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "announcements")
public class Announcement extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 1000)
    private String message;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "announcement_attachment_urls", joinColumns = @JoinColumn(name = "announcement_id"))
    @Column(name = "attachment_url", length = 1000)
    @Builder.Default
    private List<String> attachmentUrls = new ArrayList<>();
}
