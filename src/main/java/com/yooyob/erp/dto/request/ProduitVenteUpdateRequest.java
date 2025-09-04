package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitVenteUpdateRequest {

    private String nomProduit;
    private String typeProduit;

    @PositiveOrZero(message = "Le prix de vente doit être positif ou nul")
    private BigDecimal prixVente;

    private BigDecimal cout;
    private String categorie;
    private String reference;
    private String codeBarre;
    private String photo;
    private Boolean active;
}