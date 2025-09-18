package com.yooyob.erp.model.enums;

import lombok.Getter;

@Getter
public enum TypeEcheance {
    UNIQUE("Échéance unique"),
    MULTIPLE("Échéances multiples"),
    PROGRESSIVE("Échéance progressive"),
    CONDITIONNELLE("Échéance conditionnelle"),
    RECURRENTE("Échéance récurrente"),
    ANTICIPEE("Échéance anticipée"),
    DIFFEREE("Échéance différée");

    private final String libelle;

    TypeEcheance(String libelle) {
        this.libelle = libelle;
    }
}