package com.my_hourly.payroll.controller;

import com.my_hourly.payroll.dto.request.CreatePayrollRequest;
import com.my_hourly.payroll.dto.request.UpdateDraftPayrollRequest;
import com.my_hourly.payroll.dto.response.PayrollResponse;
import com.my_hourly.payroll.dto.response.PayrollSummaryResponse;
import com.my_hourly.payroll.enums.PayrollStatus;
import com.my_hourly.payroll.pdf.PayslipPdfService;
import com.my_hourly.payroll.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
@Tag(name = "20-Payroll", description = "Payroll Management APIs")
public class PayrollController {

    private final PayrollService payrollService;
    private final PayslipPdfService payslipPdfService;

    /* =====================================================
       Generate
       ===================================================== */

    @PostMapping("/generate")
    @Operation(summary = "Generate Payroll for one or more employees, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollSummaryResponse> generatePayroll(
            @Valid @RequestBody CreatePayrollRequest request) {

        return ResponseEntity.ok(
                payrollService.generatePayroll(request));
    }

    /* =====================================================
       Read
       ===================================================== */

    @GetMapping("/{id}")
    @Operation(summary = "Get Payroll by ID, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.getById(id));
    }

    @GetMapping("/number/{payrollNumber}")
    @Operation(summary = "Get Payroll by Payroll Number, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> getByPayrollNumber(
            @PathVariable String payrollNumber) {

        return ResponseEntity.ok(
                payrollService.getByPayrollNumber(payrollNumber));
    }

    @GetMapping("/employee/{employeeId}")
    @Operation(summary = "Employee Payroll History (all versions, all months), 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN','EMPLOYEE')")
    public ResponseEntity<List<PayrollResponse>> getByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                payrollService.getByEmployee(employeeId));
    }

    @GetMapping("/month")
    @Operation(summary = "Get all Payrolls for a given month, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<PayrollResponse>> getByMonth(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate payrollMonth) {

        return ResponseEntity.ok(
                payrollService.getByPayrollMonth(payrollMonth));
    }

    @GetMapping("/status")
    @Operation(summary = "Get Payrolls by Status, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<List<PayrollResponse>> getByStatus(
            @RequestParam PayrollStatus status) {

        return ResponseEntity.ok(
                payrollService.getByStatus(status));
    }

    /* =====================================================
       Lifecycle Transitions
       ===================================================== */

    @PutMapping("/{payrollId}/draft")
    @Operation(summary = "Finalize a DRAFT payroll as GENERATED before approval, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> updateDraft(
            @PathVariable Long payrollId,
            @Valid @RequestBody UpdateDraftPayrollRequest request) {

        return ResponseEntity.ok(
                payrollService.updateDraft(payrollId, request));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a GENERATED payroll, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> approve(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.approve(id));
    }

    @PatchMapping("/{id}/pay")
    @Operation(summary = "Mark an APPROVED payroll as PAID, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> markAsPaid(
            @PathVariable Long id,
            @RequestParam String paymentReference) {

        return ResponseEntity.ok(
                payrollService.markAsPaid(id, paymentReference));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a DRAFT or GENERATED payroll, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> cancel(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.cancel(id));
    }

    @PostMapping("/{id}/regenerate")
    @Operation(summary = "Supersede the current payroll and create a new version, 'SUPER_ADMIN','HR_ADMIN'")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','HR_ADMIN','PAYROLL_ADMIN')")
    public ResponseEntity<PayrollResponse> regenerate(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                payrollService.regenerate(id));
    }

    /* =====================================================
       PDF
       ===================================================== */

    @GetMapping("/{id}/payslip")
    @Operation(summary = "Download Payslip PDF (APPROVED or PAID only), 'EMPLOYEE','HR_ADMIN','SUPER_ADMIN'")
    @PreAuthorize("hasAnyRole('EMPLOYEE','HR_ADMIN','PAYROLL_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<byte[]> downloadPayslip(
            @PathVariable Long id) {

        byte[] pdf = payslipPdfService.generatePayslip(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=payslip-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

}
