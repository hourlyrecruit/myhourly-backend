package com.my_hourly.authentication.repository;

import com.my_hourly.authentication.entity.Role;
import com.my_hourly.authentication.entity.RolePermission;
import com.my_hourly.authentication.entity.User;
import com.my_hourly.authentication.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository
        extends JpaRepository<User, UUID> {

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);



}
