package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.TypeWorkflow;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandeApprobationCreateRequest {

    @NotNull(message = "L'ID du workflow est obligatoire")
    private UUID idWorkflow;

    @NotNull(message = "Le type de workflow est obligatoire")
    private TypeWorkflow typeWorkflow;

    private String titreDemande;

    private String description;

    @NotNull(message = "L'ID de l'objet est obligatoire")
    private UUID idObjet;

    private String typeObjet;

    private Map<String, Object> donneesObjet;

    private BigDecimal montantConcerne;

    @NotNull(message = "Le demandeur est obligatoire")
    private UUID demandeur;

    private String commentairesDemandeur;

    @Builder.Default
    private Integer priorite = 1;

    private List<String> tags;

    private Map<String, String> metadata;
}