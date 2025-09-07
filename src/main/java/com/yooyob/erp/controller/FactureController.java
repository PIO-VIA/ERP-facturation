package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.request.FactureUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.FactureResponse;
import com.yooyob.erp.dto.response.FactureDetailsResponse;
import com.yooyob.erp.model.enums.StatutFacture;
import com.yooyob.erp.service.FactureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/factures")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Facture", description = "API de gestion des factures")
public class FactureController {

    private final FactureService factureService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle facture")
    public ResponseEntity<ApiResponse<FactureResponse>> createFacture(
            @Valid @RequestBody FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture de: {}", request.getIdClient());
        
        FactureResponse response = factureService.createFacture(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Facture créée avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une facture existante")
    public ResponseEntity<ApiResponse<FactureResponse>> updateFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id,
            @Valid @RequestBody FactureUpdateRequest request) {
        log.info("Mise à jour de la facture: {}", id);
        
        FactureResponse response = factureService.updateFacture(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Facture mise à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une facture par son ID")
    public ResponseEntity<ApiResponse<FactureResponse>> getFactureById(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        
        FactureResponse response = factureService.getFactureById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/details")
    @Operation(summary = "Récupérer une facture avec tous les détails")
    public ResponseEntity<ApiResponse<FactureDetailsResponse>> getFactureDetails(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        
        FactureDetailsResponse response = factureService.getFactureDetails(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/numero/{numeroFacture}")
    @Operation(summary = "Récupérer une facture par son numéro")
    public ResponseEntity<ApiResponse<FactureResponse>> getFactureByNumero(
            @Parameter(description = "Numéro de la facture") @PathVariable @NotBlank String numeroFacture) {
        
        FactureResponse response = factureService.getFactureByNumero(numeroFacture);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les factures avec pagination")
    public ResponseEntity<ApiResponse<Page<FactureResponse>>> getAllFactures(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<FactureResponse> response = factureService.getAllFactures(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les factures d'un client")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        List<FactureResponse> response = factureService.getFacturesByClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/etat/{etat}")
    @Operation(summary = "Récupérer les factures par statut")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByEtat(
            @Parameter(description = "Statut de la facture") @PathVariable @NotNull StatutFacture etat) {
        
        List<FactureResponse> response = factureService.getFacturesByEtat(etat);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/client/{clientId}/etat/{etat}")
    @Operation(summary = "Récupérer les factures d'un client avec un statut donné")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByClientAndEtat(
            @Parameter(description = "ID du client") @PathVariable UUID clientId,
            @Parameter(description = "Statut de la facture") @PathVariable @NotNull StatutFacture etat) {
        
        List<FactureResponse> response = factureService.getFacturesByClientAndEtat(clientId, etat);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les factures par période")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<FactureResponse> response = factureService.getFacturesByPeriode(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/montant")
    @Operation(summary = "Récupérer les factures par montant")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByMontant(
            @Parameter(description = "Montant minimum") @RequestParam @Positive BigDecimal minAmount,
            @Parameter(description = "Montant maximum") @RequestParam @Positive BigDecimal maxAmount) {
        
        List<FactureResponse> response = factureService.getFacturesByMontant(minAmount, maxAmount);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/retard")
    @Operation(summary = "Récupérer les factures en retard")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesEnRetard() {
        
        List<FactureResponse> response = factureService.getFacturesEnRetard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/impayes")
    @Operation(summary = "Récupérer les factures impayées")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesImpayes() {
        
        List<FactureResponse> response = factureService.getFacturesImpayes();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/devise/{devise}")
    @Operation(summary = "Récupérer les factures par devise")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesByDevise(
            @Parameter(description = "Code devise") @PathVariable @NotBlank String devise) {
        
        List<FactureResponse> response = factureService.getFacturesByDevise(devise);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/envoyees-email/{envoyees}")
    @Operation(summary = "Récupérer les factures envoyées par email")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesEnvoyeesParEmail(
            @Parameter(description = "Statut d'envoi par email") @PathVariable @NotNull Boolean envoyees) {
        
        List<FactureResponse> response = factureService.getFacturesEnvoyeesParEmail(envoyees);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des factures avec filtres multiples")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> searchFactures(
            @Parameter(description = "ID du client") @RequestParam(required = false) UUID clientId,
            @Parameter(description = "Statut de la facture") @RequestParam(required = false) StatutFacture etat,
            @Parameter(description = "Date de début") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @Parameter(description = "Date de fin") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            @Parameter(description = "Montant minimum") @RequestParam(required = false) BigDecimal montantMin,
            @Parameter(description = "Montant maximum") @RequestParam(required = false) BigDecimal montantMax,
            @Parameter(description = "Code devise") @RequestParam(required = false) String devise) {
        
        List<FactureResponse> response = factureService.searchFactures(
                clientId, etat, dateStart, dateEnd, montantMin, montantMax, devise);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/paginated")
    @Operation(summary = "Recherche paginée des factures avec filtres")
    public ResponseEntity<ApiResponse<Page<FactureResponse>>> searchFacturesWithPagination(
            @Parameter(description = "ID du client") @RequestParam(required = false) UUID clientId,
            @Parameter(description = "Statut de la facture") @RequestParam(required = false) StatutFacture etat,
            @Parameter(description = "Date de début") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateStart,
            @Parameter(description = "Date de fin") @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateEnd,
            @Parameter(description = "Montant minimum") @RequestParam(required = false) BigDecimal montantMin,
            @Parameter(description = "Montant maximum") @RequestParam(required = false) BigDecimal montantMax,
            @Parameter(description = "Code devise") @RequestParam(required = false) String devise,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<FactureResponse> response = factureService.searchFacturesWithPagination(
                clientId, etat, dateStart, dateEnd, montantMin, montantMax, devise, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'une facture")
    public ResponseEntity<ApiResponse<FactureResponse>> changeStatutFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id,
            @Parameter(description = "Nouveau statut") @RequestParam @NotNull StatutFacture nouveauStatut) {
        log.info("Changement du statut de la facture {} à: {}", id, nouveauStatut);
        
        FactureResponse response = factureService.changeStatutFacture(id, nouveauStatut);
        return ResponseEntity.ok(ApiResponse.success(response, "Statut de la facture modifié avec succès"));
    }

    @PatchMapping("/{id}/marquer-envoyee")
    @Operation(summary = "Marquer une facture comme envoyée par email")
    public ResponseEntity<ApiResponse<FactureResponse>> marquerEnvoyeeParEmail(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Marquage de la facture {} comme envoyée par email", id);
        
        FactureResponse response = factureService.marquerEnvoyeeParEmail(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Facture marquée comme envoyée"));
    }

    @PatchMapping("/{id}/calculer-montants")
    @Operation(summary = "Calculer et mettre à jour les montants d'une facture")
    public ResponseEntity<ApiResponse<FactureResponse>> calculerMontantsFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Calcul des montants de la facture: {}", id);
        
        FactureResponse response = factureService.calculerMontantsFacture(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Montants calculés avec succès"));
    }

    @PatchMapping("/{id}/montant-restant")
    @Operation(summary = "Mettre à jour le montant restant après un paiement")
    public ResponseEntity<ApiResponse<FactureResponse>> updateMontantRestant(
            @Parameter(description = "ID de la facture") @PathVariable UUID id,
            @Parameter(description = "Montant payé") @RequestParam @Positive BigDecimal montantPaye) {
        log.info("Mise à jour du montant restant de la facture {} après paiement de: {}", id, montantPaye);
        
        FactureResponse response = factureService.updateMontantRestant(id, montantPaye);
        return ResponseEntity.ok(ApiResponse.success(response, "Montant restant mis à jour"));
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Générer le PDF d'une facture")
    public ResponseEntity<ApiResponse<String>> genererPdfFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Génération du PDF de la facture: {}", id);
        
        String pdfPath = factureService.genererPdfFacture(id);
        return ResponseEntity.ok(ApiResponse.success(pdfPath, "PDF généré avec succès"));
    }

    @PostMapping("/{id}/envoyer-email")
    @Operation(summary = "Envoyer une facture par email")
    public ResponseEntity<ApiResponse<Void>> envoyerFactureParEmail(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Envoi de la facture {} par email", id);
        
        factureService.envoyerFactureParEmail(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Facture envoyée par email"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une facture (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Suppression de la facture: {}", id);
        
        factureService.deleteFacture(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Facture supprimée avec succès"));
    }

    @PostMapping("/{id}/dupliquer")
    @Operation(summary = "Dupliquer une facture")
    public ResponseEntity<ApiResponse<FactureResponse>> dupliquerFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID id) {
        log.info("Duplication de la facture: {}", id);
        
        FactureResponse response = factureService.dupliquerFacture(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Facture dupliquée avec succès"));
    }

    @GetMapping("/exists/numero/{numeroFacture}")
    @Operation(summary = "Vérifier si une facture existe par son numéro")
    public ResponseEntity<ApiResponse<Boolean>> existsByNumero(
            @Parameter(description = "Numéro de la facture") @PathVariable @NotBlank String numeroFacture) {
        
        boolean exists = factureService.existsByNumero(numeroFacture);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/etat/{etat}")
    @Operation(summary = "Compter les factures par statut")
    public ResponseEntity<ApiResponse<Long>> countFacturesByEtat(
            @Parameter(description = "Statut de la facture") @PathVariable @NotNull StatutFacture etat) {
        
        Long count = factureService.countFacturesByEtat(etat);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/client/{clientId}")
    @Operation(summary = "Compter les factures d'un client")
    public ResponseEntity<ApiResponse<Long>> countFacturesByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        Long count = factureService.countFacturesByClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/statistiques")
    @Operation(summary = "Obtenir des statistiques sur les factures")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFactureStatistics(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Map<String, Object> statistics = factureService.getFactureStatistics(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/chiffre-affaires/{year}")
    @Operation(summary = "Obtenir le chiffre d'affaires par mois")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getChiffreAffairesByMonth(
            @Parameter(description = "Année") @PathVariable @Positive int year) {
        
        Map<String, BigDecimal> chiffreAffaires = factureService.getChiffreAffairesByMonth(year);
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/top-clients")
    @Operation(summary = "Obtenir les top clients par chiffre d'affaires")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopClientsByChiffreAffaires(
            @Parameter(description = "Nombre de clients à retourner") @RequestParam(defaultValue = "10") @Positive int limit) {
        
        List<Map<String, Object>> topClients = factureService.getTopClientsByChiffreAffaires(limit);
        return ResponseEntity.ok(ApiResponse.success(topClients));
    }

    @GetMapping("/approchant-echeance")
    @Operation(summary = "Obtenir les factures approchant de l'échéance")
    public ResponseEntity<ApiResponse<List<FactureResponse>>> getFacturesApprochantEcheance(
            @Parameter(description = "Nombre de jours avant échéance") @RequestParam(defaultValue = "7") @Positive int nombreJours) {
        
        List<FactureResponse> factures = factureService.getFacturesApprochantEcheance(nombreJours);
        return ResponseEntity.ok(ApiResponse.success(factures));
    }
}