CREATE TABLE IF NOT EXISTS public.password_reset_token (
    token_id    uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     uuid         NOT NULL,
    token       varchar(255) NOT NULL UNIQUE,
    created_at  timestamp    NOT NULL DEFAULT now(),
    expires_at  timestamp    NOT NULL,
    used_at     timestamp,
    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_password_reset_token_user_id
    ON public.password_reset_token (user_id);
