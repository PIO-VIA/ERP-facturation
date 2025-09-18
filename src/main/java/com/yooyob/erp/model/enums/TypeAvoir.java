package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeAvoir {
    REMBOURSEMENT("Remboursement"),
    RETOUR_MARCHANDISE("Retour de marchandise"),
    ERREUR_FACTURATION("Erreur de facturation"),
    REDUCTION_COMMERCIALE("Réduction commerciale"),
    ANNULATION_PARTIELLE("Annulation partielle"),
    AUTRE("Autre");

    private final String libelle;

    TypeAvoir(String libelle) {
        this.libelle = libelle;
    }
}