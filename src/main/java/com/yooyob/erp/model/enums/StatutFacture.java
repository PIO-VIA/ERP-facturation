package com.yooyob.erp.model.enums;

public enum StatutFacture {
    BROUILLON("Brouillon"),
    ENVOYE("Envoyé"),
    PAYE("Payé"),
    PARTIELLEMENT_PAYE("Partiellement payé"),
    EN_RETARD("En retard"),
    ANNULE("Annulé");

    private final String libelle;

    StatutFacture(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}