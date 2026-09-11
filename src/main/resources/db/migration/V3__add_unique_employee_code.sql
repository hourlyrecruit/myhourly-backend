ALTER TABLE employees
    ADD CONSTRAINT uk_employee_employee_code
        UNIQUE (employee_code);