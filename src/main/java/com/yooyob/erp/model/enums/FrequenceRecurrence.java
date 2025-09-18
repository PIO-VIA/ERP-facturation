package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum FrequenceRecurrence {
    MENSUELLE("Mensuelle", 1),
    TRIMESTRIELLE("Trimestrielle", 3),
    SEMESTRIELLE("Semestrielle", 6),
    ANNUELLE("Annuelle", 12),
    HEBDOMADAIRE("Hebdomadaire", 0), // cas spécial
    QUOTIDIENNE("Quotidienne", 0); // cas spécial

    private final String libelle;
    private final int mois; // nombre de mois entre les occurrences (0 pour cas spéciaux)

    FrequenceRecurrence(String libelle, int mois) {
        this.libelle = libelle;
        this.mois = mois;
    }
}