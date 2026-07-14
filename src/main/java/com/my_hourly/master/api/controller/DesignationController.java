package com.my_hourly.master.api.controller;

import com.my_hourly.common.response.ApiResponse;
import com.my_hourly.common.response.PageResponse;
import com.my_hourly.master.api.request.CreateDesignationRequest;
import com.my_hourly.master.api.request.UpdateDesignationRequest;
import com.my_hourly.master.api.response.DesignationResponse;
import com.my_hourly.master.service.DesignationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/designations")
@RequiredArgsConstructor
@Tag(name = "Designation Controller", description = "Access: SUPER_ADMIN, MANAGER, HR_ADMIN")
public class DesignationController {

    private final DesignationService designationService;

    @PostMapping
    @PreAuthorize("hasAuthority('designation:create')")
    @Operation(description = "Add Designation like: HR Manager, IT Manager, Software Engineer, QA Engineer, Design Engineer etc.")
    public ResponseEntity<ApiResponse<DesignationResponse>> create(
            @Valid @RequestBody CreateDesignationRequest request) {

        DesignationResponse response = designationService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<DesignationResponse>builder()
                        .success(true)
                        .message("Designation created successfully.")
                        .data(response)
                        .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('designation:view')")
    public ResponseEntity<ApiResponse<DesignationResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.<DesignationResponse>builder()
                        .success(true)
                        .message("Designation fetched successfully.")
                        .data(designationService.getById(id))
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('designation:view')")
    public ResponseEntity<ApiResponse<PageResponse<DesignationResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "designationName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection) {

        return ResponseEntity.ok(
                ApiResponse.<PageResponse<DesignationResponse>>builder()
                        .success(true)
                        .message("Designations fetched successfully.")
                        .data(designationService.getAll(page, size, search, sortBy, sortDirection))
                        .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('designation:update')")
    public ResponseEntity<ApiResponse<DesignationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDesignationRequest request) {

        return ResponseEntity.ok(
                ApiResponse.<DesignationResponse>builder()
                        .success(true)
                        .message("Designation updated successfully.")
                        .data(designationService.update(id, request))
                        .build());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('designation:update')")
    public ResponseEntity<ApiResponse<Void>> changeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {

        designationService.changeStatus(id, active);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Designation status updated successfully.")
                        .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('designation:delete')")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id) {

        designationService.delete(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Designation deleted successfully.")
                        .build());
    }

}