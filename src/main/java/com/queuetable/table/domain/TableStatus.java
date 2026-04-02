package com.queuetable.table.domain;

import java.util.Map;
import java.util.Set;

public enum TableStatus {
    FREE,
    OCCUPIED,
    CLEANING;

    private static final Map<TableStatus, Set<TableStatus>> VALID_TRANSITIONS = Map.of(
            FREE, Set.of(OCCUPIED),
            OCCUPIED, Set.of(CLEANING, FREE),
            CLEANING, Set.of(FREE)
    );

    public boolean canTransitionTo(TableStatus target) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
}
