package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.DeviseCreateRequest;
import com.yooyob.erp.dto.request.DeviseUpdateRequest;
import com.yooyob.erp.dto.response.DeviseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface DeviseService {

    /**
     * Crée une nouvelle devise
     */
    DeviseResponse createDevise(DeviseCreateRequest request);

    /**
     * Met à jour une devise existante
     */
    DeviseResponse updateDevise(UUID id, DeviseUpdateRequest request);

    /**
     * Récupère une devise par son ID
     */
    DeviseResponse getDeviseById(UUID id);

    /**
     * Récupère une devise par son nom
     */
    DeviseResponse getDeviseByNom(String nomDevise);

    /**
     * Récupère une devise par son symbole
     */
    DeviseResponse getDeviseBySymbole(String symbole);

    /**
     * Récupère toutes les devises avec pagination
     */
    Page<DeviseResponse> getAllDevises(Pageable pageable);

    /**
     * Récupère toutes les devises actives
     */
    List<DeviseResponse> getAllActiveDevises();

    /**
     * Recherche des devises par nom
     */
    List<DeviseResponse> searchDevisesByNom(String nomDevise);

    /**
     * Active/désactive une devise
     */
    DeviseResponse toggleDeviseStatus(UUID id);

    /**
     * Met à jour le facteur de conversion d'une devise
     */
    DeviseResponse updateFacteurConversion(UUID id, BigDecimal nouveauFacteur);

    /**
     * Supprime une devise (soft delete)
     */
    void deleteDevise(UUID id);

    /**
     * Convertit un montant d'une devise à une autre
     */
    BigDecimal convertAmount(BigDecimal montant, String deviseSource, String deviseTarget);

    /**
     * Convertit un montant d'une devise vers la devise de base
     */
    BigDecimal convertToBaseCurrency(BigDecimal montant, String deviseSource);

    /**
     * Convertit un montant de la devise de base vers une autre devise
     */
    BigDecimal convertFromBaseCurrency(BigDecimal montant, String deviseTarget);

    /**
     * Obtient la devise par défaut du système
     */
    DeviseResponse getDefaultDevise();

    /**
     * Définit une devise comme devise par défaut
     */
    DeviseResponse setDefaultDevise(UUID id);

    /**
     * Vérifie si une devise existe par son nom
     */
    boolean existsByNom(String nomDevise);

    /**
     * Vérifie si une devise existe par son symbole
     */
    boolean existsBySymbole(String symbole);

    /**
     * Compte le nombre de devises actives
     */
    Long countActiveDevises();

    /**
     * Obtient la liste des devises les plus utilisées
     */
    List<DeviseResponse> getMostUsedDevises(int limit);

    /**
     * Met à jour les taux de change depuis une source externe
     */
    void updateExchangeRatesFromExternalSource();
}