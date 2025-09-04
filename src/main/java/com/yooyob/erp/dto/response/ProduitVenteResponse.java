package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitVenteResponse {

    private UUID idProduit;
    private String nomProduit;
    private String typeProduit;
    private BigDecimal prixVente;
    private BigDecimal cout;
    private String categorie;
    private String reference;
    private String codeBarre;
    private String photo;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}