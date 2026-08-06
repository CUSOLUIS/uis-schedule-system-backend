-- =========================================================
-- V7: Classrooms & UUID schema
-- =========================================================
-- Flattened migration that consolidates:
--   - classroom.number type fix  (integer → varchar(50))
--   - classroom soft-delete      (is_active)
--   - groups: classroom_id FK + is_active
--   - class_hour: is_active, hour → start_time/end_time
--   - class_hour: start_date / end_date (academic period)
--   - class_hour_day join table   (ManyToMany with day_of_week)
--   - day_of_week: add "domingo", lowercase all names
--   - All academic-table PKs and FKs migrated from bigint to uuid
--   - Mock data with deterministic UUIDs
--   - All foreign-key constraints recreated with readable names
-- =========================================================

-- =========================================================
-- 1. CLASSROOM: fix number type & add soft-delete
-- =========================================================
ALTER TABLE public.classroom ALTER COLUMN number TYPE varchar(50) USING number::varchar;
ALTER TABLE public.classroom ADD COLUMN is_active boolean NOT NULL DEFAULT true;

-- =========================================================
-- 2. GROUPS: add classroom_id FK & soft-delete
-- =========================================================
ALTER TABLE public.groups ADD COLUMN classroom_id uuid;
ALTER TABLE public.classroom ALTER COLUMN classroom_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ADD COLUMN is_active boolean NOT NULL DEFAULT true;

-- =========================================================
-- 3. CLASS_HOUR: add is_active, replace hour with time range,
--    add academic period dates
-- =========================================================
ALTER TABLE public.class_hour ADD COLUMN is_active boolean NOT NULL DEFAULT true;

-- 3a. Replace single 'hour' with 'start_time' / 'end_time'
ALTER TABLE public.class_hour ADD COLUMN start_time time(6) WITHOUT TIME ZONE;
ALTER TABLE public.class_hour ADD COLUMN end_time time(6) WITHOUT TIME ZONE;

UPDATE public.class_hour SET start_time = hour, end_time = hour WHERE hour IS NOT NULL;
ALTER TABLE public.class_hour DROP COLUMN IF EXISTS hour;
ALTER TABLE public.class_hour ALTER COLUMN start_time SET NOT NULL;
ALTER TABLE public.class_hour ALTER COLUMN end_time SET NOT NULL;

-- 3b. Academic period date range
ALTER TABLE public.class_hour ADD COLUMN start_date date NOT NULL DEFAULT CURRENT_DATE;
ALTER TABLE public.class_hour ADD COLUMN end_date date NOT NULL DEFAULT (CURRENT_DATE + INTERVAL '6 months');

-- =========================================================
-- 4. CLASS_HOUR_DAY join table (ManyToMany class_hour ↔ day_of_week)
-- =========================================================
-- 4a. Migrate existing day_id data before dropping the column
INSERT INTO public.class_hour_day (class_hour_id, day_id)
SELECT ch.class_hour_id, ch.day_id
FROM public.class_hour ch
WHERE ch.day_id IS NOT NULL;

-- 4b. Drop single-day column from class_hour
ALTER TABLE public.class_hour DROP COLUMN IF EXISTS day_id;

-- 4c. Create join table (idempotent with IF NOT EXISTS)
CREATE TABLE IF NOT EXISTS public.class_hour_day (
    class_hour_id uuid NOT NULL,
    day_id        uuid NOT NULL,
    PRIMARY KEY (class_hour_id, day_id)
);

-- =========================================================
-- 5. DAY_OF_WEEK: add "domingo" & lowercase all names
-- =========================================================
-- (day_id is still bigint at this point)
INSERT INTO public.day_of_week (day_id, name)
VALUES (7, 'domingo')
ON CONFLICT DO NOTHING;

UPDATE public.day_of_week SET name = LOWER(name) WHERE name != LOWER(name);

-- =========================================================
-- 6. UUID MIGRATION — convert all bigint PKs/FKs to uuid
--    Strategy: add-col / drop-col / rename per table
-- =========================================================

