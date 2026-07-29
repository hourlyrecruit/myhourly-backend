# Payroll Module Analysis Document - MyHourly Application

## 1. Introduction
The Payroll module is a core component of the MyHourly application, responsible for managing employee compensation, statutory deductions, and payment processing. It provides a robust, auditable, and precise system for handling salary lifecycle from template definition to final payment.

## 2. System Architecture
The module is built using a layered architecture on the Spring Boot framework:
- **API Layer**: Exposes RESTful endpoints for HR and Payroll admins.
- **Business Logic Layer**: Handles complex salary calculations, LOP (Loss of Pay) logic, and state transitions.
- **Data Persistence Layer**: Uses Spring Data JPA with PostgreSQL to store templates, structures, and transactional payroll data.
- **PDF Generation**: Utilizes a dedicated service for rendering payslips in PDF format.

## 3. Data Model & Entities
The module is centered around four main entities:

### 3.1 SalaryTemplate
- **Purpose**: Defines a standard salary framework for different employment types (Full-time, Contract, etc.).
- **Key Fields**: Basic Salary, HRA, Special Allowance, Medical, PF, ESI, Professional Tax, Income Tax.
- **Nature**: Configuration/Master data.

### 3.2 SalaryStructure
- **Purpose**: Employee-specific salary configuration derived from a template.
- **Key Fields**: Effective From, Effective To, Status (ACTIVE, INACTIVE), and calculated Net Salary.
- **Relationship**: Links an Employee to a SalaryTemplate with specific dollar/rupee amounts.

### 3.3 EmployeePaymentDetails
- **Purpose**: Stores static disbursement information.
- **Key Fields**: Bank Name, Account Number, IFSC, PAN, UAN, PF Number, ESI Number.

### 3.4 Payroll (Transactional)
- **Purpose**: Records the actual monthly payout.
- **Snapshot Logic**: Critically, it stores snapshots of employee details (Name, Dept, Designation) and payment details at the time of generation. This ensures historical integrity.
- **Version Control**: Supports multiple versions via a `version` field and `active` flag for superseding records.

## 4. Operational Workflow

### 4.1 Generation Process
1. **Validation**: Checks if the employee is active and joined before the payroll month.
2. **Data Retrieval**: Fetches the `ACTIVE` SalaryStructure and current PaymentDetails.
3. **Calculation Engine**:
    - **Gross Earnings**: Sum of all allowance components.
    - **LOP Deduction**: `(Gross / Total Days) * LOP Days`.
    - **Statutory Deductions**: Sum of PF, ESI, PT, and Income Tax.
    - **Net Pay**: `Gross - (Statutory Deductions + LOP)`.
4. **State Persistence**: Saves the record with a unique payroll number (e.g., `PR-202607-0001`).

### 4.2 State Machine
The module manages a rigorous status flow:
- `DRAFT`: Preliminary record, editable.
- `GENERATED`: Finalized calculation, awaiting approval.
- `APPROVED`: Confirmed by authority, ready for payment.
- `PAID`: Payment processed, reference ID attached.
- `CANCELLED`: Invalidated record.
- `SUPERSEDED`: Replaced by a newer version (after regeneration).

## 5. Integration Points
- **Employee Module**: For basic profile and employment details.
- **Attendance/Leave (Potential)**: Currently uses default/mock values (22 days) in `PayrollServiceImpl`. This is a primary integration area where actual attendance data should be pulled.
- **Security**: Integrates with Spring Security for role-based access (SUPER_ADMIN, HR_ADMIN, PAYROLL_ADMIN, EMPLOYEE).

## 6. Key Features
- **Immutability**: Once a payroll is generated, it captures a snapshot of data, preventing historical records from changing if master data is updated.
- **Audit Trail**: Every action on a payroll record is logged in `PayrollHistory`.
- **Precision**: Uses `BigDecimal` with `HALF_UP` rounding to prevent floating-point errors in financial data.
- **PDF Payslips**: On-demand generation of professional payslips for employees.

## 7. Recommendations & Future Scope
- **Attendance Sync**: Implement the `TODO` in `PayrollServiceImpl` to fetch real `workedDays` and `lopDays` from the Attendance module.
- **Tax Projections**: Enhance the `IncomeTax` logic to calculate based on annual projections and investment declarations.
- **Bulk Operations**: Add support for bulk approval and bulk marking as paid to improve admin efficiency.
- **Email Notifications**: Automatically email payslips to employees once payroll is marked as `PAID`.
