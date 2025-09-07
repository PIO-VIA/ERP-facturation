package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.BanqueCreateRequest;
import com.yooyob.erp.dto.request.BanqueUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.BanqueResponse;
import com.yooyob.erp.mapper.BanqueMapper;
import com.yooyob.erp.model.entity.Banque;
import com.yooyob.erp.repository.BanqueRepository;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.util.ValidationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/banques")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Banque", description = "API de gestion des banques")
public class BanqueController {

    private final BanqueRepository banqueRepository;
    private final BanqueMapper banqueMapper;

    @PostMapping
    @Operation(summary = "Créer une nouvelle banque")
    public ResponseEntity<ApiResponse<BanqueResponse>> createBanque(
            @Valid @RequestBody BanqueCreateRequest request) {
        log.info("Création d'une nouvelle banque: {}", request.getBanque());
        
        if (banqueRepository.findByBanque(request.getBanque()).size() > 0) {
            throw new ValidationException("Une banque avec ce nom existe déjà");
        }
        
        Banque banque = banqueMapper.toEntity(request);

        
        Banque savedBanque = banqueRepository.save(banque);
        BanqueResponse response = banqueMapper.toResponse(savedBanque);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Banque créée avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une banque existante")
    public ResponseEntity<ApiResponse<BanqueResponse>> updateBanque(
            @Parameter(description = "ID de la banque") @PathVariable UUID id,
            @Valid @RequestBody BanqueUpdateRequest request) {
        log.info("Mise à jour de la banque: {}", id);
        
        Banque existingBanque = banqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banque", id));
                
        banqueMapper.updateEntityFromRequest(request, existingBanque);

        
        Banque savedBanque = banqueRepository.save(existingBanque);
        BanqueResponse response = banqueMapper.toResponse(savedBanque);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Banque mise à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une banque par son ID")
    public ResponseEntity<ApiResponse<BanqueResponse>> getBanqueById(
            @Parameter(description = "ID de la banque") @PathVariable UUID id) {
        
        Banque banque = banqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banque", id));
                
        BanqueResponse response = banqueMapper.toResponse(banque);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer toutes les banques avec pagination")
    public ResponseEntity<ApiResponse<Page<BanqueResponse>>> getAllBanques(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Banque> banquesPage = banqueRepository.findAll(pageable);
        List<BanqueResponse> responses = banqueMapper.toResponseList(banquesPage.getContent());
        
        Page<BanqueResponse> responsePage = new PageImpl<>(responses, pageable, banquesPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer toutes les banques actives")
    public ResponseEntity<ApiResponse<List<BanqueResponse>>> getAllActiveBanques() {
        
        List<Banque> banques = banqueRepository.findAll();
        List<BanqueResponse> responses = banqueMapper.toResponseList(banques);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des banques par nom")
    public ResponseEntity<ApiResponse<List<BanqueResponse>>> searchBanquesByNom(
            @Parameter(description = "Nom de la banque à rechercher") @RequestParam @NotBlank String nomBanque) {
        
        List<Banque> banques = banqueRepository.findByBanqueContaining(nomBanque);
        List<BanqueResponse> responses = banqueMapper.toResponseList(banques);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une banque (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteBanque(
            @Parameter(description = "ID de la banque") @PathVariable UUID id) {
        log.info("Suppression de la banque: {}", id);
        
        Banque banque = banqueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Banque", id));
                
        banqueRepository.delete(banque);
        banqueRepository.save(banque);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Banque supprimée avec succès"));
    }

    @GetMapping("/exists/nom/{nomBanque}")
    @Operation(summary = "Vérifier si une banque existe par son nom")
    public ResponseEntity<ApiResponse<Boolean>> existsByNom(
            @Parameter(description = "Nom de la banque") @PathVariable @NotBlank String nomBanque) {
        
        boolean exists = banqueRepository.findByBanque(nomBanque).size() > 0;
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de banques actives")
    public ResponseEntity<ApiResponse<Long>> countActiveBanques() {
        
        Long count = (long) banqueRepository.findAll().size();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}