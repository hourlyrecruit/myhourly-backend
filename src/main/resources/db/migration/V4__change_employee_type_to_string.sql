ALTER TABLE salary_templates
ALTER COLUMN employee_type TYPE VARCHAR(100)
USING employee_type::text;

ALTER TABLE salary_templates
    ALTER COLUMN employee_type SET NOT NULL;