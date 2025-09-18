package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum StatutDevis {
    BROUILLON("Brouillon"),
    ENVOYE("Envoyé"),
    ACCEPTE("Accepté"),
    REFUSE("Refusé"),
    EXPIRE("Expiré"),
    ANNULE("Annulé"),
    CONVERTI_EN_FACTURE("Converti en facture");

    private final String libelle;

    StatutDevis(String libelle) {
        this.libelle = libelle;
    }
}