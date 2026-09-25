CREATE TABLE IF NOT EXISTS public.user_invitation (
    invitation_id   uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    email           varchar(250) NOT NULL,
    token           varchar(255) NOT NULL UNIQUE,
    status          varchar(20)  NOT NULL DEFAULT 'PENDING',
    created_at      timestamp    NOT NULL DEFAULT now(),
    expires_at      timestamp    NOT NULL,
    completed_at    timestamp,
    created_by      uuid         NOT NULL,
    first_name      varchar(128),
    last_name       varchar(128),
    username        varchar(128),
    password_hash   varchar(256),
    CONSTRAINT fk_invitation_created_by
        FOREIGN KEY (created_by) REFERENCES public.users(user_id),
    CONSTRAINT chk_invitation_status
        CHECK (status IN ('PENDING','COMPLETED','APPROVED','REJECTED'))
);