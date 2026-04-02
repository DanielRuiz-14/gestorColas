CREATE TABLE restaurants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255) NOT NULL,
    slug            VARCHAR(100) NOT NULL UNIQUE,
    address         VARCHAR(500) NOT NULL,
    phone           VARCHAR(50),
    description     TEXT,
    opening_hours   JSONB NOT NULL DEFAULT '{}',
    qr_code_url     VARCHAR(500),
    is_active       BOOLEAN NOT NULL DEFAULT true,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_restaurants_slug ON restaurants (slug);
CREATE INDEX idx_restaurants_is_active ON restaurants (is_active);
