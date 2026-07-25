package com.my_hourly.celebration.entity;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="celebration_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"post_id","user_id"})
        })
public class CelebrationLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="post_id")
    private CelebrationPost celebrationPost;
    private Long userId;
    private LocalDateTime likedAt;

}