-- ─── Drop ALL existing foreign-key constraints ──────────
ALTER TABLE public.audit_log DROP CONSTRAINT IF EXISTS fkk4alalwu62gj4tfbgfefll3tu;
ALTER TABLE public.school    DROP CONSTRAINT IF EXISTS fkflxxoc9bllhsatkm9f3qqv19f;
ALTER TABLE public.subject   DROP CONSTRAINT IF EXISTS fkg9ahxfklvfv5o2d8ejweibfo8;
ALTER TABLE public.teacher   DROP CONSTRAINT IF EXISTS fkcp1vpkh4bh0qux9vtvs0fkwrn;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fk5ltiwe1lwq5u03wcopmh34rd8;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fkh41v53xm83rq9vspgdjqjjsm2;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fkpduc2peqan6ivl2y1gcpad4qd;
ALTER TABLE public.class     DROP CONSTRAINT IF EXISTS fklbaki8a9xaoypxd2pte3vtwmi;
ALTER TABLE public.class     DROP CONSTRAINT IF EXISTS fkeg1d23px9j4dc1eadu1hmpcf3;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fkgfg6w7c0cm71ogs0n7i5858ef;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fkgsi4ulpggesf54tygqx81icn1;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fka4tho89bqrxnle81ua0oqkb67;
ALTER TABLE public.schedule  DROP CONSTRAINT IF EXISTS fkqqv2rqy5xxw2oyhie35seyclw;
ALTER TABLE public.schedule  DROP CONSTRAINT IF EXISTS fkdn5svbxyacce1gpfiawk7iqtc;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fkhfh9dx7w3ubf1co1vdev94g3f;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fkb23g9xsd1xwow8wmdse2c7ycs;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fk_groups_classroom;
ALTER TABLE public.class_hour_day DROP CONSTRAINT IF EXISTS fk_chd_class_hour;
ALTER TABLE public.class_hour_day DROP CONSTRAINT IF EXISTS fk_chd_day;
ALTER TABLE public.school    DROP CONSTRAINT IF EXISTS fk_school_faculty;
ALTER TABLE public.subject   DROP CONSTRAINT IF EXISTS fk_subject_school;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fk_groups_teacher;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fk_groups_period;
ALTER TABLE public.groups    DROP CONSTRAINT IF EXISTS fk_groups_subject;
ALTER TABLE public.class     DROP CONSTRAINT IF EXISTS fk_class_group;
ALTER TABLE public.class     DROP CONSTRAINT IF EXISTS fk_class_user;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fk_class_hour_group;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fk_class_hour_classroom;
ALTER TABLE public.schedule  DROP CONSTRAINT IF EXISTS fk_schedule_class;
ALTER TABLE public.schedule  DROP CONSTRAINT IF EXISTS fk_schedule_user;
ALTER TABLE public.audit_log DROP CONSTRAINT IF EXISTS fk_audit_log_user;
ALTER TABLE public.teacher   DROP CONSTRAINT IF EXISTS fk_teacher_user;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_user;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_role;

-- ─── 6.1 Leaf tables (no incoming FKs) ──────────────────

-- day_of_week
ALTER TABLE public.day_of_week ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.day_of_week ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.day_of_week DROP COLUMN day_id;
ALTER TABLE public.day_of_week RENAME COLUMN _uuid TO day_id;
ALTER TABLE public.day_of_week ADD PRIMARY KEY (day_id);

-- faculty
ALTER TABLE public.faculty ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.faculty ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.faculty DROP COLUMN faculty_id;
ALTER TABLE public.faculty RENAME COLUMN _uuid TO faculty_id;
ALTER TABLE public.faculty ADD PRIMARY KEY (faculty_id);

-- academic_period
ALTER TABLE public.academic_period ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.academic_period ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.academic_period DROP COLUMN period_id;
ALTER TABLE public.academic_period RENAME COLUMN _uuid TO period_id;
ALTER TABLE public.academic_period ADD PRIMARY KEY (period_id);

