package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.TypeWorkflow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowApprobationResponse {

    private UUID idWorkflow;
    private TypeWorkflow typeWorkflow;
    private String nomWorkflow;
    private String description;
    private BigDecimal montantSeuilMin;
    private BigDecimal montantSeuilMax;
    private List<EtapeApprobationResponse> etapesApprobation;
    private String conditionsDeclenchement;
    private BigDecimal autoApprouverSiMontantInferieur;
    private Integer delaiExpirationHeures;
    private Boolean escaladeAutomatique;
    private Integer delaiEscaladeHeures;
    private Boolean notificationEmail;
    private String templateEmail;
    private Boolean actif;
    private Integer ordrePriorite;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}