package com.my_hourly.authentication.repository;

import com.my_hourly.authentication.entity.Role;
import com.my_hourly.authentication.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, UUID> {

    List<RolePermission> findByRoleId(UUID roleId);

    List<RolePermission> findAllByRole(Role role);
}
