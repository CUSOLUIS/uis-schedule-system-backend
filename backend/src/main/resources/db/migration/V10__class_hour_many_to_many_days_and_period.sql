-- =========================================================
-- V10: Change class_hour from single day (ManyToOne) to
--      multiple days (ManyToMany) and add academic period
--      fields (start_date / end_date).
-- =========================================================

-- 1. Create join table for ManyToMany relationship
CREATE TABLE public.class_hour_day (
    class_hour_id bigint NOT NULL,
    day_id        bigint NOT NULL,
    PRIMARY KEY (class_hour_id, day_id),
    CONSTRAINT fk_chd_class_hour FOREIGN KEY (class_hour_id)
        REFERENCES public.class_hour(class_hour_id) ON DELETE CASCADE,
    CONSTRAINT fk_chd_day FOREIGN KEY (day_id)
        REFERENCES public.day_of_week(day_id)
);

-- 2. Migrate existing day_id data into the join table
INSERT INTO public.class_hour_day (class_hour_id, day_id)
SELECT class_hour_id, day_id
FROM public.class_hour
WHERE day_id IS NOT NULL;

-- 3. Drop the old single-day column
ALTER TABLE public.class_hour DROP COLUMN day_id;

-- 4. Add academic period fields with defaults
ALTER TABLE public.class_hour
    ADD COLUMN start_date date NOT NULL DEFAULT CURRENT_DATE;

ALTER TABLE public.class_hour
    ADD COLUMN end_date date NOT NULL DEFAULT (CURRENT_DATE + INTERVAL '6 months');
