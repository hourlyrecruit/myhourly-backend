package com.my_hourly.form_16.controller;

import com.my_hourly.form_16.dto.Form16ChapterVIARequest;
import com.my_hourly.form_16.dto.Form16ChapterVIAResponse;
import com.my_hourly.form_16.service.Form16ChapterVIAService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "30 - Form 16 Chapter VI-A",
        description = "Form 16 Chapter VI-A APIs"
)
@RestController
@RequestMapping("/api/form16")
@RequiredArgsConstructor
public class Form16ChapterVIAController {

    private final Form16ChapterVIAService form16ChapterVIAService;


    // =========================================================
    // CREATE
    // HR + MANAGER
    // =========================================================

    @PostMapping("/{form16Id}/chapter-via")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16ChapterVIAResponse> createChapterVIA(
            @PathVariable Long form16Id,
            @RequestBody Form16ChapterVIARequest request
    ) {

        Form16ChapterVIAResponse response =
                form16ChapterVIAService.createChapterVIA(
                        form16Id,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // =========================================================
    // GET
    // EMPLOYEE + HR + MANAGER
    // =========================================================

    @GetMapping("/{form16Id}/chapter-via")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR')")
    public ResponseEntity<Form16ChapterVIAResponse>
    getChapterVIA(
            @PathVariable Long form16Id
    ) {

        return ResponseEntity.ok(
                form16ChapterVIAService
                        .getChapterVIAByForm16Id(form16Id)
        );
    }


    // =========================================================
    // UPDATE
    // HR + MANAGER
    // =========================================================

    @PutMapping("/{form16Id}/chapter-via")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16ChapterVIAResponse>
    updateChapterVIA(
            @PathVariable Long form16Id,
            @RequestBody Form16ChapterVIARequest request
    ) {

        Form16ChapterVIAResponse response =
                form16ChapterVIAService.updateChapterVIA(
                        form16Id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // AUTO SAVE
    //
    // React can call this when user changes an amount.
    //
    // No separate calculation API is required.
    // Backend calculates and saves the values.
    // =========================================================

    @PatchMapping("/{form16Id}/chapter-via")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Form16ChapterVIAResponse>
    autoSaveChapterVIA(
            @PathVariable Long form16Id,
            @RequestBody Form16ChapterVIARequest request
    ) {

        Form16ChapterVIAResponse response =
                form16ChapterVIAService.autoSaveChapterVIA(
                        form16Id,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // =========================================================
    // DELETE
    // HR + MANAGER
    // =========================================================

    @DeleteMapping("/{form16Id}/chapter-via")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<Void> deleteChapterVIA(
            @PathVariable Long form16Id
    ) {

        form16ChapterVIAService.deleteChapterVIA(
                form16Id
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}