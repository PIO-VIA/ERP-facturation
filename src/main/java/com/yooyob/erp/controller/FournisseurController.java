package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.FournisseurCreateRequest;
import com.yooyob.erp.dto.request.FournisseurUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.FournisseurResponse;
import com.yooyob.erp.mapper.FournisseurMapper;
import com.yooyob.erp.model.entity.Fournisseur;
import com.yooyob.erp.repository.FournisseurRepository;
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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fournisseurs")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Fournisseur", description = "API de gestion des fournisseurs")
public class FournisseurController {

    private final FournisseurRepository fournisseurRepository;
    private final FournisseurMapper fournisseurMapper;

    @PostMapping
    @Operation(summary = "Créer un nouveau fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> createFournisseur(
            @Valid @RequestBody FournisseurCreateRequest request) {
        log.info("Création d'un nouveau fournisseur: {}", request.getUsername());
        
        if (request.getEmail() != null && fournisseurRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Un fournisseur avec cet email existe déjà");
        }
        
        if (request.getCodeFournisseur() != null && fournisseurRepository.existsByCodeFournisseur(request.getCodeFournisseur())) {
            throw new ValidationException("Un fournisseur avec ce code existe déjà");
        }
        
        Fournisseur fournisseur = fournisseurMapper.toEntity(request);
        fournisseur.setCreatedAt(LocalDateTime.now());
        fournisseur.setUpdatedAt(LocalDateTime.now());
        
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Fournisseur créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un fournisseur existant")
    public ResponseEntity<ApiResponse<FournisseurResponse>> updateFournisseur(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID id,
            @Valid @RequestBody FournisseurUpdateRequest request) {
        log.info("Mise à jour du fournisseur: {}", id);
        
        Fournisseur existingFournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
                
        fournisseurMapper.updateEntityFromRequest(request, existingFournisseur);
        existingFournisseur.setUpdatedAt(LocalDateTime.now());
        
        Fournisseur savedFournisseur = fournisseurRepository.save(existingFournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Fournisseur mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un fournisseur par son ID")
    public ResponseEntity<ApiResponse<FournisseurResponse>> getFournisseurById(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID id) {
        
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
                
        FournisseurResponse response = fournisseurMapper.toResponse(fournisseur);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un fournisseur par son email")
    public ResponseEntity<ApiResponse<FournisseurResponse>> getFournisseurByEmail(
            @Parameter(description = "Email du fournisseur") @PathVariable @Email String email) {
        
        Fournisseur fournisseur = fournisseurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "email", email));
                
        FournisseurResponse response = fournisseurMapper.toResponse(fournisseur);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/code/{codeFournisseur}")
    @Operation(summary = "Récupérer un fournisseur par son code")
    public ResponseEntity<ApiResponse<FournisseurResponse>> getFournisseurByCode(
            @Parameter(description = "Code fournisseur") @PathVariable @NotBlank String codeFournisseur) {
        
        Fournisseur fournisseur = fournisseurRepository.findByCodeFournisseur(codeFournisseur)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", "codeFournisseur", codeFournisseur));
                
        FournisseurResponse response = fournisseurMapper.toResponse(fournisseur);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les fournisseurs avec pagination")
    public ResponseEntity<ApiResponse<Page<FournisseurResponse>>> getAllFournisseurs(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<Fournisseur> fournisseursPage = fournisseurRepository.findAll(pageable);
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseursPage.getContent());
        
        Page<FournisseurResponse> responsePage = new PageImpl<>(responses, pageable, fournisseursPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les fournisseurs actifs")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getAllActiveFournisseurs() {
        
        List<Fournisseur> fournisseurs = fournisseurRepository.findByActif(true);
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseurs);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les fournisseurs par catégorie")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getFournisseursByCategorie(
            @Parameter(description = "Catégorie") @PathVariable @NotBlank String categorie) {
        
        List<Fournisseur> fournisseurs = fournisseurRepository.findByCategorie(categorie);
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseurs);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/search/nom")
    @Operation(summary = "Rechercher des fournisseurs par nom")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> searchFournisseursByNom(
            @Parameter(description = "Nom du fournisseur à rechercher") @RequestParam @NotBlank String nomFournisseur) {
        
        List<Fournisseur> fournisseurs = fournisseurRepository.findByUsernameContaining(nomFournisseur);
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseurs);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/solde-superieur/{solde}")
    @Operation(summary = "Récupérer les fournisseurs avec un solde supérieur à un montant donné")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getFournisseursWithSoldeGreaterThan(
            @Parameter(description = "Montant minimum du solde") @PathVariable @NotNull @Positive BigDecimal solde) {
        
        List<Fournisseur> fournisseurs = fournisseurRepository.findFournisseursWithSoldeGreaterThan(solde.doubleValue());
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseurs);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/solde-negatif")
    @Operation(summary = "Récupérer les fournisseurs avec un solde négatif")
    public ResponseEntity<ApiResponse<List<FournisseurResponse>>> getFournisseursWithNegativeBalance() {
        
        List<Fournisseur> fournisseurs = fournisseurRepository.findAll().stream().filter(f -> f.getSoldeCourant() != null && f.getSoldeCourant() < 0).toList();
        List<FournisseurResponse> responses = fournisseurMapper.toResponseList(fournisseurs);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/désactiver un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> toggleFournisseurStatus(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID id) {
        log.info("Changement du statut du fournisseur: {}", id);
        
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
                
        fournisseur.setActif(!fournisseur.getActif());
        fournisseur.setUpdatedAt(LocalDateTime.now());
        
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Statut du fournisseur modifié avec succès"));
    }

    @PatchMapping("/{id}/solde")
    @Operation(summary = "Mettre à jour le solde d'un fournisseur")
    public ResponseEntity<ApiResponse<FournisseurResponse>> updateFournisseurSolde(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID id,
            @Parameter(description = "Nouveau solde") @RequestParam @NotNull BigDecimal nouveauSolde) {
        log.info("Mise à jour du solde du fournisseur {} à: {}", id, nouveauSolde);
        
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
                
        fournisseur.setSoldeCourant(nouveauSolde.doubleValue());
        fournisseur.setUpdatedAt(LocalDateTime.now());
        
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        FournisseurResponse response = fournisseurMapper.toResponse(savedFournisseur);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Solde du fournisseur mis à jour avec succès"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un fournisseur (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteFournisseur(
            @Parameter(description = "ID du fournisseur") @PathVariable UUID id) {
        log.info("Suppression du fournisseur: {}", id);
        
        Fournisseur fournisseur = fournisseurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fournisseur", id));
                
        fournisseur.setActif(false);
        fournisseur.setUpdatedAt(LocalDateTime.now());
        fournisseurRepository.save(fournisseur);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Fournisseur supprimé avec succès"));
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Vérifier si un fournisseur existe par son email")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(
            @Parameter(description = "Email") @PathVariable @Email String email) {
        
        boolean exists = fournisseurRepository.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/code/{codeFournisseur}")
    @Operation(summary = "Vérifier si un fournisseur existe par son code")
    public ResponseEntity<ApiResponse<Boolean>> existsByCode(
            @Parameter(description = "Code fournisseur") @PathVariable @NotBlank String codeFournisseur) {
        
        boolean exists = fournisseurRepository.existsByCodeFournisseur(codeFournisseur);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de fournisseurs actifs")
    public ResponseEntity<ApiResponse<Long>> countActiveFournisseurs() {
        
        Long count = (long) fournisseurRepository.findByActif(true).size();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/categorie/{categorie}")
    @Operation(summary = "Compter le nombre de fournisseurs par catégorie")
    public ResponseEntity<ApiResponse<Long>> countFournisseursByCategorie(
            @Parameter(description = "Catégorie") @PathVariable @NotBlank String categorie) {
        
        Long count = (long) fournisseurRepository.findByCategorie(categorie).size();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}