-- =====================================================================
-- V2 - Schema repairs that Hibernate's ddl-auto=update silently skipped
-- =====================================================================
-- Both statements below were declared on the entities long before this
-- migration existed, but ddl-auto=update only logs DDL failures as warnings,
-- so they never reached the database:
--
--   1. payrolls.date_of_joining was declared NOT NULL. PostgreSQL refuses to
--      add a NOT NULL column to a table that already has rows, so the column
--      was never created and every payroll query failed with
--      "column p1_0.date_of_joining does not exist".
--   2. salary_templates declared a unique constraint on the non-existent
--      column "employee_type_id"; it is now declared on "employee_type".
--
-- Both statements are written to be safe on databases that already have them.
-- =====================================================================

alter table payrolls
    add column if not exists date_of_joining date;

update payrolls p
   set date_of_joining = e.date_of_joining
  from employees e
 where p.employee_id = e.id
   and p.date_of_joining is null
   and e.date_of_joining is not null;

do $$
begin
    if not exists (
        select 1
          from pg_constraint
         where conname = 'uk_salary_template_employee_type'
           and conrelid = 'salary_templates'::regclass
    ) then
        alter table salary_templates
            add constraint uk_salary_template_employee_type unique (employee_type);
    end if;
end $$;
