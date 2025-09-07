package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.RemboursementCreateRequest;
import com.yooyob.erp.dto.request.RemboursementUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.RemboursementResponse;
import com.yooyob.erp.mapper.RemboursementMapper;
import com.yooyob.erp.model.entity.Remboursement;
import com.yooyob.erp.repository.RemboursementRepository;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/remboursements")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Remboursement", description = "API de gestion des remboursements")
public class RemboursementController {

    private final RemboursementRepository remboursementRepository;
    private final RemboursementMapper remboursementMapper;

    @PostMapping
    @Operation(summary = "Créer un nouveau remboursement")
    public ResponseEntity<ApiResponse<RemboursementResponse>> createRemboursement(
            @Valid @RequestBody RemboursementCreateRequest request) {
        log.info("Création d'un nouveau remboursement pour le client: {}", request.getIdClient());
        
        Remboursement remboursement = remboursementMapper.toEntity(request);
        remboursement.setCreatedAt(LocalDateTime.now());
        remboursement.setUpdatedAt(LocalDateTime.now());
        
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(savedRemboursement);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Remboursement créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un remboursement existant")
    public ResponseEntity<ApiResponse<RemboursementResponse>> updateRemboursement(
            @Parameter(description = "ID du remboursement") @PathVariable UUID id,
            @Valid @RequestBody RemboursementUpdateRequest request) {
        log.info("Mise à jour du remboursement: {}", id);
        
        Remboursement existingRemboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", id));
                
        remboursementMapper.updateEntityFromRequest(request, existingRemboursement);
        existingRemboursement.setUpdatedAt(LocalDateTime.now());
        
        Remboursement savedRemboursement = remboursementRepository.save(existingRemboursement);
        RemboursementResponse response = remboursementMapper.toResponse(savedRemboursement);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Remboursement mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un remboursement par son ID")
    public ResponseEntity<ApiResponse<RemboursementResponse>> getRemboursementById(
            @Parameter(description = "ID du remboursement") @PathVariable UUID id) {
        
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", id));
                
        RemboursementResponse response = remboursementMapper.toResponse(remboursement);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les remboursements avec pagination")
    public ResponseEntity<ApiResponse<Page<RemboursementResponse>>> getAllRemboursements(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Remboursement> remboursementsPage = remboursementRepository.findAll(pageable);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursementsPage.getContent());
        
        Page<RemboursementResponse> responsePage = new PageImpl<>(responses, pageable, remboursementsPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Récupérer les remboursements d'un client")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        List<Remboursement> remboursements = remboursementRepository.findByIdClient(clientId);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/facture/{factureId}")
    @Operation(summary = "Récupérer les remboursements d'une facture")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId) {
        
        List<Remboursement> remboursements = remboursementRepository.findByIdFacture(factureId);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Récupérer les remboursements par statut")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByStatut(
            @Parameter(description = "Statut du remboursement") @PathVariable @NotBlank String statut) {
        
        List<Remboursement> remboursements = remboursementRepository.findByStatut(statut);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/motif/{motif}")
    @Operation(summary = "Récupérer les remboursements par motif")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByMotif(
            @Parameter(description = "Motif du remboursement") @PathVariable @NotBlank String motif) {
        
        List<Remboursement> remboursements = remboursementRepository.findAll().stream().filter(r -> r.getMotif() != null && r.getMotif().equals(motif)).toList();
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/periode")
    @Operation(summary = "Récupérer les remboursements par période")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Remboursement> remboursements = remboursementRepository.findByDateEcheanceBetween(startDate, endDate);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/montant")
    @Operation(summary = "Récupérer les remboursements par montant")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByMontant(
            @Parameter(description = "Montant minimum") @RequestParam @Positive BigDecimal minMontant,
            @Parameter(description = "Montant maximum") @RequestParam @Positive BigDecimal maxMontant) {
        
        List<Remboursement> remboursements = remboursementRepository.findByMontantBetween(minMontant, maxMontant);
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/client/{clientId}/periode")
    @Operation(summary = "Récupérer les remboursements d'un client par période")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsByClientAndPeriode(
            @Parameter(description = "ID du client") @PathVariable UUID clientId,
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Remboursement> remboursements = remboursementRepository.findByClientAndStatut(clientId, "EN_ATTENTE");
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/en-attente")
    @Operation(summary = "Récupérer les remboursements en attente")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsEnAttente() {
        
        List<Remboursement> remboursements = remboursementRepository.findByStatut("EN_ATTENTE");
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/traites")
    @Operation(summary = "Récupérer les remboursements traités")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getRemboursementsTraites() {
        
        List<Remboursement> remboursements = remboursementRepository.findByStatut("TRAITE");
        List<RemboursementResponse> responses = remboursementMapper.toResponseList(remboursements);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un remboursement")
    public ResponseEntity<ApiResponse<RemboursementResponse>> changeStatutRemboursement(
            @Parameter(description = "ID du remboursement") @PathVariable UUID id,
            @Parameter(description = "Nouveau statut") @RequestParam @NotBlank String nouveauStatut) {
        log.info("Changement du statut du remboursement {} à: {}", id, nouveauStatut);
        
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", id));
                
        remboursement.setStatut(nouveauStatut);
        remboursement.setUpdatedAt(LocalDateTime.now());
        
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(savedRemboursement);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Statut du remboursement modifié avec succès"));
    }

    @PatchMapping("/{id}/traiter")
    @Operation(summary = "Marquer un remboursement comme traité")
    public ResponseEntity<ApiResponse<RemboursementResponse>> traiterRemboursement(
            @Parameter(description = "ID du remboursement") @PathVariable UUID id) {
        log.info("Traitement du remboursement: {}", id);
        
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", id));
                
        if (!"EN_ATTENTE".equals(remboursement.getStatut())) {
            throw new ValidationException("Ce remboursement ne peut pas être traité car il n'est pas en attente");
        }
        
        remboursement.setStatut("TRAITE");
        remboursement.setUpdatedAt(LocalDateTime.now());
        
        Remboursement savedRemboursement = remboursementRepository.save(remboursement);
        RemboursementResponse response = remboursementMapper.toResponse(savedRemboursement);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Remboursement traité avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un remboursement")
    public ResponseEntity<ApiResponse<Void>> deleteRemboursement(
            @Parameter(description = "ID du remboursement") @PathVariable UUID id) {
        log.info("Suppression du remboursement: {}", id);
        
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remboursement", id));
                
        if ("TRAITE".equals(remboursement.getStatut())) {
            throw new ValidationException("Impossible de supprimer un remboursement déjà traité");
        }
        
        remboursementRepository.delete(remboursement);
        return ResponseEntity.ok(ApiResponse.success(null, "Remboursement supprimé avec succès"));
    }

    @GetMapping("/client/{clientId}/total")
    @Operation(summary = "Calculer le montant total des remboursements d'un client")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRemboursementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        BigDecimal total = remboursementRepository.findByIdClient(clientId).stream().map(Remboursement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(ApiResponse.success(total != null ? total : BigDecimal.ZERO));
    }

    @GetMapping("/facture/{factureId}/total")
    @Operation(summary = "Calculer le montant total des remboursements d'une facture")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRemboursementsByFacture(
            @Parameter(description = "ID de la facture") @PathVariable UUID factureId) {
        
        BigDecimal total = remboursementRepository.findByIdFacture(factureId).stream().map(Remboursement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(ApiResponse.success(total != null ? total : BigDecimal.ZERO));
    }

    @GetMapping("/periode/total")
    @Operation(summary = "Calculer le montant total des remboursements par période")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRemboursementsByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal total = remboursementRepository.findByDateEcheanceBetween(startDate, endDate).stream().map(Remboursement::getMontant).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(ApiResponse.success(total != null ? total : BigDecimal.ZERO));
    }

    @GetMapping("/count/client/{clientId}")
    @Operation(summary = "Compter les remboursements d'un client")
    public ResponseEntity<ApiResponse<BigDecimal>> countRemboursementsByClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {

        BigDecimal count = remboursementRepository.sumMontantByClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/statut/{statut}")
    @Operation(summary = "Compter les remboursements par statut")
    public ResponseEntity<ApiResponse<Long>> countRemboursementsByStatut(
            @Parameter(description = "Statut du remboursement") @PathVariable @NotBlank String statut) {
        
        Long count = remboursementRepository.countByStatut(statut);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/motif/{motif}")
    @Operation(summary = "Compter les remboursements par motif")
    public ResponseEntity<ApiResponse<Long>> countRemboursementsByMotif(
            @Parameter(description = "Motif du remboursement") @PathVariable @NotBlank String motif) {
        
        Long count = (long) remboursementRepository.findAll().stream().filter(r -> r.getMotif() != null && r.getMotif().equals(motif)).count();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}