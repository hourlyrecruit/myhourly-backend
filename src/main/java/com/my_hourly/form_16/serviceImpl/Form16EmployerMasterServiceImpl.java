package com.my_hourly.form_16.service.impl;

import com.my_hourly.form_16.dto.Form16EmployerMasterRequest;
import com.my_hourly.form_16.dto.Form16EmployerMasterResponse;
import com.my_hourly.form_16.entity.Form16EmployerMaster;
import com.my_hourly.form_16.repository.Form16EmployerMasterRepository;
import com.my_hourly.form_16.service.Form16EmployerMasterService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class Form16EmployerMasterServiceImpl
        implements Form16EmployerMasterService {

    private final Form16EmployerMasterRepository repository;


    // =========================================================
    // CREATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse createEmployerMaster(
            Form16EmployerMasterRequest request) {

        validateRequest(request);

        Form16EmployerMaster employerMaster =
                Form16EmployerMaster.builder()
                        .employerName(request.getEmployerName())
                        .employerAddress(request.getEmployerAddress())
                        .employerPhone(request.getEmployerPhone())
                        .employerEmail(request.getEmployerEmail())
                        .deductorPan(request.getDeductorPan())
                        .deductorTan(request.getDeductorTan())
                        .citTdsAddress(request.getCitTdsAddress())
                        .active(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        Form16EmployerMaster saved =
                repository.save(employerMaster);

        return mapToResponse(saved);
    }


    // =========================================================
    // GET ACTIVE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16EmployerMasterResponse getActiveEmployerMaster() {

        Form16EmployerMaster employerMaster =
                repository.findByActiveTrue()
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Active employer master not found"
                                )
                        );

        return mapToResponse(employerMaster);
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16EmployerMasterResponse getEmployerMasterById(
            Long id) {

        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(employerMaster);
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse updateEmployerMaster(
            Long id,
            Form16EmployerMasterRequest request) {

        validateRequest(request);

        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );

        employerMaster.setEmployerName(
                request.getEmployerName()
        );

        employerMaster.setEmployerAddress(
                request.getEmployerAddress()
        );

        employerMaster.setEmployerPhone(
                request.getEmployerPhone()
        );

        employerMaster.setEmployerEmail(
                request.getEmployerEmail()
        );

        employerMaster.setDeductorPan(
                request.getDeductorPan()
        );

        employerMaster.setDeductorTan(
                request.getDeductorTan()
        );

        employerMaster.setCitTdsAddress(
                request.getCitTdsAddress()
        );

        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );

        Form16EmployerMaster updated =
                repository.save(employerMaster);

        return mapToResponse(updated);
    }


    // =========================================================
    // ACTIVATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse activateEmployerMaster(
            Long id) {

        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );

        /*
         * Deactivate all currently active employer masters
         */
        List<Form16EmployerMaster> allMasters =
                repository.findAll();

        for (Form16EmployerMaster master : allMasters) {

            if (Boolean.TRUE.equals(master.getActive())) {

                master.setActive(false);
                master.setUpdatedAt(
                        LocalDateTime.now()
                );

                repository.save(master);
            }
        }

        /*
         * Activate selected employer master
         */
        employerMaster.setActive(true);

        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );

        Form16EmployerMaster activated =
                repository.save(employerMaster);

        return mapToResponse(activated);
    }


    // =========================================================
    // DEACTIVATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse deactivateEmployerMaster(
            Long id) {

        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );

        employerMaster.setActive(false);

        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );

        Form16EmployerMaster updated =
                repository.save(employerMaster);

        return mapToResponse(updated);
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteEmployerMaster(Long id) {

        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );

        if (Boolean.TRUE.equals(employerMaster.getActive())) {

            throw new RuntimeException(
                    "Active employer master cannot be deleted. " +
                            "Deactivate it first."
            );
        }

        repository.delete(employerMaster);
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Form16EmployerMasterResponse> getAllEmployerMasters() {

        return repository.findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // VALIDATION
    // =========================================================

    private void validateRequest(
            Form16EmployerMasterRequest request) {

        if (request == null) {
            throw new RuntimeException(
                    "Employer master request cannot be null"
            );
        }

        if (request.getEmployerName() == null ||
                request.getEmployerName().isBlank()) {

            throw new RuntimeException(
                    "Employer name is required"
            );
        }

        if (request.getEmployerAddress() == null ||
                request.getEmployerAddress().isBlank()) {

            throw new RuntimeException(
                    "Employer address is required"
            );
        }

        if (request.getDeductorPan() == null ||
                request.getDeductorPan().isBlank()) {

            throw new RuntimeException(
                    "Deductor PAN is required"
            );
        }

        if (request.getDeductorTan() == null ||
                request.getDeductorTan().isBlank()) {

            throw new RuntimeException(
                    "Deductor TAN is required"
            );
        }

        if (request.getCitTdsAddress() == null ||
                request.getCitTdsAddress().isBlank()) {

            throw new RuntimeException(
                    "CIT TDS address is required"
            );
        }
    }


    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private Form16EmployerMasterResponse mapToResponse(
            Form16EmployerMaster entity) {

        return Form16EmployerMasterResponse.builder()
                .id(entity.getId())
                .employerName(entity.getEmployerName())
                .employerAddress(entity.getEmployerAddress())
                .employerPhone(entity.getEmployerPhone())
                .employerEmail(entity.getEmployerEmail())
                .deductorPan(entity.getDeductorPan())
                .deductorTan(entity.getDeductorTan())
                .citTdsAddress(entity.getCitTdsAddress())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}