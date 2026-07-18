-- =========================================================
-- V11: Add missing "Domingo" (Sunday) and normalize all
--      day names to lowercase.
-- =========================================================

-- Insert missing Domingo (Sunday) with id 7
INSERT INTO public.day_of_week (day_id, name)
VALUES (7, 'domingo')
ON CONFLICT DO NOTHING;

-- Normalize existing names to lowercase
UPDATE public.day_of_week SET name = LOWER(name) WHERE name != LOWER(name);
