-- Add soft-delete support to class_hour table
ALTER TABLE public.class_hour ADD COLUMN is_active boolean NOT NULL DEFAULT true;
