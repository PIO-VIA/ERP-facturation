package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.TaxeCreateRequest;
import com.yooyob.erp.dto.request.TaxeUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.TaxeResponse;
import com.yooyob.erp.service.TaxeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/taxes")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Taxe", description = "API de gestion des taxes")
public class TaxeController {

    private final TaxeService taxeService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle taxe")
    public ResponseEntity<ApiResponse<TaxeResponse>> createTaxe(
            @Valid @RequestBody TaxeCreateRequest request) {
        log.info("Création d'une nouvelle taxe: {}", request.getNomTaxe());
        
        TaxeResponse response = taxeService.createTaxe(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Taxe créée avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une taxe existante")
    public ResponseEntity<ApiResponse<TaxeResponse>> updateTaxe(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id,
            @Valid @RequestBody TaxeUpdateRequest request) {
        log.info("Mise à jour de la taxe: {}", id);
        
        TaxeResponse response = taxeService.updateTaxe(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Taxe mise à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une taxe par son ID")
    public ResponseEntity<ApiResponse<TaxeResponse>> getTaxeById(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id) {
        
        TaxeResponse response = taxeService.getTaxeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/nom/{nomTaxe}")
    @Operation(summary = "Récupérer une taxe par son nom")
    public ResponseEntity<ApiResponse<TaxeResponse>> getTaxeByNom(
            @Parameter(description = "Nom de la taxe") @PathVariable @NotBlank String nomTaxe) {
        
        TaxeResponse response = taxeService.getTaxeByNom(nomTaxe);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les taxes avec pagination")
    public ResponseEntity<ApiResponse<Page<TaxeResponse>>> getAllTaxes(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<TaxeResponse> response = taxeService.getAllTaxes(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer toutes les taxes actives")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getAllActiveTaxes() {
        
        List<TaxeResponse> response = taxeService.getAllActiveTaxes();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/type/{typeTaxe}")
    @Operation(summary = "Récupérer les taxes par type")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesByType(
            @Parameter(description = "Type de taxe") @PathVariable @NotBlank String typeTaxe) {
        
        List<TaxeResponse> response = taxeService.getTaxesByType(typeTaxe);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/porte/{porteTaxe}")
    @Operation(summary = "Récupérer les taxes par porte de taxe")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesByPorte(
            @Parameter(description = "Porte de taxe") @PathVariable @NotBlank String porteTaxe) {
        
        List<TaxeResponse> response = taxeService.getTaxesByPorte(porteTaxe);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/position-fiscale/{positionFiscale}")
    @Operation(summary = "Récupérer les taxes par position fiscale")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesByPositionFiscale(
            @Parameter(description = "Position fiscale") @PathVariable @NotBlank String positionFiscale) {
        
        List<TaxeResponse> response = taxeService.getTaxesByPositionFiscale(positionFiscale);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active/type/{typeTaxe}")
    @Operation(summary = "Récupérer les taxes actives par type")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getActiveTaxesByType(
            @Parameter(description = "Type de taxe") @PathVariable @NotBlank String typeTaxe) {
        
        List<TaxeResponse> response = taxeService.getActiveTaxesByType(typeTaxe);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/taux-range")
    @Operation(summary = "Récupérer les taxes par plage de taux")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesByTauxRange(
            @Parameter(description = "Taux minimum") @RequestParam @Positive BigDecimal minTaux,
            @Parameter(description = "Taux maximum") @RequestParam @Positive BigDecimal maxTaux) {
        
        List<TaxeResponse> response = taxeService.getTaxesByTauxRange(minTaux, maxTaux);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/montant-range")
    @Operation(summary = "Récupérer les taxes par plage de montant")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesByMontantRange(
            @Parameter(description = "Montant minimum") @RequestParam @Positive BigDecimal minMontant,
            @Parameter(description = "Montant maximum") @RequestParam @Positive BigDecimal maxMontant) {
        
        List<TaxeResponse> response = taxeService.getTaxesByMontantRange(minMontant, maxMontant);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/désactiver une taxe")
    public ResponseEntity<ApiResponse<TaxeResponse>> toggleTaxeStatus(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id) {
        log.info("Changement du statut de la taxe: {}", id);
        
        TaxeResponse response = taxeService.toggleTaxeStatus(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Statut de la taxe modifié avec succès"));
    }

    @PatchMapping("/{id}/calcul-taxe")
    @Operation(summary = "Mettre à jour le taux de calcul d'une taxe")
    public ResponseEntity<ApiResponse<TaxeResponse>> updateCalculTaxe(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id,
            @Parameter(description = "Nouveau taux") @RequestParam @NotNull @Positive BigDecimal nouveauTaux) {
        log.info("Mise à jour du taux de calcul de la taxe {} à: {}", id, nouveauTaux);
        
        TaxeResponse response = taxeService.updateCalculTaxe(id, nouveauTaux);
        return ResponseEntity.ok(ApiResponse.success(response, "Taux de calcul mis à jour avec succès"));
    }

    @PatchMapping("/{id}/montant-taxe")
    @Operation(summary = "Mettre à jour le montant d'une taxe")
    public ResponseEntity<ApiResponse<TaxeResponse>> updateMontantTaxe(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id,
            @Parameter(description = "Nouveau montant") @RequestParam @NotNull @Positive BigDecimal nouveauMontant) {
        log.info("Mise à jour du montant de la taxe {} à: {}", id, nouveauMontant);
        
        TaxeResponse response = taxeService.updateMontantTaxe(id, nouveauMontant);
        return ResponseEntity.ok(ApiResponse.success(response, "Montant de la taxe mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une taxe (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteTaxe(
            @Parameter(description = "ID de la taxe") @PathVariable UUID id) {
        log.info("Suppression de la taxe: {}", id);
        
        taxeService.deleteTaxe(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Taxe supprimée avec succès"));
    }

    @GetMapping("/{taxeId}/calculer-montant-taxe")
    @Operation(summary = "Calculer le montant de taxe pour un montant HT donné")
    public ResponseEntity<ApiResponse<BigDecimal>> calculerMontantTaxe(
            @Parameter(description = "ID de la taxe") @PathVariable UUID taxeId,
            @Parameter(description = "Montant HT") @RequestParam @NotNull @Positive BigDecimal montantHT) {
        
        BigDecimal montantTaxe = taxeService.calculerMontantTaxe(taxeId, montantHT);
        return ResponseEntity.ok(ApiResponse.success(montantTaxe));
    }

    @GetMapping("/{taxeId}/calculer-montant-ttc")
    @Operation(summary = "Calculer le montant TTC à partir d'un montant HT et d'une taxe")
    public ResponseEntity<ApiResponse<BigDecimal>> calculerMontantTTC(
            @Parameter(description = "Montant HT") @RequestParam @NotNull @Positive BigDecimal montantHT,
            @Parameter(description = "ID de la taxe") @PathVariable UUID taxeId) {
        
        BigDecimal montantTTC = taxeService.calculerMontantTTC(montantHT, taxeId);
        return ResponseEntity.ok(ApiResponse.success(montantTTC));
    }

    @GetMapping("/{taxeId}/calculer-montant-ht")
    @Operation(summary = "Calculer le montant HT à partir d'un montant TTC et d'une taxe")
    public ResponseEntity<ApiResponse<BigDecimal>> calculerMontantHT(
            @Parameter(description = "Montant TTC") @RequestParam @NotNull @Positive BigDecimal montantTTC,
            @Parameter(description = "ID de la taxe") @PathVariable UUID taxeId) {
        
        BigDecimal montantHT = taxeService.calculerMontantHT(montantTTC, taxeId);
        return ResponseEntity.ok(ApiResponse.success(montantHT));
    }

    @PostMapping("/appliquer-multiples-taxes")
    @Operation(summary = "Appliquer plusieurs taxes à un montant")
    public ResponseEntity<ApiResponse<BigDecimal>> appliquerMultiplesTaxes(
            @Parameter(description = "Montant HT") @RequestParam @NotNull @Positive BigDecimal montantHT,
            @Parameter(description = "Liste des IDs de taxes") @RequestBody List<UUID> taxeIds) {
        
        BigDecimal montantAvecTaxes = taxeService.appliquerMultiplesTaxes(montantHT, taxeIds);
        return ResponseEntity.ok(ApiResponse.success(montantAvecTaxes));
    }

    @PostMapping("/calculer-repartition-taxes")
    @Operation(summary = "Calculer la répartition des taxes pour un montant donné")
    public ResponseEntity<ApiResponse<Map<UUID, BigDecimal>>> calculerRepartitionTaxes(
            @Parameter(description = "Montant HT") @RequestParam @NotNull @Positive BigDecimal montantHT,
            @Parameter(description = "Liste des IDs de taxes") @RequestBody List<UUID> taxeIds) {
        
        Map<UUID, BigDecimal> repartition = taxeService.calculerRepartitionTaxes(montantHT, taxeIds);
        return ResponseEntity.ok(ApiResponse.success(repartition));
    }

    @GetMapping("/defaut/type-produit/{typeProduit}")
    @Operation(summary = "Obtenir les taxes par défaut pour un type de produit")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getTaxesParDefaut(
            @Parameter(description = "Type de produit") @PathVariable @NotBlank String typeProduit) {
        
        List<TaxeResponse> response = taxeService.getTaxesParDefaut(typeProduit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/exists/nom/{nomTaxe}")
    @Operation(summary = "Vérifier si une taxe existe par son nom")
    public ResponseEntity<ApiResponse<Boolean>> existsByNom(
            @Parameter(description = "Nom de la taxe") @PathVariable @NotBlank String nomTaxe) {
        
        boolean exists = taxeService.existsByNom(nomTaxe);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de taxes actives")
    public ResponseEntity<ApiResponse<Long>> countActiveTaxes() {
        
        Long count = taxeService.countActiveTaxes();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/type/{typeTaxe}")
    @Operation(summary = "Compter le nombre de taxes par type")
    public ResponseEntity<ApiResponse<Long>> countTaxesByType(
            @Parameter(description = "Type de taxe") @PathVariable @NotBlank String typeTaxe) {
        
        Long count = taxeService.countTaxesByType(typeTaxe);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/types-disponibles")
    @Operation(summary = "Obtenir la liste des types de taxes disponibles")
    public ResponseEntity<ApiResponse<List<String>>> getAvailableTaxTypes() {
        
        List<String> types = taxeService.getAvailableTaxTypes();
        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/validate-tax-rate")
    @Operation(summary = "Valider qu'un taux de taxe est valide")
    public ResponseEntity<ApiResponse<Boolean>> isValidTaxRate(
            @Parameter(description = "Taux de taxe à valider") @RequestParam @NotNull BigDecimal taux) {
        
        boolean isValid = taxeService.isValidTaxRate(taux);
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }

    @GetMapping("/most-used")
    @Operation(summary = "Obtenir les taxes les plus utilisées")
    public ResponseEntity<ApiResponse<List<TaxeResponse>>> getMostUsedTaxes(
            @Parameter(description = "Nombre de taxes à retourner") @RequestParam(defaultValue = "10") @Positive int limit) {
        
        List<TaxeResponse> response = taxeService.getMostUsedTaxes(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}