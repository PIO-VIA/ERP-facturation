package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeEscompte {
    PAIEMENT_ANTICIPE("Escompte paiement anticipé"),
    VOLUME("Escompte de volume"),
    FIDELITE("Escompte fidélité"),
    COMMERCIAL("Escompte commercial"),
    SAISONNIER("Escompte saisonnier"),
    PROMOTION("Escompte promotionnel"),
    LIQUIDATION("Escompte de liquidation"),
    NOUVEAU_CLIENT("Escompte nouveau client"),
    PERSONNALISE("Escompte personnalisé");

    private final String libelle;

    TypeEscompte(String libelle) {
        this.libelle = libelle;
    }
}