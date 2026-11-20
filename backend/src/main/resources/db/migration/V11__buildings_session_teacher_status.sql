-- HC-01 / HC-03 / HC-04: buildings, session status, teacher catalog fields, session-level teacher

-- ── Buildings ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS public.building (
    building_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(20) NOT NULL,
    name VARCHAR(256) NOT NULL,
    faculty VARCHAR(256),
    preference_order INTEGER NOT NULL DEFAULT 100,
    has_ac BOOLEAN NOT NULL DEFAULT FALSE,
    has_computer_lab BOOLEAN NOT NULL DEFAULT FALSE,
    campus VARCHAR(256),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_building_code UNIQUE (code)
);

INSERT INTO public.building (building_id, code, name, faculty, preference_order, has_ac, has_computer_lab, campus, is_active)
VALUES
    (gen_random_uuid(), 'CH2', 'Humanas 2', 'Ciencias Humanas', 1, TRUE, TRUE, 'UIS', TRUE),
    (gen_random_uuid(), 'CH', 'Ciencias Humanas', 'Ciencias Humanas', 2, FALSE, FALSE, 'UIS', TRUE)
ON CONFLICT (code) DO NOTHING;

-- ── Classrooms → building ──────────────────────────────────
ALTER TABLE public.classroom
    ADD COLUMN IF NOT EXISTS building_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_classroom_building'
    ) THEN
        ALTER TABLE public.classroom
            ADD CONSTRAINT fk_classroom_building
            FOREIGN KEY (building_id) REFERENCES public.building (building_id);
    END IF;
END $$;

UPDATE public.classroom c
SET building_id = b.building_id
FROM public.building b
WHERE c.building_id IS NULL
  AND (
      UPPER(TRIM(COALESCE(c.building, ''))) = UPPER(b.code)
      OR UPPER(TRIM(COALESCE(c.building, ''))) = UPPER(b.name)
  );

-- ── Teacher catalog (no account required) ──────────────────
ALTER TABLE public.teacher
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(250);

ALTER TABLE public.teacher
    ADD COLUMN IF NOT EXISTS link_type VARCHAR(20);

UPDATE public.teacher t
SET full_name = COALESCE(
        NULLIF(TRIM(CONCAT(COALESCE(u.first_name, ''), ' ', COALESCE(u.last_name, ''))), ''),
        'Docente sin nombre'
    ),
    link_type = COALESCE(t.link_type, 'CATEDRA')
FROM public.users u
WHERE t.user_id = u.user_id
  AND (t.full_name IS NULL OR t.link_type IS NULL);

UPDATE public.teacher
SET full_name = COALESCE(full_name, 'Docente sin nombre'),
    link_type = COALESCE(link_type, 'CATEDRA')
WHERE full_name IS NULL OR link_type IS NULL;

ALTER TABLE public.teacher
    ALTER COLUMN full_name SET NOT NULL;

ALTER TABLE public.teacher
    ALTER COLUMN link_type SET NOT NULL;

ALTER TABLE public.teacher
    ALTER COLUMN link_type SET DEFAULT 'CATEDRA';

-- ── Class hour: session teacher + status ───────────────────
ALTER TABLE public.class_hour
    ADD COLUMN IF NOT EXISTS teacher_id UUID;

ALTER TABLE public.class_hour
    ADD COLUMN IF NOT EXISTS status VARCHAR(40);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_class_hour_teacher'
    ) THEN
        ALTER TABLE public.class_hour
            ADD CONSTRAINT fk_class_hour_teacher
            FOREIGN KEY (teacher_id) REFERENCES public.teacher (teacher_id);
    END IF;
END $$;

UPDATE public.class_hour ch
SET teacher_id = g.teacher_id
FROM public.groups g
WHERE ch.group_id = g.group_id
  AND ch.teacher_id IS NULL
  AND g.teacher_id IS NOT NULL;

UPDATE public.class_hour
SET status = CASE
        WHEN classroom_id IS NULL THEN 'NO_CLASSROOM_REQUIRED'
        ELSE 'ASSIGNED'
    END
WHERE status IS NULL;

ALTER TABLE public.class_hour
    ALTER COLUMN status SET DEFAULT 'PENDING';

ALTER TABLE public.class_hour
    ALTER COLUMN status SET NOT NULL;
