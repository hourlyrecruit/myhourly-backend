package com.my_hourly.authentication.domain.repository;
import com.my_hourly.authentication.domain.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository
        extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);

}
