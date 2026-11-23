-- Add optional start and end dates to courses
ALTER TABLE course
    ADD COLUMN IF NOT EXISTS start_date DATE NULL,
    ADD COLUMN IF NOT EXISTS end_date DATE NULL;
