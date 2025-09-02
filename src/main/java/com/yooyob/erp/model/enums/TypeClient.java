package com.yooyob.erp.model.enums;

public enum TypeClient {
    PARTICULIER("Particulier"),
    ENTREPRISE("Entreprise"),
    ADMINISTRATION("Administration");

    private final String libelle;

    TypeClient(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
