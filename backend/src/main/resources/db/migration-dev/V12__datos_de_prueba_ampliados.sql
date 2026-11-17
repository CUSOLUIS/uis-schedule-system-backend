-- =========================================================
-- V12 (solo dev): datos de prueba ampliados.
--
-- Corre encima de lo que ya dejan V2, V7 y V8: agrega docentes,
-- aulas, asignaturas, periodos, grupos, matrículas (class) y horarios
-- (schedule) para tener variedad al probar. Todos los nombres son
-- ficticios y todas las contraseñas comparten el hash de prueba de V2.
--
-- Corrige además que los docentes de V7 quedaban sin fila en teacher:
-- V8 vacía esa tabla después de que V7 la llena.
--
-- Los docentes se buscan por user_id y no por teacher_id, para que la
-- migración no falle si una base de dev ya creó su fila desde la API.
-- UUID por entidad: 10 teacher, 11 groups, 12 class_hour, 13 class,
-- 14 schedule, 20 admin y operador, 30 estudiantes.
-- =========================================================

-- 1. Usuarios nuevos: dos docentes, un administrador, un operador y cinco estudiantes.
INSERT INTO public.users (
    account_no_expired, account_no_locked, credential_no_expired, is_enabled, last_session,
    user_id, first_name, last_name, username, email, password
) VALUES
(true, true, true, true, NULL, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'Patricia',   'Vega',    'pvega',    'pvega@uis.edu.co',            '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'Jorge',      'Herrera', 'jherrera', 'jherrera@uis.edu.co',         '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '20000001-0000-0000-0000-000000000001', 'Carlos',     'Gómez',   'cgomez',   'carlos.gomez@uis.edu.co',     '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '20000002-0000-0000-0000-000000000001', 'Julián',     'Rojas',   'jrojas',   'julian.rojas@uis.edu.co',     '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000001', 'Valentina',  'Torres',  'vtorres',  'valentina.torres@uis.edu.co', '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000002', 'Santiago',   'Medina',  'smedina',  'santiago.medina@uis.edu.co',  '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000003', 'Camila',     'Rueda',   'crueda',   'camila.rueda@uis.edu.co',     '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000004', 'Juan Pablo', 'Cala',    'jcala',    'juan.cala@uis.edu.co',        '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000005', 'Mariana',    'Duarte',  'mduarte',  'mariana.duarte@uis.edu.co',   '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW')
ON CONFLICT DO NOTHING;

-- Roles (V5): 3d72… ADMINISTRATOR, 1cfa… OPERATOR, b60f… TEACHER, a7f7… STUDENT.
INSERT INTO public.user_roles (user_id, role_guid) VALUES
('d4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff'),
('e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff'),
('20000001-0000-0000-0000-000000000001', '3d7254fb-3e6f-46d8-8dfe-3e07b5887931'),
('20000002-0000-0000-0000-000000000001', '1cfac455-f140-4841-a31a-b4a3465c4457'),
('30000001-0000-0000-0000-000000000001', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'),
('30000001-0000-0000-0000-000000000002', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'),
('30000001-0000-0000-0000-000000000003', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'),
('30000001-0000-0000-0000-000000000004', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'),
('30000001-0000-0000-0000-000000000005', 'a7f7d824-655d-44f9-baab-72fbde2eaca3')
ON CONFLICT DO NOTHING;

-- 2. Filas docentes: las tres de V7 que V8 borró y las dos nuevas.
INSERT INTO public.teacher (teacher_id, user_id, availability, department)
SELECT v.teacher_id::uuid, v.user_id::uuid, v.availability, v.department
FROM (VALUES
    ('10000001-0000-0000-0000-000000000002', 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'Lunes a Viernes 8:00-12:00',             'Ingeniería de Sistemas'),
    ('10000001-0000-0000-0000-000000000003', 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'Lunes a Viernes 14:00-18:00',            'Ingeniería Electrónica'),
    ('10000001-0000-0000-0000-000000000004', 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'Martes y Jueves 8:00-12:00',             'Ingeniería Industrial'),
    ('10000001-0000-0000-0000-000000000005', 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'Lunes, Miércoles y Viernes 10:00-14:00', 'Ingeniería de Sistemas'),
    ('10000001-0000-0000-0000-000000000006', 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'Martes y Jueves 14:00-18:00',            'Ingeniería Electrónica')
) AS v(teacher_id, user_id, availability, department)
WHERE EXISTS (SELECT 1 FROM public.users u WHERE u.user_id = v.user_id::uuid)
  AND NOT EXISTS (SELECT 1 FROM public.teacher t WHERE t.user_id = v.user_id::uuid);

-- 3. Segunda escuela, más aulas, asignaturas y un periodo pasado.
INSERT INTO public.school (school_id, faculty_id, name) VALUES
('c0000001-0000-0000-0000-000000000002', 'b0000001-0000-0000-0000-000000000001', 'Ingeniería Electrónica')
ON CONFLICT DO NOTHING;

INSERT INTO public.classroom (classroom_id, number, max_capacity, building, campus, type, is_active) VALUES
('d0000001-0000-0000-0000-000000000004', '202', 20, 'Edificio B', 'Campus Principal', 'Laboratorio', true),
('d0000001-0000-0000-0000-000000000005', '301', 40, 'Edificio C', 'Campus Principal', 'Teoría',      true),
('d0000001-0000-0000-0000-000000000006', '302', 22, 'Edificio C', 'Campus Principal', 'Laboratorio', true)
ON CONFLICT DO NOTHING;

INSERT INTO public.subject (subject_id, code, name, credits, theory_hours, practice_hours, school_id) VALUES
('e0000001-0000-0000-0000-000000000004', 'IS-401', 'Ingeniería de Software', 4, 2, 2, 'c0000001-0000-0000-0000-000000000001'),
('e0000001-0000-0000-0000-000000000005', 'IE-101', 'Circuitos I',            3, 2, 2, 'c0000001-0000-0000-0000-000000000002'),
('e0000001-0000-0000-0000-000000000006', 'IE-201', 'Señales y Sistemas',     3, 3, 1, 'c0000001-0000-0000-0000-000000000002'),
('e0000001-0000-0000-0000-000000000007', 'IE-301', 'Electrónica Digital',    3, 2, 2, 'c0000001-0000-0000-0000-000000000002')
ON CONFLICT DO NOTHING;

INSERT INTO public.academic_period (period_id, name, start_date, end_date, active) VALUES
('f0000001-0000-0000-0000-000000000002', '2025-II', '2025-08-01', '2025-12-10', false)
ON CONFLICT DO NOTHING;

-- 4. Reparte entre varios docentes los grupos de V8, que estaban todos a cargo del administrador.
UPDATE public.groups
SET teacher_id = (SELECT teacher_id FROM public.teacher WHERE user_id = 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d')
WHERE group_id IN ('11000001-0000-0000-0000-000000000002', '11000001-0000-0000-0000-000000000003');

UPDATE public.groups
SET teacher_id = (SELECT teacher_id FROM public.teacher WHERE user_id = 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a')
WHERE group_id = '11000001-0000-0000-0000-000000000005';

-- 5. Grupos nuevos, uno de ellos inactivo en el periodo pasado.
INSERT INTO public.groups (group_id, group_name, capacity, teacher_id, period_id, subject_id, classroom_id, is_active)
SELECT v.group_id::uuid, v.group_name, v.capacity,
       (SELECT t.teacher_id FROM public.teacher t WHERE t.user_id = v.teacher_user::uuid),
       v.period_id::uuid, v.subject_id::uuid, v.classroom_id::uuid, v.is_active
FROM (VALUES
    ('11000001-0000-0000-0000-000000000006', 'Ingeniería de Software - Grupo A',     20, 'd4e5f6a7-b8c9-4d0e-1f2a-3b4c5d6e7f8a', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000004', 'd0000001-0000-0000-0000-000000000005', true),
    ('11000001-0000-0000-0000-000000000007', 'Circuitos I - Grupo A',                25, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000005', 'd0000001-0000-0000-0000-000000000004', true),
    ('11000001-0000-0000-0000-000000000008', 'Señales y Sistemas - Grupo A',         20, 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000006', true),
    ('11000001-0000-0000-0000-000000000009', 'Electrónica Digital - Grupo A',        20, 'e5f6a7b8-c9d0-4e1f-2a3b-4c5d6e7f8a9b', 'f0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000004', true),
    ('11000001-0000-0000-0000-000000000010', 'Programación I - Grupo C (histórico)', 30, 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'f0000001-0000-0000-0000-000000000002', 'e0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000003', false)
) AS v(group_id, group_name, capacity, teacher_user, period_id, subject_id, classroom_id, is_active)
ON CONFLICT DO NOTHING;

-- 6. Una hora de clase por grupo nuevo, con su día.
INSERT INTO public.class_hour (class_hour_id, start_time, end_time, group_id, classroom_id, start_date, end_date, is_active) VALUES
('12000001-0000-0000-0000-000000000006', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000006', 'd0000001-0000-0000-0000-000000000005', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000007', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000007', 'd0000001-0000-0000-0000-000000000004', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000008', '14:00:00', '16:00:00', '11000001-0000-0000-0000-000000000008', 'd0000001-0000-0000-0000-000000000006', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000009', '08:00:00', '10:00:00', '11000001-0000-0000-0000-000000000009', 'd0000001-0000-0000-0000-000000000004', '2026-01-15', '2026-06-15', true),
('12000001-0000-0000-0000-000000000010', '10:00:00', '12:00:00', '11000001-0000-0000-0000-000000000010', 'd0000001-0000-0000-0000-000000000003', '2025-08-01', '2025-12-10', false)
ON CONFLICT DO NOTHING;

INSERT INTO public.class_hour_day (class_hour_id, day_id) VALUES
('12000001-0000-0000-0000-000000000006', 'a0000001-0000-0000-0000-000000000001'),
('12000001-0000-0000-0000-000000000007', 'a0000001-0000-0000-0000-000000000002'),
('12000001-0000-0000-0000-000000000008', 'a0000001-0000-0000-0000-000000000003'),
('12000001-0000-0000-0000-000000000009', 'a0000001-0000-0000-0000-000000000004'),
('12000001-0000-0000-0000-000000000010', 'a0000001-0000-0000-0000-000000000005')
ON CONFLICT DO NOTHING;

-- 7. Matrículas de los estudiantes (no existían en ningún entorno).
INSERT INTO public.class (class_id, group_id, user_id, class_type) VALUES
('13000001-0000-0000-0000-000000000001', '11000001-0000-0000-0000-000000000001', '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'REGULAR'),
('13000001-0000-0000-0000-000000000002', '11000001-0000-0000-0000-000000000003', '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'REGULAR'),
('13000001-0000-0000-0000-000000000003', '11000001-0000-0000-0000-000000000002', '30000001-0000-0000-0000-000000000001', 'REGULAR'),
('13000001-0000-0000-0000-000000000004', '11000001-0000-0000-0000-000000000004', '30000001-0000-0000-0000-000000000001', 'REGULAR'),
('13000001-0000-0000-0000-000000000005', '11000001-0000-0000-0000-000000000001', '30000001-0000-0000-0000-000000000002', 'REGULAR'),
('13000001-0000-0000-0000-000000000006', '11000001-0000-0000-0000-000000000007', '30000001-0000-0000-0000-000000000003', 'REGULAR'),
('13000001-0000-0000-0000-000000000007', '11000001-0000-0000-0000-000000000008', '30000001-0000-0000-0000-000000000003', 'REGULAR'),
('13000001-0000-0000-0000-000000000008', '11000001-0000-0000-0000-000000000005', '30000001-0000-0000-0000-000000000004', 'REGULAR'),
('13000001-0000-0000-0000-000000000009', '11000001-0000-0000-0000-000000000009', '30000001-0000-0000-0000-000000000005', 'REGULAR'),
('13000001-0000-0000-0000-000000000010', '11000001-0000-0000-0000-000000000010', '30000001-0000-0000-0000-000000000005', 'REGULAR')
ON CONFLICT DO NOTHING;

-- 8. Horario: a lo sumo una fila por usuario (UNIQUE(user_id) desde V1).
INSERT INTO public.schedule (schedule_id, class_id, user_id) VALUES
('14000001-0000-0000-0000-000000000001', '13000001-0000-0000-0000-000000000001', '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d'),
('14000001-0000-0000-0000-000000000002', '13000001-0000-0000-0000-000000000003', '30000001-0000-0000-0000-000000000001'),
('14000001-0000-0000-0000-000000000003', '13000001-0000-0000-0000-000000000005', '30000001-0000-0000-0000-000000000002'),
('14000001-0000-0000-0000-000000000004', '13000001-0000-0000-0000-000000000006', '30000001-0000-0000-0000-000000000003'),
('14000001-0000-0000-0000-000000000005', '13000001-0000-0000-0000-000000000008', '30000001-0000-0000-0000-000000000004'),
('14000001-0000-0000-0000-000000000006', '13000001-0000-0000-0000-000000000009', '30000001-0000-0000-0000-000000000005')
ON CONFLICT DO NOTHING;
