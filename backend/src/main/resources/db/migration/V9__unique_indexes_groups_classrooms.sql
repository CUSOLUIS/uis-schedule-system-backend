-- =========================================================
-- V9: Partial unique indexes for active classrooms and groups
-- Additive only — does not alter the already-applied V8 checksum.
-- Soft-deleted rows are excluded so the same key can be reused.
-- =========================================================

CREATE UNIQUE INDEX IF NOT EXISTS uk_classroom_number_campus_building_active
    ON public.classroom (
        LOWER(number),
        LOWER(COALESCE(campus, '')),
        LOWER(COALESCE(building, ''))
    )
    WHERE is_active = true;

CREATE UNIQUE INDEX IF NOT EXISTS uk_groups_name_subject_period_active
    ON public.groups (
        LOWER(group_name),
        subject_id,
        period_id
    )
    WHERE is_active = true;
