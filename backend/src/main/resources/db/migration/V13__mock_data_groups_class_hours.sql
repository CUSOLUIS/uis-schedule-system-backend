-- =========================================================
-- MOCK DATA: Groups and ClassHours
-- =========================================================
-- Dependency chain: DayWeek, Classroom (already may exist from V4),
-- Faculty → School → Subject, Teacher → User (existing),
-- AcademicPeriod → Group → ClassHour

-- =========================================================
-- 1. DAYS OF WEEK (2 needed for class hours)
-- =========================================================
INSERT INTO public.day_of_week (day_id, name) VALUES
(1, 'Lunes'),
(2, 'Martes'),
(3, 'Miércoles'),
(4, 'Jueves'),
(5, 'Viernes'),
(6, 'Sábado')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 2. CLASSROOM (1 additional if not exists)
-- =========================================================
INSERT INTO public.classroom (classroom_id, number, max_capacity, building, campus, type, is_active) VALUES
(1, '101', 30, 'Edificio B', 'Campus Principal', 'Teoría', true),
(2, '201', 25, 'Edificio B', 'Campus Principal', 'Laboratorio', true),
(3, '102', 35, 'Edificio A', 'Campus Principal', 'Teoría', true)
ON CONFLICT DO NOTHING;

-- =========================================================
-- 3. FACULTY
-- =========================================================
INSERT INTO public.faculty (faculty_id, name) VALUES
(1, 'Ingeniería de Sistemas')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 4. SCHOOL
-- =========================================================
INSERT INTO public.school (school_id, faculty_id, name) VALUES
(1, 1, 'Ingeniería de Sistemas e Informática')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 5. SUBJECTS (3 subjects for variety in groups)
-- =========================================================
INSERT INTO public.subject (subject_id, code, name, credits, theory_hours, practice_hours, school_id) VALUES
(1, 'IS-101', 'Programación I', 3, 2, 2, 1),
(2, 'IS-201', 'Estructuras de Datos', 3, 3, 1, 1),
(3, 'IS-301', 'Bases de Datos', 3, 2, 2, 1)
ON CONFLICT DO NOTHING;

-- =========================================================
-- 6. ACADEMIC PERIOD
-- =========================================================
INSERT INTO public.academic_period (period_id, name, start_date, end_date, active) VALUES
(1, '2026-I', '2026-01-15', '2026-06-15', true)
ON CONFLICT DO NOTHING;

-- =========================================================
-- 7. TEACHER (linked to existing user 'Nicole' — ADMINISTRADOR)
-- =========================================================
INSERT INTO public.teacher (teacher_id, user_id, availability, department) VALUES
(1, 'e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', 'Lunes a Viernes 8am-12pm', 'Ingeniería de Sistemas')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 8. GROUPS (5 mock records)
-- =========================================================
INSERT INTO public.groups (group_id, group_name, capacity, teacher_id, period_id, subject_id, classroom_id, is_active) VALUES
(1, 'Programación I - Grupo A', 30, 1, 1, 1, 1, true),
(2, 'Programación I - Grupo B', 30, 1, 1, 1, 2, true),
(3, 'Estructuras de Datos - Grupo A', 25, 1, 1, 2, 3, true),
(4, 'Bases de Datos - Grupo A', 25, 1, 1, 3, 1, true),
(5, 'Bases de Datos - Grupo B', 25, 1, 1, 3, 2, true)
ON CONFLICT DO NOTHING;

-- =========================================================
-- 9. CLASS HOURS (5 mock records)
-- =========================================================
INSERT INTO public.class_hour (class_hour_id, start_time, end_time, day_id, group_id, classroom_id, is_active) VALUES
-- Group 1 (Prog I - A): Lunes 8:00-10:00 en Aula 101
(1, '08:00:00', '10:00:00', 1, 1, 1, true),
-- Group 2 (Prog I - B): Martes 10:00-12:00 en Lab 201
(2, '10:00:00', '12:00:00', 2, 2, 2, true),
-- Group 3 (ED - A): Miércoles 08:00-10:00 en Aula 102
(3, '08:00:00', '10:00:00', 3, 3, 3, true),
-- Group 4 (BD - A): Jueves 14:00-16:00 en Aula 101
(4, '14:00:00', '16:00:00', 4, 4, 1, true),
-- Group 5 (BD - B): Viernes 10:00-12:00 en Lab 201
(5, '10:00:00', '12:00:00', 5, 5, 2, true)
ON CONFLICT DO NOTHING;