-- classroom (already has is_active from step 1, already varchar(50) from step 1)
ALTER TABLE public.classroom ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.classroom ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.classroom DROP COLUMN classroom_id;
ALTER TABLE public.classroom RENAME COLUMN _uuid TO classroom_id;
ALTER TABLE public.classroom ADD PRIMARY KEY (classroom_id);

-- audit_log
ALTER TABLE public.audit_log ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.audit_log ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.audit_log DROP COLUMN audit_id;
ALTER TABLE public.audit_log RENAME COLUMN _uuid TO audit_id;
ALTER TABLE public.audit_log ADD PRIMARY KEY (audit_id);

-- ─── 6.2 Tables with FK dependencies ───────────────────

-- school (FK → faculty)
ALTER TABLE public.school ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.school ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.school DROP COLUMN school_id;
ALTER TABLE public.school RENAME COLUMN _uuid TO school_id;
ALTER TABLE public.school ADD PRIMARY KEY (school_id);
ALTER TABLE public.school ALTER COLUMN faculty_id TYPE uuid USING gen_random_uuid();

-- subject (FK → school)
ALTER TABLE public.subject ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.subject ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.subject DROP COLUMN subject_id;
ALTER TABLE public.subject RENAME COLUMN _uuid TO subject_id;
ALTER TABLE public.subject ADD PRIMARY KEY (subject_id);
ALTER TABLE public.subject ALTER COLUMN school_id TYPE uuid USING gen_random_uuid();

-- teacher (FK → users — already uuid)
ALTER TABLE public.teacher ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.teacher ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.teacher DROP COLUMN teacher_id;
ALTER TABLE public.teacher RENAME COLUMN _uuid TO teacher_id;
ALTER TABLE public.teacher ADD PRIMARY KEY (teacher_id);

-- groups (FK → teacher, period, subject, classroom)
ALTER TABLE public.groups ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.groups DROP COLUMN group_id;
ALTER TABLE public.groups RENAME COLUMN _uuid TO group_id;
ALTER TABLE public.groups ADD PRIMARY KEY (group_id);
ALTER TABLE public.groups ALTER COLUMN teacher_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN period_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN subject_id TYPE uuid USING gen_random_uuid();

-- class (FK → user [already uuid], group)
ALTER TABLE public.class ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.class ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.class DROP COLUMN class_id;
ALTER TABLE public.class RENAME COLUMN _uuid TO class_id;
ALTER TABLE public.class ADD PRIMARY KEY (class_id);
ALTER TABLE public.class ALTER COLUMN group_id TYPE uuid USING gen_random_uuid();

-- class_hour (FK → group, classroom)
ALTER TABLE public.class_hour ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.class_hour ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.class_hour DROP COLUMN class_hour_id;
ALTER TABLE public.class_hour RENAME COLUMN _uuid TO class_hour_id;
ALTER TABLE public.class_hour ADD PRIMARY KEY (class_hour_id);
ALTER TABLE public.class_hour ALTER COLUMN group_id TYPE uuid USING gen_random_uuid();

-- class_hour_day (join table — both columns now uuid)
ALTER TABLE public.class_hour_day ALTER COLUMN class_hour_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.class_hour_day ALTER COLUMN day_id TYPE uuid USING gen_random_uuid();

-- schedule (FK → class, user [already uuid])
ALTER TABLE public.schedule ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.schedule ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.schedule DROP COLUMN schedule_id;
ALTER TABLE public.schedule RENAME COLUMN _uuid TO schedule_id;
ALTER TABLE public.schedule ADD PRIMARY KEY (schedule_id);
ALTER TABLE public.schedule ALTER COLUMN class_id TYPE uuid USING gen_random_uuid();

-- =========================================================
-- 7. MOCK DATA — deterministic UUIDs for academic entities
-- =========================================================

-- 7.1 Clean ALL existing mock data (reverse dependency order)
DELETE FROM public.class_hour_day;
DELETE FROM public.class_hour;
DELETE FROM public.class;
DELETE FROM public.groups;
DELETE FROM public.schedule;
DELETE FROM public.teacher;
DELETE FROM public.academic_period;
DELETE FROM public.subject;
DELETE FROM public.school;
DELETE FROM public.classroom;
DELETE FROM public.faculty;
DELETE FROM public.day_of_week;
DELETE FROM public.audit_log;

