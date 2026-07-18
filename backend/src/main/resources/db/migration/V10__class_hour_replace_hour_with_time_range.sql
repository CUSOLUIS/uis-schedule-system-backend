-- Replace single 'hour' column with 'start_time' and 'end_time' columns for class_hour table
ALTER TABLE public.class_hour ADD COLUMN start_time time(6) without time zone;
ALTER TABLE public.class_hour ADD COLUMN end_time time(6) without time zone;

-- Migrate existing data: copy hour into both columns
UPDATE public.class_hour SET start_time = hour, end_time = hour WHERE hour IS NOT NULL;

-- Drop the old column
ALTER TABLE public.class_hour DROP COLUMN hour;

-- Ensure start_time and end_time are NOT NULL
ALTER TABLE public.class_hour ALTER COLUMN start_time SET NOT NULL;
ALTER TABLE public.class_hour ALTER COLUMN end_time SET NOT NULL;
