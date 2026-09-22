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
            Form16EmployerMasterRequest request
    ) {

        validateRequest(request);


        String pan =
                normalizePan(
                        request.getDeductorPan()
                );

        String tan =
                normalizeTan(
                        request.getDeductorTan()
                );


        // =====================================================
        // DUPLICATE PAN CHECK
        // =====================================================

        if (repository.existsByDeductorPan(pan)) {

            throw new IllegalArgumentException(
                    "Employer master with PAN "
                            + pan
                            + " already exists"
            );
        }


        // =====================================================
        // DUPLICATE TAN CHECK
        // =====================================================

        if (repository.existsByDeductorTan(tan)) {

            throw new IllegalArgumentException(
                    "Employer master with TAN "
                            + tan
                            + " already exists"
            );
        }


        // =====================================================
        // CREATE ENTITY
        // =====================================================

        Form16EmployerMaster employerMaster =
                Form16EmployerMaster.builder()

                        .employerName(
                                request.getEmployerName()
                                        .trim()
                        )

                        .employerAddress(
                                request.getEmployerAddress()
                                        .trim()
                        )

                        .employerPhone(
                                normalize(
                                        request.getEmployerPhone()
                                )
                        )

                        .employerEmail(
                                normalize(
                                        request.getEmployerEmail()
                                )
                        )

                        .deductorPan(pan)

                        .deductorTan(tan)

                        .citTdsAddress(
                                request.getCitTdsAddress()
                                        .trim()
                        )

                        // New employer starts inactive
                        .active(false)

                        .createdAt(
                                LocalDateTime.now()
                        )

                        .updatedAt(
                                LocalDateTime.now()
                        )

                        .build();


        Form16EmployerMaster saved =
                repository.save(
                        employerMaster
                );


        return mapToResponse(saved);
    }


    // =========================================================
    // GET ACTIVE
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16EmployerMasterResponse
    getActiveEmployerMaster() {

        Form16EmployerMaster employerMaster =
                repository.findByActiveTrue()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Active employer master not found"
                                )
                        );


        return mapToResponse(
                employerMaster
        );
    }


    // =========================================================
    // GET BY ID
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public Form16EmployerMasterResponse
    getEmployerMasterById(
            Long id
    ) {

        validateId(id);


        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );


        return mapToResponse(
                employerMaster
        );
    }


    // =========================================================
    // UPDATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse
    updateEmployerMaster(
            Long id,
            Form16EmployerMasterRequest request
    ) {

        validateId(id);

        validateRequest(request);


        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );


        String pan =
                normalizePan(
                        request.getDeductorPan()
                );

        String tan =
                normalizeTan(
                        request.getDeductorTan()
                );


        // =====================================================
        // DUPLICATE PAN CHECK
        // =====================================================

        if (repository.existsByDeductorPan(pan)
                &&
                !pan.equals(
                        employerMaster.getDeductorPan()
                )) {

            throw new IllegalArgumentException(
                    "Another employer master already uses PAN "
                            + pan
            );
        }


        // =====================================================
        // DUPLICATE TAN CHECK
        // =====================================================

        if (repository.existsByDeductorTan(tan)
                &&
                !tan.equals(
                        employerMaster.getDeductorTan()
                )) {

            throw new IllegalArgumentException(
                    "Another employer master already uses TAN "
                            + tan
            );
        }


        // =====================================================
        // UPDATE
        // =====================================================

        employerMaster.setEmployerName(
                request.getEmployerName()
                        .trim()
        );


        employerMaster.setEmployerAddress(
                request.getEmployerAddress()
                        .trim()
        );


        employerMaster.setEmployerPhone(
                normalize(
                        request.getEmployerPhone()
                )
        );


        employerMaster.setEmployerEmail(
                normalize(
                        request.getEmployerEmail()
                )
        );


        employerMaster.setDeductorPan(
                pan
        );


        employerMaster.setDeductorTan(
                tan
        );


        employerMaster.setCitTdsAddress(
                request.getCitTdsAddress()
                        .trim()
        );


        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );


        Form16EmployerMaster updated =
                repository.save(
                        employerMaster
                );


        return mapToResponse(
                updated
        );
    }


    // =========================================================
    // ACTIVATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse
    activateEmployerMaster(
            Long id
    ) {

        validateId(id);


        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );


        // =====================================================
        // DEACTIVATE ALL CURRENTLY ACTIVE EMPLOYERS
        // =====================================================

        List<Form16EmployerMaster> allMasters =
                repository.findAll();


        for (
                Form16EmployerMaster master
                : allMasters
        ) {

            if (Boolean.TRUE.equals(
                    master.getActive()
            )) {

                master.setActive(false);

                master.setUpdatedAt(
                        LocalDateTime.now()
                );

                repository.save(master);
            }
        }


        // =====================================================
        // ACTIVATE SELECTED EMPLOYER
        // =====================================================

        employerMaster.setActive(true);

        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );


        Form16EmployerMaster activated =
                repository.save(
                        employerMaster
                );


        return mapToResponse(
                activated
        );
    }


    // =========================================================
    // DEACTIVATE
    // =========================================================

    @Override
    public Form16EmployerMasterResponse
    deactivateEmployerMaster(
            Long id
    ) {

        validateId(id);


        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );


        employerMaster.setActive(false);

        employerMaster.setUpdatedAt(
                LocalDateTime.now()
        );


        Form16EmployerMaster updated =
                repository.save(
                        employerMaster
                );


        return mapToResponse(
                updated
        );
    }


    // =========================================================
    // DELETE
    // =========================================================

    @Override
    public void deleteEmployerMaster(
            Long id
    ) {

        validateId(id);


        Form16EmployerMaster employerMaster =
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Employer master not found with id: "
                                                + id
                                )
                        );


        // =====================================================
        // ACTIVE EMPLOYER CANNOT BE DELETED
        // =====================================================

        if (Boolean.TRUE.equals(
                employerMaster.getActive()
        )) {

            throw new IllegalArgumentException(
                    "Active employer master cannot be deleted. "
                            + "Deactivate it first."
            );
        }


        repository.delete(
                employerMaster
        );
    }


    // =========================================================
    // GET ALL
    // =========================================================

    @Override
    @Transactional(readOnly = true)
    public List<Form16EmployerMasterResponse>
    getAllEmployerMasters() {

        return repository
                .findAllByOrderByIdDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // =========================================================
    // REQUEST VALIDATION
    // =========================================================

    private void validateRequest(
            Form16EmployerMasterRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "Employer master request cannot be null"
            );
        }


        // =====================================================
        // EMPLOYER NAME
        // =====================================================

        if (
                request.getEmployerName() == null
                        ||
                        request.getEmployerName().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Employer name is required"
            );
        }


        // =====================================================
        // EMPLOYER ADDRESS
        // =====================================================

        if (
                request.getEmployerAddress() == null
                        ||
                        request.getEmployerAddress().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Employer address is required"
            );
        }


        // =====================================================
        // PAN REQUIRED
        // =====================================================

        if (
                request.getDeductorPan() == null
                        ||
                        request.getDeductorPan().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Deductor PAN is required"
            );
        }


        String pan =
                normalizePan(
                        request.getDeductorPan()
                );


        // =====================================================
        // PAN FORMAT
        // =====================================================

        if (
                !pan.matches(
                        "^[A-Z]{5}[0-9]{4}[A-Z]$"
                )
        ) {

            throw new IllegalArgumentException(
                    "Invalid Deductor PAN. "
                            + "Expected format: ABCDE1234F"
            );
        }


        // =====================================================
        // TAN REQUIRED
        // =====================================================

        if (
                request.getDeductorTan() == null
                        ||
                        request.getDeductorTan().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "Deductor TAN is required"
            );
        }


        String tan =
                normalizeTan(
                        request.getDeductorTan()
                );


        // =====================================================
        // TAN FORMAT
        // =====================================================

        if (
                !tan.matches(
                        "^[A-Z]{4}[0-9]{5}[A-Z]$"
                )
        ) {

            throw new IllegalArgumentException(
                    "Invalid Deductor TAN. "
                            + "Expected format: ABCD12345E"
            );
        }


        // =====================================================
        // CIT TDS ADDRESS
        // =====================================================

        if (
                request.getCitTdsAddress() == null
                        ||
                        request.getCitTdsAddress().isBlank()
        ) {

            throw new IllegalArgumentException(
                    "CIT TDS address is required"
            );
        }
    }


    // =========================================================
    // PAN NORMALIZATION
    // =========================================================

    private String normalizePan(
            String pan
    ) {

        if (pan == null) {
            return null;
        }

        return pan
                .trim()
                .toUpperCase();
    }


    // =========================================================
    // TAN NORMALIZATION
    // =========================================================

    private String normalizeTan(
            String tan
    ) {

        if (tan == null) {
            return null;
        }

        return tan
                .trim()
                .toUpperCase();
    }


    // =========================================================
    // GENERAL STRING NORMALIZATION
    // =========================================================

    private String normalize(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String result =
                value.trim();

        return result.isEmpty()
                ? null
                : result;
    }


    // =========================================================
    // ID VALIDATION
    // =========================================================

    private void validateId(
            Long id
    ) {

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "Valid employer master ID is required"
            );
        }
    }


    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private Form16EmployerMasterResponse
    mapToResponse(
            Form16EmployerMaster entity
    ) {

        return Form16EmployerMasterResponse
                .builder()

                .id(
                        entity.getId()
                )

                .employerName(
                        entity.getEmployerName()
                )

                .employerAddress(
                        entity.getEmployerAddress()
                )

                .employerPhone(
                        entity.getEmployerPhone()
                )

                .employerEmail(
                        entity.getEmployerEmail()
                )

                .deductorPan(
                        entity.getDeductorPan()
                )

                .deductorTan(
                        entity.getDeductorTan()
                )

                .citTdsAddress(
                        entity.getCitTdsAddress()
                )

                .active(
                        entity.getActive()
                )

                .createdAt(
                        entity.getCreatedAt()
                )

                .updatedAt(
                        entity.getUpdatedAt()
                )

                .build();
    }
}
