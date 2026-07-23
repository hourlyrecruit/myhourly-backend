package com.my_hourly.celebration.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "upcoming_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UpcomingEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    private String location;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false)
    private LocalDate eventDate;
    private LocalDateTime createdAt;
    private Long createdBy;
}
