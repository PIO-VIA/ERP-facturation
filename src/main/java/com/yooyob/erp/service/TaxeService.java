package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.TaxeCreateRequest;
import com.yooyob.erp.dto.request.TaxeUpdateRequest;
import com.yooyob.erp.dto.response.TaxeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaxeService {

    /**
     * Crée une nouvelle taxe
     */
    TaxeResponse createTaxe(TaxeCreateRequest request);

    /**
     * Met à jour une taxe existante
     */
    TaxeResponse updateTaxe(UUID id, TaxeUpdateRequest request);

    /**
     * Récupère une taxe par son ID
     */
    TaxeResponse getTaxeById(UUID id);

    /**
     * Récupère une taxe par son nom
     */
    TaxeResponse getTaxeByNom(String nomTaxe);

    /**
     * Récupère toutes les taxes avec pagination
     */
    Page<TaxeResponse> getAllTaxes(Pageable pageable);

    /**
     * Récupère toutes les taxes actives
     */
    List<TaxeResponse> getAllActiveTaxes();

    /**
     * Récupère les taxes par type
     */
    List<TaxeResponse> getTaxesByType(String typeTaxe);

    /**
     * Récupère les taxes par porte de taxe
     */
    List<TaxeResponse> getTaxesByPorte(String porteTaxe);

    /**
     * Récupère les taxes par position fiscale
     */
    List<TaxeResponse> getTaxesByPositionFiscale(String positionFiscale);

    /**
     * Récupère les taxes actives par type
     */
    List<TaxeResponse> getActiveTaxesByType(String typeTaxe);

    /**
     * Récupère les taxes par plage de taux
     */
    List<TaxeResponse> getTaxesByTauxRange(BigDecimal minTaux, BigDecimal maxTaux);

    /**
     * Récupère les taxes par plage de montant
     */
    List<TaxeResponse> getTaxesByMontantRange(BigDecimal minMontant, BigDecimal maxMontant);

    /**
     * Active/désactive une taxe
     */
    TaxeResponse toggleTaxeStatus(UUID id);

    /**
     * Met à jour le taux de calcul d'une taxe
     */
    TaxeResponse updateCalculTaxe(UUID id, BigDecimal nouveauTaux);

    /**
     * Met à jour le montant d'une taxe
     */
    TaxeResponse updateMontantTaxe(UUID id, BigDecimal nouveauMontant);

    /**
     * Supprime une taxe (soft delete)
     */
    void deleteTaxe(UUID id);

    /**
     * Calcule le montant de taxe pour un montant HT donné
     */
    BigDecimal calculerMontantTaxe(UUID taxeId, BigDecimal montantHT);

    /**
     * Calcule le montant TTC à partir d'un montant HT et d'une taxe
     */
    BigDecimal calculerMontantTTC(BigDecimal montantHT, UUID taxeId);

    /**
     * Calcule le montant HT à partir d'un montant TTC et d'une taxe
     */
    BigDecimal calculerMontantHT(BigDecimal montantTTC, UUID taxeId);

    /**
     * Applique plusieurs taxes à un montant
     */
    BigDecimal appliquerMultiplesTaxes(BigDecimal montantHT, List<UUID> taxeIds);

    /**
     * Calcule la répartition des taxes pour un montant donné
     */
    Map<UUID, BigDecimal> calculerRepartitionTaxes(BigDecimal montantHT, List<UUID> taxeIds);

    /**
     * Obtient les taxes par défaut pour un type de produit
     */
    List<TaxeResponse> getTaxesParDefaut(String typeProduit);

    /**
     * Vérifie si une taxe existe par son nom
     */
    boolean existsByNom(String nomTaxe);

    /**
     * Compte le nombre de taxes actives
     */
    Long countActiveTaxes();

    /**
     * Compte le nombre de taxes par type
     */
    Long countTaxesByType(String typeTaxe);

    /**
     * Obtient la liste des types de taxes disponibles
     */
    List<String> getAvailableTaxTypes();

    /**
     * Valide qu'un taux de taxe est valide
     */
    boolean isValidTaxRate(BigDecimal taux);

    /**
     * Obtient les taxes les plus utilisées
     */
    List<TaxeResponse> getMostUsedTaxes(int limit);
}