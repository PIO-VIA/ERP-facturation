package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutRelance;
import com.yooyob.erp.model.enums.TypeRelance;
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
public class PlanificationRelanceResponse {
    private UUID idPlanification;
    private UUID idConfiguration;
    private String nomConfiguration;
    private TypeRelance typeRelance;
    private UUID idFacture;
    private String numeroFacture;
    private UUID idClient;
    private String nomClient;
    private String emailClient;
    private BigDecimal montantFacture;
    private BigDecimal montantRestant;
    private LocalDateTime dateEcheance;
    private Integer joursRetard;
    private StatutRelance statut;
    private LocalDateTime datePlanifiee;
    private LocalDateTime dateEnvoiPrevue;
    private LocalDateTime dateEnvoiReelle;
    private Integer numeroTentative;
    private Integer numeroRelanceSequence;
    private String contenuEmail;
    private String objetEmail;
    private List<String> destinatairesEmail;
    private List<String> canalEnvoi;
    private Boolean reponseRecue;
    private LocalDateTime dateReponse;
    private String contenuReponse;
    private List<String> erreursEnvoi;
    private Integer nombreEchecs;
    private BigDecimal fraisAppliques;
    private Boolean escaladeEffectuee;
    private LocalDateTime dateEscalade;
    private Boolean annuleeAutomatiquement;
    private String motifAnnulation;
    private Integer priorite;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}