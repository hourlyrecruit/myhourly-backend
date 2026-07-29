package com.my_hourly.payroll.dto.response;

import com.my_hourly.payroll.enums.PaymentMode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee Payment Details Response")
public class EmployeePaymentDetailsResponse {

    @Schema(description = "Payment Details Id", example = "1")
    private Long id;

    @Schema(description = "Employee Id", example = "101")
    private Long employeeId;

    @Schema(description = "Employee Code", example = "EMP000101")
    private String employeeCode;

    @Schema(description = "Employee Name", example = "Jitendra Prajapati")
    private String employeeName;

    @Schema(description = "PAN Number", example = "ABCDE1234F")
    private String panNumber;

    @Schema(description = "Bank Name", example = "State Bank of India")
    private String bankName;

    @Schema(description = "Account Number", example = "123456789012")
    private String accountNumber;

    @Schema(description = "IFSC Code", example = "SBIN0001234")
    private String ifscCode;

    @Schema(description = "Payment Mode")
    private PaymentMode paymentMode;

    @Schema(description = "UAN Number", example = "100123456789")
    private String uanNumber;

    @Schema(description = "PF Number", example = "KNBNG1234567000001")
    private String pfNumber;

    @Schema(description = "ESI Number", example = "31001234567890001")
    private String esiNumber;

    @Schema(description = "Created Date")
    private LocalDateTime createdAt;

    @Schema(description = "Last Updated Date")
    private LocalDateTime updatedAt;

}