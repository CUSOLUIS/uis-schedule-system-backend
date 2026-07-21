-- Fix classroom.number column type: integer → varchar(50) to match ClassroomEntity (String number)
ALTER TABLE public.classroom ALTER COLUMN number TYPE varchar(50) USING number::varchar;
