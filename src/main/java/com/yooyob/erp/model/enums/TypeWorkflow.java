package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeWorkflow {
    APPROBATION_FACTURE("Approbation de facture"),
    APPROBATION_DEVIS("Approbation de devis"),
    APPROBATION_AVOIR("Approbation d'avoir"),
    APPROBATION_PAIEMENT("Approbation de paiement"),
    APPROBATION_REMBOURSEMENT("Approbation de remboursement"),
    VALIDATION_MONTANT("Validation de montant élevé"),
    VALIDATION_CLIENT("Validation nouveau client"),
    VALIDATION_REMISE("Validation remise importante");

    private final String libelle;

    TypeWorkflow(String libelle) {
        this.libelle = libelle;
    }
}