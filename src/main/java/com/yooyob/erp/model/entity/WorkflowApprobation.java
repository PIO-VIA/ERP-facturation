package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeWorkflow;
import com.yooyob.erp.model.enums.StatutApprobation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("workflows_approbation")
public class WorkflowApprobation {

    @PrimaryKey
    @Column("id_workflow")
    private UUID idWorkflow;

    @NotNull(message = "Le type de workflow est obligatoire")
    @Column("type_workflow")
    private TypeWorkflow typeWorkflow;

    @Column("nom_workflow")
    private String nomWorkflow;

    @Column("description")
    private String description;

    @Column("montant_seuil_min")
    private BigDecimal montantSeuilMin;

    @Column("montant_seuil_max")
    private BigDecimal montantSeuilMax;

    @Column("etapes_approbation")
    private List<EtapeApprobation> etapesApprobation;

    @Column("conditions_declenchement")
    private String conditionsDeclenchement;

    @Column("auto_approuver_si_montant_inferieur")
    private BigDecimal autoApprouverSiMontantInferieur;

    @Column("delai_expiration_heures")
    @Builder.Default
    private Integer delaiExpirationHeures = 72;

    @Column("escalade_automatique")
    @Builder.Default
    private Boolean escaladeAutomatique = true;

    @Column("delai_escalade_heures")
    @Builder.Default
    private Integer delaiEscaladeHeures = 24;

    @Column("notification_email")
    @Builder.Default
    private Boolean notificationEmail = true;

    @Column("template_email")
    private String templateEmail;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("ordre_priorite")
    @Builder.Default
    private Integer ordrePriorite = 1;

    @Column("created_by")
    private UUID createdBy;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}