CREATE TABLE queue_entries (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id           UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    table_id                UUID REFERENCES restaurant_tables(id) ON DELETE SET NULL,
    customer_name           VARCHAR(200) NOT NULL,
    customer_phone          VARCHAR(50),
    party_size              INTEGER NOT NULL,
    access_token            UUID NOT NULL DEFAULT gen_random_uuid(),
    position                INTEGER NOT NULL,
    status                  VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    estimated_wait_minutes  INTEGER,
    notified_at             TIMESTAMP WITH TIME ZONE,
    is_walk_in              BOOLEAN NOT NULL DEFAULT false,
    created_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at              TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version                 INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_queue_status CHECK (status IN ('WAITING', 'NOTIFIED', 'SEATED', 'CANCELLED', 'EXPIRED')),
    CONSTRAINT chk_party_size CHECK (party_size > 0),
    CONSTRAINT chk_position CHECK (position > 0)
);

CREATE UNIQUE INDEX idx_queue_entries_access_token ON queue_entries (access_token);
CREATE INDEX idx_queue_entries_restaurant_status ON queue_entries (restaurant_id, status);
CREATE INDEX idx_queue_entries_restaurant_position ON queue_entries (restaurant_id, position) WHERE status = 'WAITING';
