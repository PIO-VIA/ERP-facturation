package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum StatutAvoir {
    BROUILLON("Brouillon"),
    VALIDE("Validé"),
    APPLIQUE("Appliqué"),
    REMBOURSE("Remboursé"),
    ANNULE("Annulé");

    private final String libelle;

    StatutAvoir(String libelle) {
        this.libelle = libelle;
    }
}