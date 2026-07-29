CREATE TABLE IF NOT EXISTS public.revoked_token (
    revoked_token_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    jti               varchar(255) NOT NULL UNIQUE,
    user_id           uuid         NOT NULL,
    revoked_at        timestamp    NOT NULL DEFAULT now(),
    expires_at        timestamp    NOT NULL,
    CONSTRAINT fk_revoked_token_user
        FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_revoked_token_jti
    ON public.revoked_token (jti);

CREATE INDEX IF NOT EXISTS idx_revoked_token_expires_at
    ON public.revoked_token (expires_at);


CREATE TABLE IF NOT EXISTS public.refresh_token (
    refresh_token_id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    jti               varchar(255) NOT NULL UNIQUE,
    user_id           uuid         NOT NULL,
    created_at        timestamp    NOT NULL DEFAULT now(),
    expires_at        timestamp    NOT NULL,
    revoked           boolean      NOT NULL DEFAULT false,
    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id) REFERENCES public.users(user_id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_jti
    ON public.refresh_token (jti);

CREATE INDEX IF NOT EXISTS idx_refresh_token_user_id
    ON public.refresh_token (user_id);
