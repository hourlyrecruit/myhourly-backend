package com.my_hourly.project.entity;

import com.my_hourly.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="clients")
public class Client extends BaseEntity {

    @Column(unique = true,nullable = false)
    private String clientCode;
    @Column(nullable = false)
    private String companyName;
    private String contactPerson;
    @Column(unique = true)
    private String email;
    private  String phone;
    @Column(length = 500)
    private String address;
    @Enumerated(EnumType.STRING)
    private ClientStatus status;
    @OneToMany(mappedBy = "client")
    private List<Project> projects;
}
