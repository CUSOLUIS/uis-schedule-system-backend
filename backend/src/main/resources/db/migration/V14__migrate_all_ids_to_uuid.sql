-- =========================================================
-- V12: Migrate ALL bigint IDENTITY primary keys and their
--      foreign key references to UUID.
-- =========================================================
-- Strategy:
--   1. Drop ALL FK constraints (named and auto-generated)
--   2. Convert every bigint PK column to uuid via
--      add-col / drop-col / rename pattern
--   3. Convert every bigint FK column to uuid
--   4. Do NOT re-create FK constraints here — V13 will
--      rewrite all data with deterministic UUIDs and then
--      create the FK constraints with consistent references.

-- =========================================================
-- PHASE 0: Drop ALL foreign-key constraints
-- =========================================================

-- Constraints created in V1 (auto-generated Hibernate names)
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

-- Constraints created in V6 (groups.classroom_id)
ALTER TABLE public.groups DROP CONSTRAINT IF EXISTS fk_groups_classroom;

-- Constraints created in V10 (class_hour_day join table)
ALTER TABLE public.class_hour_day DROP CONSTRAINT IF EXISTS fk_chd_class_hour;
ALTER TABLE public.class_hour_day DROP CONSTRAINT IF EXISTS fk_chd_day;

-- Any other named constraints that may exist
ALTER TABLE public.school  DROP CONSTRAINT IF EXISTS fk_school_faculty;
ALTER TABLE public.subject DROP CONSTRAINT IF EXISTS fk_subject_school;
ALTER TABLE public.groups  DROP CONSTRAINT IF EXISTS fk_groups_teacher;
ALTER TABLE public.groups  DROP CONSTRAINT IF EXISTS fk_groups_period;
ALTER TABLE public.groups  DROP CONSTRAINT IF EXISTS fk_groups_subject;
ALTER TABLE public.class   DROP CONSTRAINT IF EXISTS fk_class_group;
ALTER TABLE public.class   DROP CONSTRAINT IF EXISTS fk_class_user;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fk_class_hour_group;
ALTER TABLE public.class_hour DROP CONSTRAINT IF EXISTS fk_class_hour_classroom;
ALTER TABLE public.schedule DROP CONSTRAINT IF EXISTS fk_schedule_class;
ALTER TABLE public.schedule DROP CONSTRAINT IF EXISTS fk_schedule_user;
ALTER TABLE public.audit_log DROP CONSTRAINT IF EXISTS fk_audit_log_user;
ALTER TABLE public.teacher DROP CONSTRAINT IF EXISTS fk_teacher_user;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_user;
ALTER TABLE public.user_roles DROP CONSTRAINT IF EXISTS fk_user_roles_role;

-- =========================================================
-- PHASE 1: Convert leaf tables (no incoming FKs) to UUID
-- =========================================================

-- ─── 1. day_of_week (leaf) ────────────────────────────────
ALTER TABLE public.day_of_week ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.day_of_week ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.day_of_week DROP COLUMN day_id;
ALTER TABLE public.day_of_week RENAME COLUMN _uuid TO day_id;
ALTER TABLE public.day_of_week ADD PRIMARY KEY (day_id);

-- ─── 2. faculty (leaf) ───────────────────────────────────
ALTER TABLE public.faculty ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.faculty ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.faculty DROP COLUMN faculty_id;
ALTER TABLE public.faculty RENAME COLUMN _uuid TO faculty_id;
ALTER TABLE public.faculty ADD PRIMARY KEY (faculty_id);

-- ─── 3. academic_period (leaf) ────────────────────────────
ALTER TABLE public.academic_period ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.academic_period ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.academic_period DROP COLUMN period_id;
ALTER TABLE public.academic_period RENAME COLUMN _uuid TO period_id;
ALTER TABLE public.academic_period ADD PRIMARY KEY (period_id);

-- ─── 4. classroom (leaf) ─────────────────────────────────────
ALTER TABLE public.classroom ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.classroom ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.classroom DROP COLUMN classroom_id;
ALTER TABLE public.classroom RENAME COLUMN _uuid TO classroom_id;
ALTER TABLE public.classroom ADD PRIMARY KEY (classroom_id);

