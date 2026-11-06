-- Repair teacher profiles for accounts that already have the TEACHER role
-- but no row in teacher. Does not rewrite migrations V1-V9.
-- Existing teacher rows (including availability and department) are left as they are.

INSERT INTO public.teacher (teacher_id, user_id, availability, department)
SELECT gen_random_uuid(), u.user_id, NULL, NULL
FROM public.users u
JOIN public.user_roles ur ON ur.user_id = u.user_id
JOIN public.roles r ON r.role_guid = ur.role_guid
WHERE upper(r.name) = 'TEACHER'
  AND NOT EXISTS (
      SELECT 1 FROM public.teacher t WHERE t.user_id = u.user_id
  );
