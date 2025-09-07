package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.PaiementCreateRequest;
import com.yooyob.erp.dto.request.PaiementUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.mapper.PaiementMapper;
import com.yooyob.erp.model.entity.Paiement;
import com.yooyob.erp.model.enums.TypePaiement;
import com.yooyob.erp.repository.PaiementRepository;
import com.yooyob.erp.service.PaiementService;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/paiements")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Paiement", description = "API de gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;
    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;

    @PostMapping
    @Operation(summary = "Créer un nouveau paiement")
    public ResponseEntity<ApiResponse<PaiementResponse>> createPaiement(
            @Valid @RequestBody PaiementCreateRequest request) {
        log.info("Création d'un nouveau paiement pour le client: {}", request.getIdClient());
        
        PaiementResponse response = paiementService.createPaiement(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Paiement créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un paiement existant")
    public ResponseEntity<ApiResponse<PaiementResponse>> updatePaiement(
            @Parameter(description = "ID du paiement") @PathVariable UUID id,
            @Valid @RequestBody PaiementUpdateRequest request) {
        log.info("Mise à jour du paiement: {}", id);
        
        PaiementResponse response = paiementService.updatePaiement(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Paiement mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un paiement par son ID")
    public ResponseEntity<ApiResponse<PaiementResponse>> getPaiementById(
            @Parameter(description = "ID du paiement") @PathVariable UUID id) {
        
        PaiementResponse response = paiementService.getPaiementById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les paiements avec pagination")
    public ResponseEntity<ApiResponse<Page<PaiementResponse>>> getAllPaiements(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<PaiementResponse> response = paiementService.getAllPaiements(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les paiements d'un client")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        List<Paiement> paiements = paiementRepository.findByIdClient(clientId);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Récupérer les paiements d'une facture")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId) {
        
        List<Paiement> paiements = paiementRepository.findByIdFacture(factureId);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/mode-paiement/{modePaiement}")
    @Operation(summary = "Récupérer les paiements par mode de paiement")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByModePaiement(
            @Parameter(description = "Mode de paiement") @PathVariable @NotNull TypePaiement modePaiement) {
        
        List<Paiement> paiements = paiementRepository.findByModePaiement(modePaiement);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/journal/{journal}")
    @Operation(summary = "Récupérer les paiements par journal")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByJournal(
            @Parameter(description = "Journal") @PathVariable @NotBlank String journal) {
        
        List<Paiement> paiements = paiementRepository.findByJournal(journal);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les paiements par période")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Paiement> paiements = paiementRepository.findByDateBetween(startDate, endDate);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/montant")
    @Operation(summary = "Récupérer les paiements par montant")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByMontant(
            @Parameter(description = "Montant minimum") @RequestParam @Positive BigDecimal minAmount,
            @Parameter(description = "Montant maximum") @RequestParam @Positive BigDecimal maxAmount) {
        
        List<Paiement> paiements = paiementRepository.findByMontantBetween(minAmount, maxAmount);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/client/{clientId}/periode")
    @Operation(summary = "Récupérer les paiements d'un client par période")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByClientAndPeriode(
            @Parameter(description = "ID du client") @PathVariable UUID clientId,
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Paiement> paiements = paiementRepository.findByClientAndDateBetween(clientId, startDate, endDate);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/facture/{factureId}/ordered")
    @Operation(summary = "Récupérer les paiements d'une facture triés par date")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByFactureOrderByDate(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId) {
        
        List<Paiement> paiements = paiementRepository.findByFactureOrderByDateDesc(factureId);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/mode-paiement/{modePaiement}/periode")
    @Operation(summary = "Récupérer les paiements par mode et période")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getPaiementsByModePaiementAndPeriode(
            @Parameter(description = "Mode de paiement") @PathVariable @NotNull TypePaiement modePaiement,
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Paiement> paiements = paiementRepository.findByModePaiementAndDateBetween(modePaiement, startDate, endDate);
        List<PaiementResponse> response = paiementMapper.toResponseList(paiements);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un paiement")
    public ResponseEntity<ApiResponse<Void>> deletePaiement(
            @Parameter(description = "ID du paiement") @PathVariable UUID id) {
        log.info("Suppression du paiement: {}", id);
        
        paiementService.deletePaiement(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Paiement supprimé avec succès"));
    }

    @GetMapping("/client/{clientId}/total")
    @Operation(summary = "Calculer le montant total des paiements d'un client")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaiementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        BigDecimal total = paiementService.getTotalPaiementsByClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/facture/{factureId}/total")
    @Operation(summary = "Calculer le montant total des paiements d'une facture")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaiementsByFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId) {
        
        BigDecimal total = paiementService.getTotalPaiementsByFacture(factureId);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/periode/total")
    @Operation(summary = "Calculer le montant total des paiements par période")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPaiementsByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal total = paiementService.getTotalPaiementsByPeriode(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(total));
    }

    @GetMapping("/client/{clientId}/count")
    @Operation(summary = "Compter les paiements d'un client")
    public ResponseEntity<ApiResponse<Long>> countPaiementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        Long count = paiementService.countPaiementsByClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/mode-paiement/{modePaiement}/count")
    @Operation(summary = "Compter les paiements par mode de paiement")
    public ResponseEntity<ApiResponse<Long>> countPaiementsByModePaiement(
            @Parameter(description = "Mode de paiement") @PathVariable @NotNull TypePaiement modePaiement) {
        
        Long count = paiementService.countPaiementsByModePaiement(modePaiement);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/periode/count")
    @Operation(summary = "Compter les paiements par période")
    public ResponseEntity<ApiResponse<Long>> countPaiementsByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        Long count = paiementService.countPaiementsByPeriode(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/facture/{factureId}/can-pay")
    @Operation(summary = "Valider qu'un paiement peut être effectué pour une facture")
    public ResponseEntity<ApiResponse<Boolean>> canPayFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId,
            @Parameter(description = "Montant du paiement") @RequestParam @Positive BigDecimal montantPaiement) {
        
        boolean canPay = paiementService.canPayFacture(factureId, montantPaiement);
        return ResponseEntity.ok(ApiResponse.success(canPay));
    }

    @PostMapping("/facture/{factureId}")
    @Operation(summary = "Traiter un paiement de facture (met à jour la facture et crée le paiement)")
    public ResponseEntity<ApiResponse<PaiementResponse>> traiterPaiementFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId,
            @Valid @RequestBody PaiementCreateRequest request) {
        log.info("Traitement du paiement pour la facture: {}", factureId);
        
        PaiementResponse response = paiementService.traiterPaiementFacture(factureId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Paiement traité avec succès"));
    }
}