-- Remove enum-like CHECK restriction
ALTER TABLE salary_templates
DROP CONSTRAINT salary_templates_employee_type_check;

-- Remove uniqueness restriction
ALTER TABLE salary_templates
DROP CONSTRAINT uk_salary_template_employee_type;