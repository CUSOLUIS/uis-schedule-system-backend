-- Add soft-delete support to classroom table
ALTER TABLE public.classroom ADD COLUMN is_active boolean NOT NULL DEFAULT true;
