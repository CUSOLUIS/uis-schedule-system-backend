-- =========================================================
-- V13: Rewrite mock data with controlled UUIDs after the
--      bigint → UUID migration in V12, then re-create all
--      foreign-key constraints with consistent references.
-- =========================================================

-- =========================================================
-- 1. Clean ALL existing mock data (reverse dependency order)
-- =========================================================
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

-- =========================================================
-- 2. DAYS OF WEEK (7 days, lowercase per V11 style)
-- =========================================================
INSERT INTO public.day_of_week (day_id, name) VALUES
('a0000001-0000-0000-0000-000000000001', 'lunes'),
('a0000001-0000-0000-0000-000000000002', 'martes'),
('a0000001-0000-0000-0000-000000000003', 'miércoles'),
('a0000001-0000-0000-0000-000000000004', 'jueves'),
('a0000001-0000-0000-0000-000000000005', 'viernes'),
('a0000001-0000-0000-0000-000000000006', 'sábado'),
('a0000001-0000-0000-0000-000000000007', 'domingo');

-- =========================================================
-- 3. FACULTY
-- =========================================================
INSERT INTO public.faculty (faculty_id, name) VALUES
('b0000001-0000-0000-0000-000000000001', 'Ingeniería de Sistemas');

-- =========================================================
-- 4. SCHOOL
-- =========================================================
INSERT INTO public.school (school_id, faculty_id, name) VALUES
('c0000001-0000-0000-0000-000000000001', 'b0000001-0000-0000-0000-000000000001', 'Ingeniería de Sistemas e Informática');

-- =========================================================
-- 5. CLASSROOMS
-- =========================================================
INSERT INTO public.classroom (classroom_id, number, max_capacity, building, campus, type, is_active) VALUES
('d0000001-0000-0000-0000-000000000001', '101', 30, 'Edificio B', 'Campus Principal', 'Teoría', true),
('d0000001-0000-0000-0000-000000000002', '201', 25, 'Edificio B', 'Campus Principal', 'Laboratorio', true),
('d0000001-0000-0000-0000-000000000003', '102', 35, 'Edificio A', 'Campus Principal', 'Teoría', true);

-- =========================================================
-- 6. SUBJECTS
-- =========================================================
INSERT INTO public.subject (subject_id, code, name, credits, theory_hours, practice_hours, school_id) VALUES
('e0000001-0000-0000-0000-000000000001', 'IS-101', 'Programación I', 3, 2, 2, 'c0000001-0000-0000-0000-000000000001'),
('e0000001-0000-0000-0000-000000000002', 'IS-201', 'Estructuras de Datos', 3, 3, 1, 'c0000001-0000-0000-0000-000000000001'),
('e0000001-0000-0000-0000-000000000003', 'IS-301', 'Bases de Datos', 3, 2, 2, 'c0000001-0000-0000-0000-000000000001');

-- =========================================================
-- 7. ACADEMIC PERIOD
-- =========================================================
INSERT INTO public.academic_period (period_id, name, start_date, end_date, active) VALUES
('f0000001-0000-0000-0000-000000000001', '2026-I', '2026-01-15', '2026-06-15', true);

-- =========================================================
-- 8. TEACHER (linked to existing user 'Nicole')
-- =========================================================
INSERT INTO public.teacher (teacher_id, user_id, availability, department) VALUES
('10000001-0000-0000-0000-000000000001', 'e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', 'Lunes a Viernes 8am-12pm', 'Ingeniería de Sistemas');

-- =========================================================
-- 9. GROUPS (5 mock records)
-- =========================================================
INSERT INTO public.groups (group_id, group_name, capacity, teacher_id, period_id, subject_id, classroom_id, is_active) VALUES
('11000001-0000-0000-0000-000000000001', 'Programación I - Grupo A', 30, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', true),
('11000001-0000-0000-0000-000000000002', 'Programación I - Grupo B', 30, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002', true),
('11000001-0000-0000-0000-000000000003', 'Estructuras de Datos - Grupo A', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000003', true),
('11000001-0000-0000-0000-000000000004', 'Bases de Datos - Grupo A', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000001', true),
('11000001-0000-0000-0000-000000000005', 'Bases de Datos - Grupo B', 25, '10000001-0000-0000-0000-000000000001', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000002', true);

-- =========================================================
-- 10. CLASS HOURS (5 mock records, with period dates)
-- =========================================================
INSERT INTO public.class_hour (class_hour_id, start_time, end_time, group_id, classroom_id, start_date, end_date, is_active) VALUES
('12000001-0000-0000-0000-000000000001', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000001', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000002', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000002', 'd0000001-0000-0000-0000-000000000002', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000003', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000003', 'd0000001-0000-0000-0000-000000000003', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000004', '14:00:00', '16:00:00', '11000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000001', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000005', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000002', '2026-01-15', '2026-06-15', true);

-- =========================================================
-- 11. CLASS_HOUR_DAY (join table for ManyToMany)
-- =========================================================
INSERT INTO public.class_hour_day (class_hour_id, day_id) VALUES
('12000001-0000-0000-0000-000000000001', 'a0000001-0000-0000-0000-000000000001'),
('12000001-0000-0000-0000-000000000002', 'a0000001-0000-0000-0000-000000000002'),
('12000001-0000-0000-0000-000000000003', 'a0000001-0000-0000-0000-000000000003'),
('12000001-0000-0000-0000-000000000004', 'a0000001-0000-0000-0000-000000000004'),
('12000001-0000-0000-0000-000000000005', 'a0000001-0000-0000-0000-000000000005');

-- =========================================================
-- 12. Re-create ALL foreign-key constraints
--     (now all columns are UUID and data is consistent)
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
