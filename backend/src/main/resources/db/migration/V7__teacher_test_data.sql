SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

-- =========================================================
-- Usuario de prueba con rol de profesor (TEACHER)
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
(true, true, true, true, NULL, 'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'David', 'Trujillo', 'dtrujillo', 'dtrujillo@uis.edu.co', '$2b$10$ZC3Bpd0rUnXN2RHYFmn6F.cOrMvNO87/SnJEMk3Cv3Dt/U8ynesLO');


INSERT INTO public.user_roles (user_id, role_guid) VALUES
('a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff');


INSERT INTO public.teacher (availability, department, user_id) VALUES
('Lunes a Viernes 8:00-12:00', 'Ingeniería de Sistemas', (SELECT user_id FROM public.users WHERE username = 'dtrujillo'));

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
(true, true, true, true, NULL, 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'Laura', 'Ramírez', 'lramirez', 'lramirez@uis.edu.co', '$2b$10$ZC3Bpd0rUnXN2RHYFmn6F.cOrMvNO87/SnJEMk3Cv3Dt/U8ynesLO'),
(true, true, true, true, NULL, 'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'Andrés', 'Suárez', 'asuarez', 'asuarez@uis.edu.co', '$2b$10$ZC3Bpd0rUnXN2RHYFmn6F.cOrMvNO87/SnJEMk3Cv3Dt/U8ynesLO');

INSERT INTO public.user_roles (user_id, role_guid) VALUES
('b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff'),
('c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f', 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff');

INSERT INTO public.teacher (availability, department, user_id) VALUES
('Lunes a Viernes 14:00-18:00', 'Ingeniería Electrónica', (SELECT user_id FROM public.users WHERE username = 'lramirez')),
('Martes y Jueves 8:00-12:00', 'Ingeniería Industrial', (SELECT user_id FROM public.users WHERE username = 'asuarez'));
