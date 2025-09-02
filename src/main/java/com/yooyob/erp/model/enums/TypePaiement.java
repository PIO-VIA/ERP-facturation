package com.yooyob.erp.model.enums;

public enum TypePaiement {
    ESPECES("Espèces"),
    CHEQUE("Chèque"),
    VIREMENT("Virement"),
    CARTE_BANCAIRE("Carte bancaire"),
    AUTRE("Autre");

    private final String libelle;

    TypePaiement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}