package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeRelance;
import com.yooyob.erp.model.enums.StatutRelance;
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
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("planifications_relance")
public class PlanificationRelance {

    @PrimaryKey
    @Column("id_planification")
    private UUID idPlanification;

    @NotNull(message = "L'ID de configuration est obligatoire")
    @Column("id_configuration")
    private UUID idConfiguration;

    @Column("nom_configuration")
    private String nomConfiguration;

    @NotNull(message = "Le type de relance est obligatoire")
    @Column("type_relance")
    private TypeRelance typeRelance;

    @NotNull(message = "L'ID de la facture est obligatoire")
    @Column("id_facture")
    private UUID idFacture;

    @Column("numero_facture")
    private String numeroFacture;

    @Column("id_client")
    private UUID idClient;

    @Column("nom_client")
    private String nomClient;

    @Column("email_client")
    private String emailClient;

    @Column("telephone_client")
    private String telephoneClient;

    @Column("montant_facture")
    private BigDecimal montantFacture;

    @Column("montant_restant")
    private BigDecimal montantRestant;

    @Column("date_echeance")
    private LocalDateTime dateEcheance;

    @Column("jours_retard")
    private Integer joursRetard;

    @Column("statut")
    private StatutRelance statut;

    @Column("date_planifiee")
    private LocalDateTime datePlanifiee;

    @Column("date_envoi_prevue")
    private LocalDateTime dateEnvoiPrevue;

    @Column("date_envoi_reelle")
    private LocalDateTime dateEnvoiReelle;

    @Column("date_prochaine_tentative")
    private LocalDateTime dateProchaineTentative;

    @Column("numero_tentative")
    @Builder.Default
    private Integer numeroTentative = 1;

    @Column("numero_relance_sequence")
    @Builder.Default
    private Integer numeroRelanceSequence = 1;

    @Column("contenu_email")
    private String contenuEmail;

    @Column("objet_email")
    private String objetEmail;

    @Column("destinataires_email")
    private List<String> destinatairesEmail;

    @Column("copie_carbone")
    private List<String> copieCarbone;

    @Column("pieces_jointes")
    private List<String> piecesJointes;

    @Column("canal_envoi")
    private List<String> canalEnvoi; // EMAIL, SMS, COURRIER

    @Column("reponse_recue")
    @Builder.Default
    private Boolean reponseRecue = false;

    @Column("date_reponse")
    private LocalDateTime dateReponse;

    @Column("contenu_reponse")
    private String contenuReponse;

    @Column("erreurs_envoi")
    private List<String> erreursEnvoi;

    @Column("nombre_echecs")
    @Builder.Default
    private Integer nombreEchecs = 0;

    @Column("logs_execution")
    private List<String> logsExecution;

    @Column("metadata")
    private Map<String, String> metadata;

    @Column("frais_appliques")
    private BigDecimal fraisAppliques;

    @Column("escalade_effectuee")
    @Builder.Default
    private Boolean escaladeEffectuee = false;

    @Column("date_escalade")
    private LocalDateTime dateEscalade;

    @Column("escalade_vers")
    private List<String> escaladeVers;

    @Column("annulee_automatiquement")
    @Builder.Default
    private Boolean annuleeAutomatiquement = false;

    @Column("motif_annulation")
    private String motifAnnulation;

    @Column("priorite")
    @Builder.Default
    private Integer priorite = 1;

    @Column("tags")
    private List<String> tags;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}