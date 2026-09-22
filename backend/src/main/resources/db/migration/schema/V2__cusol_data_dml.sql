
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
