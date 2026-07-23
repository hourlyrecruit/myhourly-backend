package com.my_hourly.celebration.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "celebration_posts")
public class CelebrationPost {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;
    private String title;
    @Column(length=5000)
    private String description;
    @Enumerated(EnumType.STRING)
    private com.my_hourly.celebration.entity.CelebrationType celebrationType;
    private LocalDateTime createdAt;
    private Long createdBy;   //admin/HR id
    private boolean active=true;
    @OneToMany(mappedBy = "celebrationPost",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CelebrationTag> tags;

    @OneToMany(mappedBy = "celebrationPost",
            cascade = CascadeType.ALL)
    private List<CelebrationLike> likes;

    @OneToMany(mappedBy = "celebrationPost",
            cascade = CascadeType.ALL)
    private List<CelebrationComment> comments;

}
