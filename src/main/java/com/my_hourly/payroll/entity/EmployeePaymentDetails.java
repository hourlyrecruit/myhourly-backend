package com.my_hourly.payroll.entity;

import com.my_hourly.common.entity.BaseEntity;
import com.my_hourly.employee.entity.Employee;
import com.my_hourly.payroll.enums.PaymentMode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "employee_payment_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_employee_payment_details_employee",
                        columnNames = "employee_id"
                )
        }
)
public class EmployeePaymentDetails extends BaseEntity {


    /**
     * One employee can have only one payment details record.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "employee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_payment_details_employee")
    )
    private Employee employee;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Column(name = "bank_name", nullable = false, length = 100)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "ifsc_code", nullable = false, length = 20)
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false, length = 30)
    private PaymentMode paymentMode;

    @Column(name = "uan_number", length = 30)
    private String uanNumber;

    @Column(name = "pf_number", length = 30)
    private String pfNumber;

    @Column(name = "esi_number", length = 30)
    private String esiNumber;

}