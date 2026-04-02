CREATE TABLE restaurant_configs (
    id                                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id                           UUID NOT NULL UNIQUE REFERENCES restaurants(id) ON DELETE CASCADE,
    confirmation_timeout_minutes            INTEGER NOT NULL DEFAULT 5,
    noshow_grace_minutes                    INTEGER NOT NULL DEFAULT 15,
    avg_table_duration_minutes              INTEGER NOT NULL DEFAULT 45,
    reservation_protection_window_minutes   INTEGER NOT NULL DEFAULT 30,
    max_queue_size                          INTEGER
);

CREATE INDEX idx_restaurant_configs_restaurant_id ON restaurant_configs (restaurant_id);
