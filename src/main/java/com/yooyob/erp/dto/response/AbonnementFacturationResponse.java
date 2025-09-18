package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbonnementFacturationResponse {

    private UUID idAbonnement;
    private String nomAbonnement;
    private String description;
    private UUID idClient;
    private String nomClient;
    private String emailClient;
    private StatutAbonnement statut;
    private FrequenceRecurrence frequenceRecurrence;
    private Integer jourFacturation;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateProchaineFacturation;
    private LocalDate dateDerniereFacturation;
    private BigDecimal montantRecurrent;
    private List<LigneFactureResponse> lignesTemplate;
    private String devise;
    private BigDecimal tauxChange;
    private String conditionsPaiement;
    private String notesTemplate;
    private Integer nombreFacturesGenerees;
    private Integer nombreMaxFactures;
    private BigDecimal montantTotalFacture;
    private Boolean autoEnvoyerEmail;
    private Boolean autoGenererPdf;
    private Integer joursAvantRappel;
    private String templateEmailPersonnalise;
    private Boolean actif;
    private String derniereErreur;
    private LocalDateTime dateDerniereErreur;
    private UUID createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}