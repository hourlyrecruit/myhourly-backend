package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.dto.Form16SalaryResponse;
import com.my_hourly.form_16.service.Form16SalaryService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "27 - Form 16 Salary",
        description = "Form 16 Salary APIs"
)
@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
public class Form16SalaryController {

    private final Form16SalaryService form16SalaryService;

    // CREATE
    @PostMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN')")
    public ResponseEntity<Form16SalaryResponse> createSalary(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16SalaryRequest request) {

        Form16SalaryResponse response =
                form16SalaryService.createSalary(form16Id, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET
    @GetMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR_ADMIN')")
    public ResponseEntity<Form16SalaryResponse> getSalary(
            @PathVariable Long form16Id) {

        return ResponseEntity.ok(
                form16SalaryService.getSalaryByForm16Id(form16Id)
        );
    }

    // UPDATE
    @PutMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN')")
    public ResponseEntity<Form16SalaryResponse> updateSalary(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16SalaryRequest request) {

        return ResponseEntity.ok(
                form16SalaryService.updateSalary(form16Id, request)
        );
    }

    // DELETE
    @DeleteMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR_ADMIN')")
    public ResponseEntity<Void> deleteSalary(
            @PathVariable Long form16Id) {

        form16SalaryService.deleteSalary(form16Id);

        return ResponseEntity.noContent().build();
    }
}
