
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- =========================================================
-- USUARIOS DE PRUEBA (dev only)
-- Todas las contraseñas de prueba comparten el mismo hash
-- bcrypt ('$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
-- son credenciales ficticias, no reutilizar en ningún otro entorno.
-- =========================================================

INSERT INTO public.users (
    account_no_expired,
    account_no_locked,
    credential_no_expired,
    is_enabled,
    last_session,
    user_id,
    first_name,
    last_name,
    username,
    email,
    password
) VALUES
-- Usuarios originales
(true, true, true, true, NULL, 'e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', 'Nicole',    'Alvarez', 'nalvarez', 'nicole0202@uis.edu.co',    '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '4a9f1d2b-3c5e-4f6a-9b1c-2d3e4f5a6b7c', 'Dayanna',   'Diaz',    'ddiaz',    'dayanna123@uis.edu.co',    '$2a$10$tOkjthC5qROvhVFDYDYNc.MLIShWlI1brUyhQykHLUMboCvf7P.8i'),
(true, true, true, true, NULL, '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'Marcos',    'Perez',   'mperez',   'marcos123@uis.edu.co',     '$2a$10$kvVSq6AbfkfUJPshJjFUseC8kTCiZmktnAHHCvZIIytCvKokcjoBS'),
-- Administrador adicional
(true, true, true, true, NULL, '20000001-0000-0000-0000-000000000001', 'Carlos',    'Gómez',   'cgomez',   'carlos.gomez@uis.edu.co',  '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
-- Operador adicional
(true, true, true, true, NULL, '20000002-0000-0000-0000-000000000001', 'Julián',    'Rojas',   'jrojas',   'julian.rojas@uis.edu.co',  '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
-- Estudiantes adicionales
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000001', 'Valentina', 'Torres',  'vtorres',  'valentina.torres@uis.edu.co', '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000002', 'Santiago',  'Medina',  'smedina',  'santiago.medina@uis.edu.co',  '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000003', 'Camila',    'Rueda',   'crueda',   'camila.rueda@uis.edu.co',     '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000004', 'Juan Pablo','Cala',    'jcala',    'juan.cala@uis.edu.co',        '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '30000001-0000-0000-0000-000000000005', 'Mariana',   'Duarte',  'mduarte',  'mariana.duarte@uis.edu.co',   '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW');

-- =========================================================
-- RELACIÓN USUARIO - ROL
-- =========================================================

INSERT INTO public.user_roles (user_id, role_guid) VALUES
-- Originales
('e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', '3d7254fb-3e6f-46d8-8dfe-3e07b5887931'), -- Nicole -> ADMINISTRADOR
('4a9f1d2b-3c5e-4f6a-9b1c-2d3e4f5a6b7c', '1cfac455-f140-4841-a31a-b4a3465c4457'), -- Dayanna -> OPERADOR
('7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'), -- Marcos -> ESTUDIANTE
-- Nuevos
('20000001-0000-0000-0000-000000000001', '3d7254fb-3e6f-46d8-8dfe-3e07b5887931'), -- Carlos -> ADMINISTRADOR
('20000002-0000-0000-0000-000000000001', '1cfac455-f140-4841-a31a-b4a3465c4457'), -- Julián -> OPERADOR
('30000001-0000-0000-0000-000000000001', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'), -- Valentina -> ESTUDIANTE
('30000001-0000-0000-0000-000000000002', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'), -- Santiago -> ESTUDIANTE
('30000001-0000-0000-0000-000000000003', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'), -- Camila -> ESTUDIANTE
('30000001-0000-0000-0000-000000000004', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'), -- Juan Pablo -> ESTUDIANTE
('30000001-0000-0000-0000-000000000005', 'a7f7d824-655d-44f9-baab-72fbde2eaca3'); -- Mariana -> ESTUDIANTE
