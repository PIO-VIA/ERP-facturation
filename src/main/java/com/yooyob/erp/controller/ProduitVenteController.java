package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.ProduitVenteCreateRequest;
import com.yooyob.erp.dto.request.ProduitVenteUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.ProduitVenteResponse;
import com.yooyob.erp.mapper.ProduitVenteMapper;
import com.yooyob.erp.model.entity.ProduitVente;
import com.yooyob.erp.repository.ProduitVenteRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/produits-vente")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "ProduitVente", description = "API de gestion des produits de vente")
public class ProduitVenteController {

    private final ProduitVenteRepository produitVenteRepository;
    private final ProduitVenteMapper produitVenteMapper;

    @PostMapping
    @Operation(summary = "Créer un nouveau produit de vente")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> createProduitVente(
            @Valid @RequestBody ProduitVenteCreateRequest request) {
        log.info("Création d'un nouveau produit de vente: {}", request.getNomProduit());
        
        if (request.getReference() != null && produitVenteRepository.existsByReference(request.getReference())) {
            throw new ValidationException("Un produit avec ce code existe déjà");
        }
        
        ProduitVente produit = produitVenteMapper.toEntity(request);
        produit.setCreatedAt(LocalDateTime.now());
        produit.setUpdatedAt(LocalDateTime.now());
        
        ProduitVente savedProduit = produitVenteRepository.save(produit);
        ProduitVenteResponse response = produitVenteMapper.toResponse(savedProduit);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Produit de vente créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un produit de vente existant")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> updateProduitVente(
            @Parameter(description = "ID du produit") @PathVariable UUID id,
            @Valid @RequestBody ProduitVenteUpdateRequest request) {
        log.info("Mise à jour du produit de vente: {}", id);
        
        ProduitVente existingProduit = produitVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", id));
                
        produitVenteMapper.updateEntityFromRequest(request, existingProduit);
        existingProduit.setUpdatedAt(LocalDateTime.now());
        
        ProduitVente savedProduit = produitVenteRepository.save(existingProduit);
        ProduitVenteResponse response = produitVenteMapper.toResponse(savedProduit);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Produit de vente mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit de vente par son ID")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> getProduitVenteById(
            @Parameter(description = "ID du produit") @PathVariable UUID id) {
        
        ProduitVente produit = produitVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", id));
                
        ProduitVenteResponse response = produitVenteMapper.toResponse(produit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/code/{codeProduit}")
    @Operation(summary = "Récupérer un produit de vente par son code")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> getProduitVenteByCode(
            @Parameter(description = "Code du produit") @PathVariable @NotBlank String codeProduit) {
        
        ProduitVente produit = produitVenteRepository.findByReference(codeProduit)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", "codeProduit", codeProduit));
                
        ProduitVenteResponse response = produitVenteMapper.toResponse(produit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les produits de vente avec pagination")
    public ResponseEntity<ApiResponse<Page<ProduitVenteResponse>>> getAllProduitsVente(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ProduitVente> produitsPage = produitVenteRepository.findAll(pageable);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produitsPage.getContent());
        
        Page<ProduitVenteResponse> responsePage = new PageImpl<>(responses, pageable, produitsPage.getTotalElements());
        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les produits de vente actifs")
    public ResponseEntity<ApiResponse<List<ProduitVenteResponse>>> getAllActiveProduitsVente() {
        
        List<ProduitVente> produits = produitVenteRepository.findByActive(true);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produits);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les produits de vente par catégorie")
    public ResponseEntity<ApiResponse<List<ProduitVenteResponse>>> getProduitsByCategorie(
            @Parameter(description = "Catégorie du produit") @PathVariable @NotBlank String categorie) {
        
        List<ProduitVente> produits = produitVenteRepository.findByCategorie(categorie);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produits);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/prix-vente")
    @Operation(summary = "Récupérer les produits de vente par plage de prix")
    public ResponseEntity<ApiResponse<List<ProduitVenteResponse>>> getProduitsByPrixVente(
            @Parameter(description = "Prix minimum") @RequestParam @Positive BigDecimal minPrix,
            @Parameter(description = "Prix maximum") @RequestParam @Positive BigDecimal maxPrix) {
        
        List<ProduitVente> produits = produitVenteRepository.findByPrixVenteBetween(minPrix, maxPrix);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produits);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }



    @GetMapping("/search/nom")
    @Operation(summary = "Rechercher des produits par nom")
    public ResponseEntity<ApiResponse<List<ProduitVenteResponse>>> searchProduitsByNom(
            @Parameter(description = "Nom du produit à rechercher") @RequestParam @NotBlank String nomProduit) {
        
        List<ProduitVente> produits = produitVenteRepository.findByNomProduitContaining(nomProduit);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produits);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }


    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/désactiver un produit de vente")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> toggleProduitStatus(
            @Parameter(description = "ID du produit") @PathVariable UUID id) {
        log.info("Changement du statut du produit de vente: {}", id);
        
        ProduitVente produit = produitVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", id));
                
        produit.setActive(!produit.getActive());
        produit.setUpdatedAt(LocalDateTime.now());
        
        ProduitVente savedProduit = produitVenteRepository.save(produit);
        ProduitVenteResponse response = produitVenteMapper.toResponse(savedProduit);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Statut du produit modifié avec succès"));
    }

    @PatchMapping("/{id}/prix-vente")
    @Operation(summary = "Mettre à jour le prix de vente d'un produit")
    public ResponseEntity<ApiResponse<ProduitVenteResponse>> updatePrixVente(
            @Parameter(description = "ID du produit") @PathVariable UUID id,
            @Parameter(description = "Nouveau prix de vente") @RequestParam @NotNull @Positive BigDecimal nouveauPrix) {
        log.info("Mise à jour du prix de vente du produit {} à: {}", id, nouveauPrix);
        
        ProduitVente produit = produitVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", id));
                
        produit.setPrixVente(nouveauPrix);
        produit.setUpdatedAt(LocalDateTime.now());
        
        ProduitVente savedProduit = produitVenteRepository.save(produit);
        ProduitVenteResponse response = produitVenteMapper.toResponse(savedProduit);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Prix de vente mis à jour avec succès"));
    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un produit de vente (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteProduitVente(
            @Parameter(description = "ID du produit") @PathVariable UUID id) {
        log.info("Suppression du produit de vente: {}", id);
        
        ProduitVente produit = produitVenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProduitVente", id));
                
        produit.setActive(false);
        produit.setUpdatedAt(LocalDateTime.now());
        produitVenteRepository.save(produit);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Produit de vente supprimé avec succès"));
    }

    @GetMapping("/exists/code/{codeProduit}")
    @Operation(summary = "Vérifier si un produit existe par son code")
    public ResponseEntity<ApiResponse<Boolean>> existsByCode(
            @Parameter(description = "Code du produit") @PathVariable @NotBlank String codeProduit) {
        
        boolean exists = produitVenteRepository.existsByReference(codeProduit);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de produits de vente actifs")
    public ResponseEntity<ApiResponse<Long>> countActiveProduitsVente() {
        
        Long count = (long) produitVenteRepository.findByActive(true).size();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/categorie/{categorie}")
    @Operation(summary = "Compter le nombre de produits par catégorie")
    public ResponseEntity<ApiResponse<Long>> countProduitsByCategorie(
            @Parameter(description = "Catégorie du produit") @PathVariable @NotBlank String categorie) {
        
        Long count = produitVenteRepository.countByCategorie(categorie);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/valeur-stock-total")
    @Operation(summary = "Calculer la valeur totale du stock")
    public ResponseEntity<ApiResponse<BigDecimal>> getValeurStockTotal() {
        
        BigDecimal valeurTotale = produitVenteRepository.findByActive(true).stream().map(ProduitVente::getPrixVente).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(ApiResponse.success(valeurTotale != null ? valeurTotale : BigDecimal.ZERO));
    }

    @GetMapping("/top-vendus")
    @Operation(summary = "Obtenir les produits les plus vendus")
    public ResponseEntity<ApiResponse<List<ProduitVenteResponse>>> getTopSellingProducts(
            @Parameter(description = "Nombre de produits à retourner") @RequestParam(defaultValue = "10") @Positive int limit) {
        
        List<ProduitVente> produits = produitVenteRepository.findByActive(true);
        List<ProduitVenteResponse> responses = produitVenteMapper.toResponseList(produits);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}