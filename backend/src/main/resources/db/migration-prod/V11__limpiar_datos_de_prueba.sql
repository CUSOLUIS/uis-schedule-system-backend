-- =========================================================
-- V11 (solo prod): retira los datos de prueba que V2, V7 y V8
-- cargaron en todos los entornos.
--
-- Se conservan el administrador sembrado en V2, los roles y los
-- días de la semana: sin ellos no se puede entrar ni crear horarios.
--
-- Solo se borran filas con los UUID fijos de esas semillas. Si un
-- dato real ya depende de una de ellas (por ejemplo, un grupo creado
-- en el periodo o el aula de prueba), esa fila se conserva para no
-- romper ninguna llave foránea. En dev, los datos de prueba siguen
-- disponibles: dev no carga esta carpeta.
-- =========================================================

-- 1. Horas de clase de prueba (class_hour_day se borra en cascada).
DELETE FROM public.class_hour
WHERE class_hour_id IN (
    '12000001-0000-0000-0000-000000000001', '12000001-0000-0000-0000-000000000002',
    '12000001-0000-0000-0000-000000000003', '12000001-0000-0000-0000-000000000004',
    '12000001-0000-0000-0000-000000000005'
);

-- 2. Grupos de prueba sin horas ni matrículas reales.
DELETE FROM public.groups g
WHERE g.group_id IN (
    '11000001-0000-0000-0000-000000000001', '11000001-0000-0000-0000-000000000002',
    '11000001-0000-0000-0000-000000000003', '11000001-0000-0000-0000-000000000004',
    '11000001-0000-0000-0000-000000000005'
)
  AND NOT EXISTS (SELECT 1 FROM public.class_hour ch WHERE ch.group_id = g.group_id)
  AND NOT EXISTS (SELECT 1 FROM public.class c WHERE c.group_id = g.group_id);

-- 3. Fila docente del administrador (V8) y de los docentes de prueba (V7),
--    si ningún grupo la usa.
DELETE FROM public.teacher t
WHERE (t.teacher_id = '10000001-0000-0000-0000-000000000001'
       OR t.user_id IN (
           'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e',
           'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f'))
  AND NOT EXISTS (SELECT 1 FROM public.groups g WHERE g.teacher_id = t.teacher_id);

-- 4. Periodo, asignaturas y aulas de prueba sin grupos ni horas que los usen.
DELETE FROM public.academic_period p
WHERE p.period_id = 'f0000001-0000-0000-0000-000000000001'
  AND NOT EXISTS (SELECT 1 FROM public.groups g WHERE g.period_id = p.period_id);

DELETE FROM public.subject s
WHERE s.subject_id IN (
    'e0000001-0000-0000-0000-000000000001', 'e0000001-0000-0000-0000-000000000002',
    'e0000001-0000-0000-0000-000000000003'
)
  AND NOT EXISTS (SELECT 1 FROM public.groups g WHERE g.subject_id = s.subject_id);

DELETE FROM public.classroom r
WHERE r.classroom_id IN (
    'd0000001-0000-0000-0000-000000000001', 'd0000001-0000-0000-0000-000000000002',
    'd0000001-0000-0000-0000-000000000003'
)
  AND NOT EXISTS (SELECT 1 FROM public.groups g WHERE g.classroom_id = r.classroom_id)
  AND NOT EXISTS (SELECT 1 FROM public.class_hour ch WHERE ch.classroom_id = r.classroom_id);

-- 5. Escuela y facultad de prueba sin dependientes.
DELETE FROM public.school s
WHERE s.school_id = 'c0000001-0000-0000-0000-000000000001'
  AND NOT EXISTS (SELECT 1 FROM public.subject x WHERE x.school_id = s.school_id);

DELETE FROM public.faculty f
WHERE f.faculty_id = 'b0000001-0000-0000-0000-000000000001'
  AND NOT EXISTS (SELECT 1 FROM public.school s WHERE s.faculty_id = f.faculty_id);

-- 6. Usuarios de prueba: operador y estudiante de V2, y los tres docentes de V7.
--    Se borran solo si nada real los referencia. Sus tokens se borran en cascada.
CREATE TEMPORARY TABLE usuarios_de_prueba ON COMMIT DROP AS
SELECT u.user_id
FROM public.users u
WHERE u.user_id IN (
    '4a9f1d2b-3c5e-4f6a-9b1c-2d3e4f5a6b7c', '7b6a5c4d-3e2f-1a0b-9c8d-7e6f5a4b3c2d',
    'a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d', 'b2c3d4e5-f6a7-4b8c-9d0e-1f2a3b4c5d6e',
    'c3d4e5f6-a7b8-4c9d-0e1f-2a3b4c5d6e7f'
)
  AND NOT EXISTS (SELECT 1 FROM public.teacher t WHERE t.user_id = u.user_id)
  AND NOT EXISTS (SELECT 1 FROM public.class c WHERE c.user_id = u.user_id)
  AND NOT EXISTS (SELECT 1 FROM public.schedule s WHERE s.user_id = u.user_id)
  AND NOT EXISTS (SELECT 1 FROM public.audit_log a WHERE a.user_id = u.user_id)
  AND NOT EXISTS (SELECT 1 FROM public.user_invitation i WHERE i.created_by = u.user_id);

DELETE FROM public.user_roles WHERE user_id IN (SELECT user_id FROM usuarios_de_prueba);
DELETE FROM public.users WHERE user_id IN (SELECT user_id FROM usuarios_de_prueba);