-- 7.2 Days of week (7 days, lowercase)
INSERT INTO public.day_of_week (day_id, name) VALUES
('a0000001-0000-0000-0000-000000000001', 'lunes'),
('a0000001-0000-0000-0000-000000000002', 'martes'),
('a0000001-0000-0000-0000-000000000003', 'miércoles'),
('a0000001-0000-0000-0000-000000000004', 'jueves'),
('a0000001-0000-0000-0000-000000000005', 'viernes'),
('a0000001-0000-0000-0000-000000000006', 'sábado'),
('a0000001-0000-0000-0000-000000000007', 'domingo');

-- 7.3 Faculty
INSERT INTO public.faculty (faculty_id, name) VALUES
('b0000001-0000-0000-0000-000000000001', 'Ingeniería de Sistemas');

-- 7.4 School
INSERT INTO public.school (school_id, faculty_id, name) VALUES
('c0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001', 'Ingeniería de Sistemas e Informática');

-- 7.5 Classrooms (with varchar number and is_active)
INSERT INTO public.classroom (classroom_id, number, max_capacity, building, campus, type, is_active) VALUES
('d0000001-0000-0000-0000-000000000001', '101', 30, 'Edificio B', 'Campus Principal', 'Teoría', true),
('d0000001-0000-0000-0000-000000000002', '201', 25, 'Edificio B', 'Campus Principal', 'Laboratorio', true),
('d0000001-0000-0000-0000-000000000003', '102', 35, 'Edificio A', 'Campus Principal', 'Teoría', true);

-- 7.6 Subjects
INSERT INTO public.subject (subject_id, code, name, credits, theory_hours, practice_hours, school_id) VALUES
('e0000001-0000-0000-0000-000000000001', 'IS-101', 'Programación I', 3, 2, 2, 'c0000001-0000-0000-0000-000000000001'),
('e0000001-0000-0000-0000-000000000002', 'IS-201', 'Estructuras de Datos', 3, 3, 1, 'c0000001-0000-0000-0000-000000000001'),
('e0000001-0000-0000-0000-000000000003', 'IS-301', 'Bases de Datos', 3, 2, 2, 'c0000001-0000-0000-0000-000000000001');

-- 7.7 Academic period
INSERT INTO public.academic_period (period_id, name, start_date, end_date, active) VALUES
('f0000001-0000-0000-0000-000000000001', '2026-I', '2026-01-15', '2026-06-15', true);

-- 7.8 Teacher (linked to existing user 'Nicole')
INSERT INTO public.teacher (teacher_id, user_id, availability, department) VALUES
('10000001-0000-0000-0000-000000000001', 'e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', 'Lunes a Viernes 8am-12pm', 'Ingeniería de Sistemas');