-- ─── 5. audit_log (FK → users is already uuid) ─────────────
ALTER TABLE public.audit_log ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.audit_log ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.audit_log DROP COLUMN audit_id;
ALTER TABLE public.audit_log RENAME COLUMN _uuid TO audit_id;
ALTER TABLE public.audit_log ADD PRIMARY KEY (audit_id);

-- =========================================================
-- PHASE 2: Convert tables with FK dependencies
-- =========================================================

-- ─── 6. school (FK → faculty) ───────────────────────────────
ALTER TABLE public.school ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.school ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.school DROP COLUMN school_id;
ALTER TABLE public.school RENAME COLUMN _uuid TO school_id;
ALTER TABLE public.school ADD PRIMARY KEY (school_id);
ALTER TABLE public.school ALTER COLUMN faculty_id TYPE uuid USING gen_random_uuid();

-- ─── 7. subject (FK → school) ──────────────────────────────
ALTER TABLE public.subject ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.subject ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.subject DROP COLUMN subject_id;
ALTER TABLE public.subject RENAME COLUMN _uuid TO subject_id;
ALTER TABLE public.subject ADD PRIMARY KEY (subject_id);
ALTER TABLE public.subject ALTER COLUMN school_id TYPE uuid USING gen_random_uuid();

-- ─── 8. teacher (FK → users is already uuid) ───────────────
ALTER TABLE public.teacher ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.teacher ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.teacher DROP COLUMN teacher_id;
ALTER TABLE public.teacher RENAME COLUMN _uuid TO teacher_id;
ALTER TABLE public.teacher ADD PRIMARY KEY (teacher_id);

-- ─── 9. groups (FK → teacher, period, subject, classroom) ───
ALTER TABLE public.groups ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.groups DROP COLUMN group_id;
ALTER TABLE public.groups RENAME COLUMN _uuid TO group_id;
ALTER TABLE public.groups ADD PRIMARY KEY (group_id);
ALTER TABLE public.groups ALTER COLUMN teacher_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN period_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN subject_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.groups ALTER COLUMN classroom_id TYPE uuid USING gen_random_uuid();

-- ─── 10. class (FK → user [uuid], group) ───────────────────
ALTER TABLE public.class ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.class ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.class DROP COLUMN class_id;
ALTER TABLE public.class RENAME COLUMN _uuid TO class_id;
ALTER TABLE public.class ADD PRIMARY KEY (class_id);
ALTER TABLE public.class ALTER COLUMN group_id TYPE uuid USING gen_random_uuid();

-- ─── 11. class_hour (FK → group, classroom) ────────────────
ALTER TABLE public.class_hour ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.class_hour ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.class_hour DROP COLUMN class_hour_id;
ALTER TABLE public.class_hour RENAME COLUMN _uuid TO class_hour_id;
ALTER TABLE public.class_hour ADD PRIMARY KEY (class_hour_id);
ALTER TABLE public.class_hour ALTER COLUMN group_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.class_hour ALTER COLUMN classroom_id TYPE uuid USING gen_random_uuid();

-- ─── 12. class_hour_day (join table) ──────────────────────
ALTER TABLE public.class_hour_day ALTER COLUMN class_hour_id TYPE uuid USING gen_random_uuid();
ALTER TABLE public.class_hour_day ALTER COLUMN day_id TYPE uuid USING gen_random_uuid();

-- ─── 13. schedule (FK → class, user [uuid]) ────────────────
ALTER TABLE public.schedule ADD COLUMN _uuid uuid DEFAULT gen_random_uuid();
ALTER TABLE public.schedule ALTER COLUMN _uuid SET NOT NULL;
ALTER TABLE public.schedule DROP COLUMN schedule_id;
ALTER TABLE public.schedule RENAME COLUMN _uuid TO schedule_id;
ALTER TABLE public.schedule ADD PRIMARY KEY (schedule_id);
ALTER TABLE public.schedule ALTER COLUMN class_id TYPE uuid USING gen_random_uuid();
