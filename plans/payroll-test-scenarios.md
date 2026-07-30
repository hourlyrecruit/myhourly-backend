# Payroll Module Test Scenarios

## 1. Generate Payroll (`POST /api/v1/payroll/generate`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC01** | Generate payroll for multiple active employees with valid `payrollMonth` | `200 OK` with summary containing generated payroll numbers for each employee |
| **TC02** | Generate payroll for duplicate `payrollMonth` for same employee | `400 Bad Request` with error `PAYROLL_ALREADY_PROCESSED` |
| **TC03** | Generate payroll with empty `employeeIds` | `200 OK` generating payroll for all active employees |
| **TC04** | Generate payroll with invalid `payrollMonth` format | `400 Bad Request` (validation error) |
| **TC05** | Generate payroll with non‑existent employee ID | `404 Not Found` (handled by service layer) |
| **TC06** | Generate payroll with `saveAsDraft=true` | Payroll saved as `DRAFT` status, summary reflects draft count |

## 2. Get Payroll by ID (`GET /api/v1/payroll/{id}`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC07** | Retrieve existing payroll by valid ID | `200 OK` with payroll details |
| **TC08** | Retrieve non‑existent payroll ID | `404 Not Found` with error message |

## 3. Get Payroll by Payroll Number (`GET /api/v1/payroll/number/{payrollNumber}`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC09** | Query with valid payroll number | `200 OK` with payroll details |
| **TC10** | Query with invalid/non‑existent payroll number | `404 Not Found` |

## 4. Get Payroll by Employee (`GET /api/v1/payroll/employee/{employeeId}`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC11** | Retrieve history for employee with multiple payroll entries | `200 OK` with list of payroll responses |
| **TC12** | Retrieve history for employee with no payroll entries | `200 OK` with empty list |

## 5. Get Payroll by Month (`GET /api/v1/payroll/month`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC13** | Provide valid `LocalDate` (e.g., `2026-07`) | `200 OK` with all payrolls for that month |
| **TC14** | Provide malformed month parameter | `400 Bad Request` (validation error) |

## 6. Get Payroll by Status (`GET /api/v1/payroll/status`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC15** | Request with `PayrollStatus.DRAFT` | `200 OK` with all draft payrolls |
| **TC16** | Request with `PayrollStatus.GENERATED` | `200 OK` with all generated payrolls |
| **TC17** | Request with `PayrollStatus.APPROVED` | `200 OK` with all approved payrolls |
| **TC18** | Request with unknown status | `400 Bad Request` (validation error) |

## 7. Update Draft (`PUT /api/v1/payroll/{id}/draft`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC19** | Update fields on a payroll with `DRAFT` status | `200 OK` with updated payroll response |
| **TC20** | Attempt to update a non‑draft payroll | `400 Bad Request` with error `Only DRAFT payrolls can be edited` |
| **TC21** | Provide invalid field values that violate business rules | `400 Bad Request` with appropriate validation error |

## 8. Approve Payroll (`PATCH /api/v1/payroll/{id}/approve`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC22** | Approve a payroll with `GENERATED` status | `200 OK` with status transitioned to `APPROVED` |
| **TC23** | Approve a non‑generated payroll | `400 Bad Request` with error `Only GENERATED payroll can be approved` |

## 9. Mark as Paid (`PATCH /api/v1/payroll/{id}/pay`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC24** | Mark an `APPROVED` payroll as paid with valid reference | `200 OK` with status transitioned to `PAID` |
| **TC25** | Attempt to mark a non‑approved payroll as paid | `400 Bad Request` with error `Payroll must be APPROVED before marking as PAID` |

## 10. Cancel Payroll (`PATCH /api/v1/payroll/{id}/cancel`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC26** | Cancel a `DRAFT` or `GENERATED` payroll | `200 OK` with status transitioned to `CANCELLED` |
| **TC27** | Cancel an `APPROVED` or `PAID` payroll | `400 Bad Request` with error `Approved or Paid payrolls cannot be cancelled` |

## 11. Regenerate Payroll (`POST /api/v1/payroll/{id}/regenerate`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC28** | Regenerate a `GENERATED` payroll | `200 OK` with new payroll version created |
| **TC29** | Regenerate a `PAID` payroll | `400 Bad Request` with error `Paid payrolls cannot be regenerated` |
| **TC30** | Regenerate a `SUPERSEDED` payroll | `400 Bad Request` with error `Only the active payroll version can be regenerated` |

## 12. Download Payslip PDF (`GET /api/v1/payroll/{id}/payslip`)

| Scenario | Description | Expected Result |
|----------|-------------|-----------------|
| **TC31** | Generate PDF for an `APPROVED` or `PAID` payroll | `200 OK` with PDF content disposition header |
| **TC32** | Attempt to download PDF for a non‑approved/paid payroll | `403 Forbidden` or `400 Bad Request` (handled by service) |

---

### General Test Data Setup

1. **Create Employees** – Ensure at least two active employees with distinct `employeeCode`s.
2. **Create Salary Structures** – Assign active salary structures to employees.
3. **Create Payment Details** – Populate bank, PAN, and other payment details.
4. **Seed Payroll History (if needed)** – Use seed scripts to pre‑populate existing payroll records for versioning tests.

---

### Execution Notes

- Use **Swagger UI** (`/swagger-ui.html`) to manually trigger endpoints with sample payloads.
- For automated testing, leverage **MockMvc** or **RestAssured** in integration test suites.
- Validate database state after each operation (e.g., `Payroll`, `PayrollHistory` tables) to ensure correct status transitions and audit trails.
- Ensure **security constraints** (`@PreAuthorize`) are enforced by testing with unauthorized roles where applicable.