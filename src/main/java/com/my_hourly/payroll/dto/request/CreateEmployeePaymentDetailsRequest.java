package com.my_hourly.payroll.dto.request;

import com.my_hourly.payroll.enums.PaymentMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request to create employee payment details")
public class CreateEmployeePaymentDetailsRequest {

    @NotNull(message = "Employee Id is required.")
    @Schema(description = "Employee Id", example = "1")
    private Long employeeId;

    @Size(max = 20, message = "PAN Number cannot exceed 20 characters.")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$|^$",
            message = "Invalid PAN Number format."
    )
    @Schema(description = "PAN Number", example = "ABCDE1234F")
    private String panNumber;

    @NotBlank(message = "Bank Name is required.")
    @Size(max = 100, message = "Bank Name cannot exceed 100 characters.")
    @Schema(description = "Bank Name", example = "State Bank of India")
    private String bankName;

    @NotBlank(message = "Account Number is required.")
    @Size(max = 30, message = "Account Number cannot exceed 30 characters.")
    @Schema(description = "Bank Account Number", example = "123456789012")
    private String accountNumber;

    @NotBlank(message = "IFSC Code is required.")
    @Size(max = 20, message = "IFSC Code cannot exceed 20 characters.")
    @Schema(description = "IFSC Code", example = "SBIN0001234")
    private String ifscCode;

    @NotNull(message = "Payment Mode is required.")
    @Schema(description = "Payment Mode")
    private PaymentMode paymentMode;

    @Size(max = 30, message = "UAN Number cannot exceed 30 characters.")
    @Schema(description = "UAN Number", example = "100123456789")
    private String uanNumber;

    @Size(max = 30, message = "PF Number cannot exceed 30 characters.")
    @Schema(description = "PF Number", example = "KNBNG1234567000001")
    private String pfNumber;

    @Size(max = 30, message = "ESI Number cannot exceed 30 characters.")
    @Schema(description = "ESI Number", example = "31001234567890001")
    private String esiNumber;

}
