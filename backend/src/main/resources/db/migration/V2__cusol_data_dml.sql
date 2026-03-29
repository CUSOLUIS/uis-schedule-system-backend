
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- =========================================================
-- ROLES
-- =========================================================

INSERT INTO public.roles (is_active, created_at, updated_at, role_guid, name) VALUES
(true,  '2026-02-21 14:12:39.896475', '2026-02-21 14:12:39.896475', '3d7254fb-3e6f-46d8-8dfe-3e07b5887931', 'ADMINISTRADOR'),
(true,  '2026-02-21 14:12:39.979441', '2026-02-21 14:12:39.979441', '1cfac455-f140-4841-a31a-b4a3465c4457', 'OPERADOR'),
(true,  '2026-02-21 14:12:39.992679', '2026-02-21 14:12:39.992679', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff', 'DOCENTE'),
(true,  '2026-02-21 14:12:40.005516', '2026-02-21 14:12:40.005516', 'a7f7d824-655d-44f9-baab-72fbde2eaca3', 'ESTUDIANTE');

-- =========================================================
-- USUARIOS
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
-- UUIDs v4 generados para usuarios
(true, true, true, true, NULL, 'e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', 'Nicole',  'Alvarez', 'nalvarez', 'nicole0202@uis.edu.co',  '$2a$10$0/XUxFTQepl9Nv5dIlIhI./IJjA4QT3DL4PK90I3MSCA9InB1.WpW'),
(true, true, true, true, NULL, '4a9f1d2b-3c5e-4f6a-9b1c-2d3e4f5a6b7c', 'Dayanna', 'Diaz',    'ddiaz',    'dayanna123@uis.edu.co', '$2a$10$tOkjthC5qROvhVFDYDYNc.MLIShWlI1brUyhQykHLUMboCvf7P.8i'),
(true, true, true, true, NULL, '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'Marcos',  'Perez',   'mperez',   'marcos123@uis.edu.co',  '$2a$10$kvVSq6AbfkfUJPshJjFUseC8kTCiZmktnAHHCvZIIytCvKokcjoBS');

-- =========================================================
-- RELACIÓN USUARIO - ROL
-- =========================================================

INSERT INTO public.user_roles (user_id, role_guid) VALUES
('e7f3c6a4-8fb1-4ba2-b3d4-1c2f3e4a5b6c', '3d7254fb-3e6f-46d8-8dfe-3e07b5887931'),
('4a9f1d2b-3c5e-4f6a-9b1c-2d3e4f5a6b7c', '1cfac455-f140-4841-a31a-b4a3465c4457'),
('7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d', 'a7f7d824-655d-44f9-baab-72fbde2eaca3');


