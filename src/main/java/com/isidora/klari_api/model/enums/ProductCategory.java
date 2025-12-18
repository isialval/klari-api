package com.isidora.klari_api.model.enums;

public enum ProductCategory {
    LIMPIADOR("Limpiador"),
    TONICO("Tónico"),
    HIDRATANTE("Hidratante"),
    SERUM("Serum"),
    PROTECTOR_SOLAR("Protector solar"),
    EXFOLIANTE("Exfoliante"),
    MASCARILLA("Mascarilla"),
    CONTORNO_OJOS("Contorno de ojos"),
    ACEITE("Aceite"),
    OTRO("Otro");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
