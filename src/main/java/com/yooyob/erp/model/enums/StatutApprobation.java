package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum StatutApprobation {
    EN_ATTENTE("En attente"),
    APPROUVE("Approuvé"),
    REJETE("Rejeté"),
    ANNULE("Annulé"),
    EXPIRE("Expiré"),
    EN_COURS("En cours"),
    DELEGUE("Délégué");

    private final String libelle;

    StatutApprobation(String libelle) {
        this.libelle = libelle;
    }
}