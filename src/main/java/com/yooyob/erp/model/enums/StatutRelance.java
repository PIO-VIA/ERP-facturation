package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum StatutRelance {
    PLANIFIEE("Planifiée"),
    EN_ATTENTE("En attente d'envoi"),
    ENVOYEE("Envoyée"),
    ECHEC_ENVOI("Échec d'envoi"),
    ANNULEE("Annulée"),
    REPONDUE("Réponse reçue"),
    IGNOREE("Ignorée"),
    OBSOLETE("Obsolète");

    private final String libelle;

    StatutRelance(String libelle) {
        this.libelle = libelle;
    }
}