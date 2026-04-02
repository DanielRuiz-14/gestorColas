CREATE TABLE staff_users (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    role            VARCHAR(20) NOT NULL DEFAULT 'STAFF',
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_staff_role CHECK (role IN ('ADMIN', 'STAFF'))
);

CREATE INDEX idx_staff_users_restaurant_id ON staff_users (restaurant_id);
CREATE INDEX idx_staff_users_email ON staff_users (email);
