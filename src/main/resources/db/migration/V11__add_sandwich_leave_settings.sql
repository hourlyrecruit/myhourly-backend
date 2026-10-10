-- Add sandwich leave policy settings to leave_settings table
-- These settings control whether weekends are automatically charged
-- when leave is taken on specific days of the week.

ALTER TABLE leave_settings 
ADD COLUMN IF NOT EXISTS sandwich_leave_monday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN IF NOT EXISTS sandwich_leave_friday_enabled BOOLEAN NOT NULL DEFAULT false,
ADD COLUMN IF NOT EXISTS sandwich_leave_friday_monday_enabled BOOLEAN NOT NULL DEFAULT false;

-- Add column to store forced working days (sandwich leave weekends) in leave_requests
ALTER TABLE leave_requests
ADD COLUMN IF NOT EXISTS forced_working_days_json TEXT;

-- Add comments for documentation
COMMENT ON COLUMN leave_settings.sandwich_leave_monday_enabled IS 
  'When enabled, taking leave on Monday forces the preceding Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days.';

COMMENT ON COLUMN leave_settings.sandwich_leave_friday_enabled IS 
  'When enabled, taking leave on Friday forces the following Saturday and Sunday to be counted as working days, resulting in 3 total chargeable days.';

COMMENT ON COLUMN leave_settings.sandwich_leave_friday_monday_enabled IS 
  'When enabled, taking leave on both Friday and Monday forces the intervening Saturday and Sunday to be counted as working days, resulting in 4 total chargeable days. This rule takes precedence over the individual Friday and Monday rules.';

COMMENT ON COLUMN leave_requests.forced_working_days_json IS
  'JSON array of dates (YYYY-MM-DD format) that should be counted as working days even if they fall on weekends. Used for sandwich leave policy enforcement. Example: ["2023-10-28", "2023-10-29"]';
