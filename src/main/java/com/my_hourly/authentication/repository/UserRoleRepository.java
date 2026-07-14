package com.my_hourly.authentication.repository;

import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository
        extends JpaRepository<UserRole, UUID> {

    List<UserRole> findByUserId(UUID userId);

    List<UserRole> findAllByUser(User user);
}
