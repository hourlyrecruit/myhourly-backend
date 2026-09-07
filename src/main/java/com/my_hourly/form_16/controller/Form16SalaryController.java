package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16SalaryRequest;
import com.my_hourly.form_16.entity.Form16Salary;
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
        description = "Form 16 APIs"
)
@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
public class Form16SalaryController {

    private final Form16SalaryService form16SalaryService;


    // =========================================================
    // CREATE
    // MANAGER + HR ONLY
    // =========================================================

    @PostMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16Salary> createSalary(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16SalaryRequest request) {

        Form16Salary response =
                form16SalaryService.createSalary(
                        form16Id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // VIEW
    // EMPLOYEE + MANAGER + HR
    // =========================================================

    @GetMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<Form16Salary> getSalary(
            @PathVariable Long form16Id) {

        Form16Salary response =
                form16SalaryService.getSalaryByForm16Id(
                        form16Id
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // UPDATE
    // MANAGER + HR ONLY
    // =========================================================

    @PutMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16Salary> updateSalary(
            @PathVariable Long form16Id,
            @Valid @RequestBody Form16SalaryRequest request) {

        Form16Salary response =
                form16SalaryService.updateSalary(
                        form16Id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE
    // MANAGER + HR ONLY
    // =========================================================

    @DeleteMapping("/{form16Id}/salary")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Void> deleteSalary(
            @PathVariable Long form16Id) {

        form16SalaryService.deleteSalary(
                form16Id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}