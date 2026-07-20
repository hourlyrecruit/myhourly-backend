package com.my_hourly.project.repository;
import java.util.List;
import java.util.Optional;

import com.my_hourly.project.entity.ClientStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.my_hourly.project.entity.Client;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByClientCode(String clientCode);
    boolean existsByClientCode(String clientCode);
    boolean existsByEmail(String email);
    List<Client> findByStatus(ClientStatus status);
    List<Client> findByCompanyNameContainingIgnoreCase(String companyName);

}