-- 7.9 Groups (with classroom_id and is_active)
INSERT INTO public.groups (group_id, group_name, capacity, teacher_id, period_id, subject_id, classroom_id, is_active) VALUES
('11000001-0000-0000-0000-000000000001', 'Programación I - Grupo A', 30, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', true),
('11000001-0000-0000-0000-000000000002', 'Programación I - Grupo B', 30, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002', true),
('11000001-0000-0000-0000-000000000003', 'Estructuras de Datos - Grupo A', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000003', true),
('11000001-0000-0000-0000-000000000004', 'Bases de Datos - Grupo A', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000001', true),
('11000001-0000-0000-0000-000000000005', 'Bases de Datos - Grupo B', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000002', true);

-- 7.10 Class hours (with start_time, end_time, start_date, end_date, is_active)
INSERT INTO public.class_hour (class_hour_id, start_time, end_time, group_id, classroom_id, start_date, end_date, is_active) VALUES
('12000001-0000-0000-0000-000000000001', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000002', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000002', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000003', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000003', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000004', '14:00:00', '16:00:00', '11000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000001', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000005', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000002', '2026-01-15', '2026-06-15', true);

-- 7.11 Class_hour_day (join table for ManyToMany)
INSERT INTO public.class_hour_day (class_hour_id, day_id) VALUES
('12000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001'),
('12000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002'),
('12000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000003'),
('12000001-0000-0000-0000-000000000004', 'a0000001-0000-0000-0000-000000000004'),
('12000001-0000-0000-0000-000000000005', 'a0000001-0000-0000-0000-000000000005');

-- =========================================================
-- 8. Re-create ALL foreign-key constraints
--    (now all columns are UUID and data is consistent)
-- =========================================================

-- school → faculty
ALTER TABLE public.school ADD CONSTRAINT fk_school_faculty
    FOREIGN KEY (faculty_id) REFERENCES public.faculty(faculty_id);

-- subject → school
ALTER TABLE public.subject ADD CONSTRAINT fk_subject_school
    FOREIGN KEY (school_id) REFERENCES public.school(school_id);

-- audit_log → users
ALTER TABLE public.audit_log ADD CONSTRAINT fk_audit_log_user
    FOREIGN KEY (user_id) REFERENCES public.users(user_id);

-- teacher → users
ALTER TABLE public.teacher ADD CONSTRAINT fk_teacher_user
    FOREIGN KEY (user_id) REFERENCES public.users(user_id);

-- groups → teacher
ALTER TABLE public.groups ADD CONSTRAINT fk_groups_teacher
    FOREIGN KEY (teacher_id) REFERENCES public.teacher(teacher_id);

-- groups → academic_period
ALTER TABLE public.groups ADD CONSTRAINT fk_groups_period
    FOREIGN KEY (period_id) REFERENCES public.academic_period(period_id);

-- groups → subject
ALTER TABLE public.groups ADD CONSTRAINT fk_groups_subject
    FOREIGN KEY (subject_id) REFERENCES public.subject(subject_id);

-- groups → classroom
ALTER TABLE public.groups ADD CONSTRAINT fk_groups_classroom
    FOREIGN KEY (classroom_id) REFERENCES public.classroom(classroom_id);

-- class → groups
ALTER TABLE public.class ADD CONSTRAINT fk_class_group
    FOREIGN KEY (group_id) REFERENCES public.groups(group_id);

-- class → users
ALTER TABLE public.class ADD CONSTRAINT fk_class_user
    FOREIGN KEY (user_id) REFERENCES public.users(user_id);

-- class_hour → groups
ALTER TABLE public.class_hour ADD CONSTRAINT fk_class_hour_group
    FOREIGN KEY (group_id) REFERENCES public.groups(group_id);

-- class_hour → classroom
ALTER TABLE public.class_hour ADD CONSTRAINT fk_class_hour_classroom
    FOREIGN KEY (classroom_id) REFERENCES public.classroom(classroom_id);

-- class_hour_day → class_hour (cascade delete)
ALTER TABLE public.class_hour_day ADD CONSTRAINT fk_chd_class_hour
    FOREIGN KEY (class_hour_id) REFERENCES public.class_hour(class_hour_id) ON DELETE CASCADE;

-- class_hour_day → day_of_week
ALTER TABLE public.class_hour_day ADD CONSTRAINT fk_chd_day
    FOREIGN KEY (day_id) REFERENCES public.day_of_week(day_id);

-- schedule → class
ALTER TABLE public.schedule ADD CONSTRAINT fk_schedule_class
    FOREIGN KEY (class_id) REFERENCES public.class(class_id);

-- schedule → users
ALTER TABLE public.schedule ADD CONSTRAINT fk_schedule_user
    FOREIGN KEY (user_id) REFERENCES public.users(user_id);

-- user_roles → users
ALTER TABLE public.user_roles ADD CONSTRAINT fk_user_roles_user
    FOREIGN KEY (user_id) REFERENCES public.users(user_id);

-- user_roles → roles
ALTER TABLE public.user_roles ADD CONSTRAINT fk_user_roles_role
    FOREIGN KEY (role_guid) REFERENCES public.roles(role_guid);
