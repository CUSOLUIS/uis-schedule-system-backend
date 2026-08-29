-- Add classroom foreign key and soft-delete support to groups table
ALTER TABLE public.groups ADD COLUMN classroom_id bigint;
ALTER TABLE public.groups ADD CONSTRAINT fk_groups_classroom
    FOREIGN KEY (classroom_id) REFERENCES public.classroom(classroom_id);
ALTER TABLE public.groups ADD COLUMN is_active boolean NOT NULL DEFAULT true;
