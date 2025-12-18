package com.isidora.klari_api.model.enums;

public enum SkinType {
    GRASA("Piel grasa"),
    MIXTA("Piel mixta"),
    SECA("Piel seca"),
    NORMAL("Piel normal"),
    SENSIBLE("Piel sensible");

    private final String displayName;

    SkinType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
