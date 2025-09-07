package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.DeviseCreateRequest;
import com.yooyob.erp.dto.request.DeviseUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.DeviseResponse;
import com.yooyob.erp.service.DeviseService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devises")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Devise", description = "API de gestion des devises")
public class DeviseController {

    private final DeviseService deviseService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle devise")
    public ResponseEntity<ApiResponse<DeviseResponse>> createDevise(
            @Valid @RequestBody DeviseCreateRequest request) {
        log.info("Création d'une nouvelle devise: {}", request.getNomDevise());
        
        DeviseResponse response = deviseService.createDevise(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Devise créée avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une devise existante")
    public ResponseEntity<ApiResponse<DeviseResponse>> updateDevise(
            @Parameter(description = "ID de la devise") @PathVariable UUID id,
            @Valid @RequestBody DeviseUpdateRequest request) {
        log.info("Mise à jour de la devise: {}", id);
        
        DeviseResponse response = deviseService.updateDevise(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Devise mise à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une devise par son ID")
    public ResponseEntity<ApiResponse<DeviseResponse>> getDeviseById(
            @Parameter(description = "ID de la devise") @PathVariable UUID id) {
        
        DeviseResponse response = deviseService.getDeviseById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/nom/{nomDevise}")
    @Operation(summary = "Récupérer une devise par son nom")
    public ResponseEntity<ApiResponse<DeviseResponse>> getDeviseByNom(
            @Parameter(description = "Nom de la devise") @PathVariable @NotBlank String nomDevise) {
        
        DeviseResponse response = deviseService.getDeviseByNom(nomDevise);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/symbole/{symbole}")
    @Operation(summary = "Récupérer une devise par son symbole")
    public ResponseEntity<ApiResponse<DeviseResponse>> getDeviseBySymbole(
            @Parameter(description = "Symbole de la devise") @PathVariable @NotBlank String symbole) {
        
        DeviseResponse response = deviseService.getDeviseBySymbole(symbole);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les devises avec pagination")
    public ResponseEntity<ApiResponse<Page<DeviseResponse>>> getAllDevises(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<DeviseResponse> response = deviseService.getAllDevises(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer toutes les devises actives")
    public ResponseEntity<ApiResponse<List<DeviseResponse>>> getAllActiveDevises() {
        
        List<DeviseResponse> response = deviseService.getAllActiveDevises();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des devises par nom")
    public ResponseEntity<ApiResponse<List<DeviseResponse>>> searchDevisesByNom(
            @Parameter(description = "Nom de la devise à rechercher") @RequestParam @NotBlank String nomDevise) {
        
        List<DeviseResponse> response = deviseService.searchDevisesByNom(nomDevise);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/désactiver une devise")
    public ResponseEntity<ApiResponse<DeviseResponse>> toggleDeviseStatus(
            @Parameter(description = "ID de la devise") @PathVariable UUID id) {
        log.info("Changement du statut de la devise: {}", id);
        
        DeviseResponse response = deviseService.toggleDeviseStatus(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Statut de la devise modifié avec succès"));
    }

    @PatchMapping("/{id}/facteur-conversion")
    @Operation(summary = "Mettre à jour le facteur de conversion d'une devise")
    public ResponseEntity<ApiResponse<DeviseResponse>> updateFacteurConversion(
            @Parameter(description = "ID de la devise") @PathVariable UUID id,
            @Parameter(description = "Nouveau facteur de conversion") @RequestParam @NotNull @Positive BigDecimal nouveauFacteur) {
        log.info("Mise à jour du facteur de conversion de la devise {} à: {}", id, nouveauFacteur);
        
        DeviseResponse response = deviseService.updateFacteurConversion(id, nouveauFacteur);
        return ResponseEntity.ok(ApiResponse.success(response, "Facteur de conversion mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une devise (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteDevise(
            @Parameter(description = "ID de la devise") @PathVariable UUID id) {
        log.info("Suppression de la devise: {}", id);
        
        deviseService.deleteDevise(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Devise supprimée avec succès"));
    }

    @GetMapping("/convert")
    @Operation(summary = "Convertir un montant d'une devise à une autre")
    public ResponseEntity<ApiResponse<BigDecimal>> convertAmount(
            @Parameter(description = "Montant à convertir") @RequestParam @NotNull @Positive BigDecimal montant,
            @Parameter(description = "Devise source") @RequestParam @NotBlank String deviseSource,
            @Parameter(description = "Devise de destination") @RequestParam @NotBlank String deviseTarget) {
        
        BigDecimal convertedAmount = deviseService.convertAmount(montant, deviseSource, deviseTarget);
        return ResponseEntity.ok(ApiResponse.success(convertedAmount));
    }

    @GetMapping("/convert/to-base")
    @Operation(summary = "Convertir un montant d'une devise vers la devise de base")
    public ResponseEntity<ApiResponse<BigDecimal>> convertToBaseCurrency(
            @Parameter(description = "Montant à convertir") @RequestParam @NotNull @Positive BigDecimal montant,
            @Parameter(description = "Devise source") @RequestParam @NotBlank String deviseSource) {
        
        BigDecimal convertedAmount = deviseService.convertToBaseCurrency(montant, deviseSource);
        return ResponseEntity.ok(ApiResponse.success(convertedAmount));
    }

    @GetMapping("/convert/from-base")
    @Operation(summary = "Convertir un montant de la devise de base vers une autre devise")
    public ResponseEntity<ApiResponse<BigDecimal>> convertFromBaseCurrency(
            @Parameter(description = "Montant à convertir") @RequestParam @NotNull @Positive BigDecimal montant,
            @Parameter(description = "Devise de destination") @RequestParam @NotBlank String deviseTarget) {
        
        BigDecimal convertedAmount = deviseService.convertFromBaseCurrency(montant, deviseTarget);
        return ResponseEntity.ok(ApiResponse.success(convertedAmount));
    }

    @GetMapping("/default")
    @Operation(summary = "Obtenir la devise par défaut du système")
    public ResponseEntity<ApiResponse<DeviseResponse>> getDefaultDevise() {
        
        DeviseResponse response = deviseService.getDefaultDevise();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/set-default")
    @Operation(summary = "Définir une devise comme devise par défaut")
    public ResponseEntity<ApiResponse<DeviseResponse>> setDefaultDevise(
            @Parameter(description = "ID de la devise") @PathVariable UUID id) {
        log.info("Définition de la devise {} comme devise par défaut", id);
        
        DeviseResponse response = deviseService.setDefaultDevise(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Devise définie comme devise par défaut"));
    }

    @GetMapping("/exists/nom/{nomDevise}")
    @Operation(summary = "Vérifier si une devise existe par son nom")
    public ResponseEntity<ApiResponse<Boolean>> existsByNom(
            @Parameter(description = "Nom de la devise") @PathVariable @NotBlank String nomDevise) {
        
        boolean exists = deviseService.existsByNom(nomDevise);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/symbole/{symbole}")
    @Operation(summary = "Vérifier si une devise existe par son symbole")
    public ResponseEntity<ApiResponse<Boolean>> existsBySymbole(
            @Parameter(description = "Symbole de la devise") @PathVariable @NotBlank String symbole) {
        
        boolean exists = deviseService.existsBySymbole(symbole);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de devises actives")
    public ResponseEntity<ApiResponse<Long>> countActiveDevises() {
        
        Long count = deviseService.countActiveDevises();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/most-used")
    @Operation(summary = "Obtenir la liste des devises les plus utilisées")
    public ResponseEntity<ApiResponse<List<DeviseResponse>>> getMostUsedDevises(
            @Parameter(description = "Nombre de devises à retourner") @RequestParam(defaultValue = "10") @Positive int limit) {
        
        List<DeviseResponse> response = deviseService.getMostUsedDevises(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/update-exchange-rates")
    @Operation(summary = "Mettre à jour les taux de change depuis une source externe")
    public ResponseEntity<ApiResponse<Void>> updateExchangeRatesFromExternalSource() {
        log.info("Mise à jour des taux de change depuis une source externe");
        
        deviseService.updateExchangeRatesFromExternalSource();
        return ResponseEntity.ok(ApiResponse.success(null, "Taux de change mis à jour avec succès"));
    }
}