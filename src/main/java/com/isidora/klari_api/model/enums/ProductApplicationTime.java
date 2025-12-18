package com.isidora.klari_api.model.enums;

public enum ProductApplicationTime {
    DIA("Día"),
    NOCHE("Noche"),
    AMBOS("Día y noche");

    private final String displayName;

    ProductApplicationTime(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}