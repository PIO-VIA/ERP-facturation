package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.TypeWorkflow;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowApprobationCreateRequest {

    @NotNull(message = "Le type de workflow est obligatoire")
    private TypeWorkflow typeWorkflow;

    @NotBlank(message = "Le nom du workflow est obligatoire")
    private String nomWorkflow;

    private String description;

    private BigDecimal montantSeuilMin;

    private BigDecimal montantSeuilMax;

    @Valid
    private List<EtapeApprobationCreateRequest> etapesApprobation;

    private String conditionsDeclenchement;

    private BigDecimal autoApprouverSiMontantInferieur;

    @Builder.Default
    private Integer delaiExpirationHeures = 72;

    @Builder.Default
    private Boolean escaladeAutomatique = true;

    @Builder.Default
    private Integer delaiEscaladeHeures = 24;

    @Builder.Default
    private Boolean notificationEmail = true;

    private String templateEmail;

    @Builder.Default
    private Boolean actif = true;

    @Builder.Default
    private Integer ordrePriorite = 1;
}