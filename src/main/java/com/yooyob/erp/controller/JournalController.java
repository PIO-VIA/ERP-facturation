package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.JournalCreateRequest;
import com.yooyob.erp.dto.request.JournalUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.JournalResponse;
import com.yooyob.erp.mapper.JournalMapper;
import com.yooyob.erp.model.entity.Journal;
import com.yooyob.erp.repository.JournalRepository;
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
@RequestMapping("/api/v1/journals")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Journal", description = "API de gestion des journaux comptables")
public class JournalController {

    private final JournalRepository journalRepository;
    private final JournalMapper journalMapper;

    @PostMapping
    @Operation(summary = "Créer un nouveau journal")
    public ResponseEntity<ApiResponse<JournalResponse>> createJournal(
            @Valid @RequestBody JournalCreateRequest request) {
        log.info("Création d'un nouveau journal: {}", request.getNomJournal());
        
        if (journalRepository.existsByNomJournal(request.getNomJournal())) {
            throw new ValidationException("Un journal avec ce nom existe déjà");
        }
        
        Journal journal = journalMapper.toEntity(request);
        journal.setCreatedAt(LocalDateTime.now());
        journal.setUpdatedAt(LocalDateTime.now());
        
        Journal savedJournal = journalRepository.save(journal);
        JournalResponse response = journalMapper.toResponse(savedJournal);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Journal créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un journal existant")
    public ResponseEntity<ApiResponse<JournalResponse>> updateJournal(
            @Parameter(description = "ID du journal") @PathVariable UUID id,
            @Valid @RequestBody JournalUpdateRequest request) {
        log.info("Mise à jour du journal: {}", id);
        
        Journal existingJournal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
                
        journalMapper.updateEntityFromRequest(request, existingJournal);
        existingJournal.setUpdatedAt(LocalDateTime.now());
        
        Journal savedJournal = journalRepository.save(existingJournal);
        JournalResponse response = journalMapper.toResponse(savedJournal);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Journal mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un journal par son ID")
    public ResponseEntity<ApiResponse<JournalResponse>> getJournalById(
            @Parameter(description = "ID du journal") @PathVariable UUID id) {
        
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
                
        JournalResponse response = journalMapper.toResponse(journal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/nom/{nomJournal}")
    @Operation(summary = "Récupérer un journal par son nom")
    public ResponseEntity<ApiResponse<JournalResponse>> getJournalByNom(
            @Parameter(description = "Nom du journal") @PathVariable @NotBlank String nomJournal) {
        
        Journal journal = journalRepository.findByNomJournal(nomJournal)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", "nomJournal", nomJournal));
                
        JournalResponse response = journalMapper.toResponse(journal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les journaux avec pagination")
    public ResponseEntity<ApiResponse<Page<JournalResponse>>> getAllJournals(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Journal> journalsPage = journalRepository.findAll(pageable);
        List<JournalResponse> responses = journalMapper.toResponseList(journalsPage.getContent());
        
        Page<JournalResponse> responsePage = new PageImpl<>(responses, pageable, journalsPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les journaux actifs")
    public ResponseEntity<ApiResponse<List<JournalResponse>>> getAllActiveJournals() {
        
        List<Journal> journals = journalRepository.findAll();
        List<JournalResponse> responses = journalMapper.toResponseList(journals);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/type/{typeJournal}")
    @Operation(summary = "Récupérer les journaux par type")
    public ResponseEntity<ApiResponse<List<JournalResponse>>> getJournalsByType(
            @Parameter(description = "Type de journal") @PathVariable @NotBlank String typeJournal) {
        
        List<Journal> journals = journalRepository.findByType(typeJournal);
        List<JournalResponse> responses = journalMapper.toResponseList(journals);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }





    @GetMapping("/search/nom")
    @Operation(summary = "Rechercher des journaux par nom")
    public ResponseEntity<ApiResponse<List<JournalResponse>>> searchJournalsByNom(
            @Parameter(description = "Nom du journal à rechercher") @RequestParam @NotBlank String nomJournal) {
        
        List<Journal> journals = journalRepository.findByNomJournalContaining(nomJournal);
        List<JournalResponse> responses = journalMapper.toResponseList(journals);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un journal (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteJournal(
            @Parameter(description = "ID du journal") @PathVariable UUID id) {
        log.info("Suppression du journal: {}", id);
        
        Journal journal = journalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Journal", id));
                
        journalRepository.delete(journal);
        journal.setUpdatedAt(LocalDateTime.now());
        journalRepository.save(journal);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Journal supprimé avec succès"));
    }

    @GetMapping("/exists/nom/{nomJournal}")
    @Operation(summary = "Vérifier si un journal existe par son nom")
    public ResponseEntity<ApiResponse<Boolean>> existsByNom(
            @Parameter(description = "Nom du journal") @PathVariable @NotBlank String nomJournal) {
        
        boolean exists = journalRepository.existsByNomJournal(nomJournal);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de journaux actifs")
    public ResponseEntity<ApiResponse<Long>> countActiveJournals() {
        
        Long count = (long) journalRepository.findAll().size();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/type/{typeJournal}")
    @Operation(summary = "Compter le nombre de journaux par type")
    public ResponseEntity<ApiResponse<Long>> countJournalsByType(
            @Parameter(description = "Type de journal") @PathVariable @NotBlank String typeJournal) {
        
        Long count = journalRepository.countByType(typeJournal);
        return ResponseEntity.ok(ApiResponse.success(count));
    }


}