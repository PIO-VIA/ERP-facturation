package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeNumerotation {
    FACTURE("Facture"),
    DEVIS("Devis"),
    AVOIR("Avoir"),
    PAIEMENT("Paiement"),
    REMBOURSEMENT("Remboursement"),
    COMMANDE("Commande"),
    BON_LIVRAISON("Bon de livraison"),
    CLIENT("Client"),
    FOURNISSEUR("Fournisseur"),
    PRODUIT("Produit"),
    ABONNEMENT("Abonnement"),
    CONTRAT("Contrat"),
    PROJET("Projet"),
    TICKET("Ticket"),
    PERSONNALISE("Personnalisé");

    private final String libelle;

    TypeNumerotation(String libelle) {
        this.libelle = libelle;
    }
}