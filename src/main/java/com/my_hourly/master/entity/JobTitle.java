package com.my_hourly.master.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_titles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobTitle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_title_code", nullable = false, unique = true)
    private String jobTitleCode;

    @Column(name = "job_title", nullable = false, length = 100)
    private String jobTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designation_id", nullable = false)
    private Designation designation;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

}