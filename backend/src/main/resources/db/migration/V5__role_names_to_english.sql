SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;


UPDATE public.roles SET name = 'ADMINISTRATOR' WHERE role_guid = '3d7254fb-3e6f-46d8-8dfe-3e07b5887931';
UPDATE public.roles SET name = 'OPERATOR'      WHERE role_guid = '1cfac455-f140-4841-a31a-b4a3465c4457';
UPDATE public.roles SET name = 'TEACHER'       WHERE role_guid = 'b60f3f14-8d1c-4039-9dd6-a8d646a809ff';
UPDATE public.roles SET name = 'STUDENT'       WHERE role_guid = 'a7f7d824-655d-44f9-baab-72fbde2eaca3';

-- End of migration