package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeRelance {
    RAPPEL_ECHEANCE("Rappel d'échéance"),
    PREMIERE_RELANCE("Première relance"),
    DEUXIEME_RELANCE("Deuxième relance"),
    TROISIEME_RELANCE("Troisième relance"),
    MISE_EN_DEMEURE("Mise en demeure"),
    RELANCE_PERSONNALISEE("Relance personnalisée"),
    RELANCE_DEVIS("Relance de devis"),
    RELANCE_PAIEMENT_PARTIEL("Relance paiement partiel");

    private final String libelle;

    TypeRelance(String libelle) {
        this.libelle = libelle;
    }
}