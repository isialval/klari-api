package com.isidora.klari_api.model.enums;

public enum RoutineType {
    DIA("Rutina de día"),
    NOCHE("Rutina de noche");

    private final String displayName;

    RoutineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
