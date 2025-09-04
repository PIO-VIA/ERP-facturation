package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.TypeClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FournisseurUpdateRequest {

    private String username;
    private String categorie;
    private String siteWeb;
    private Boolean nTva;
    private String adresse;
    private String telephone;

    @Email(message = "Format d'email invalide")
    private String email;

    private TypeClient typeFournisseur;
    private String raisonSociale;
    private String numeroTva;
    private String codeFournisseur;
    private Double limiteCredit;
    private Boolean actif;
}