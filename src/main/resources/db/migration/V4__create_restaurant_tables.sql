CREATE TABLE restaurant_tables (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id   UUID NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
    label           VARCHAR(100) NOT NULL,
    capacity        INTEGER NOT NULL,
    status          VARCHAR(20) NOT NULL DEFAULT 'FREE',
    zone            VARCHAR(100),
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    version         INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_table_status CHECK (status IN ('FREE', 'OCCUPIED', 'CLEANING')),
    CONSTRAINT chk_table_capacity CHECK (capacity > 0),
    CONSTRAINT uq_table_label_restaurant UNIQUE (restaurant_id, label)
);

CREATE INDEX idx_restaurant_tables_restaurant_id ON restaurant_tables (restaurant_id);
CREATE INDEX idx_restaurant_tables_status ON restaurant_tables (restaurant_id, status);
