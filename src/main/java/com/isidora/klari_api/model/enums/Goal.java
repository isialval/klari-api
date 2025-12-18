package com.isidora.klari_api.model.enums;

public enum Goal {
    MANCHAS("Eliminar manchas"),
    TEXTURA("Mejorar textura"),
    IRRITACION("Reducir irritación"),
    LINEAS_EXPRESION("Disminuir líneas de expresión"),
    POROS("Minimizar poros"),
    HIDRATACION("Aumentar hidratación");

    private final String displayName;

    Goal(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
