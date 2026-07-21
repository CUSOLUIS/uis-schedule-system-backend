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

