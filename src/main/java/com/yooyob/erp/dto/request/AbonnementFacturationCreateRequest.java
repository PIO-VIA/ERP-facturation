package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AbonnementFacturationCreateRequest {

    @NotBlank(message = "Le nom de l'abonnement est obligatoire")
    private String nomAbonnement;

    private String description;

    @NotNull(message = "L'ID client est obligatoire")
    private UUID idClient;

    private StatutAbonnement statut;

    @NotNull(message = "La fréquence de récurrence est obligatoire")
    private FrequenceRecurrence frequenceRecurrence;

    private Integer jourFacturation;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotNull(message = "Le montant récurrent est obligatoire")
    @PositiveOrZero(message = "Le montant récurrent doit être positif ou nul")
    private BigDecimal montantRecurrent;

    @Valid
    private List<LigneFactureCreateRequest> lignesTemplate;

    private String devise;

    private BigDecimal tauxChange;

    private String conditionsPaiement;

    private String notesTemplate;

    private Integer nombreMaxFactures;

    @Builder.Default
    private Boolean autoEnvoyerEmail = false;

    @Builder.Default
    private Boolean autoGenererPdf = true;

    private Integer joursAvantRappel;

    private String templateEmailPersonnalise;

    @Builder.Default
    private Boolean actif = true;
}