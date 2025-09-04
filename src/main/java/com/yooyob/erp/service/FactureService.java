package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.request.FactureUpdateRequest;
import com.yooyob.erp.dto.response.FactureResponse;
import com.yooyob.erp.dto.response.FactureDetailsResponse;
import com.yooyob.erp.model.enums.StatutFacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface FactureService {

    /**
     * Crée une nouvelle facture
     */
    FactureResponse createFacture(FactureCreateRequest request);

    /**
     * Met à jour une facture existante
     */
    FactureResponse updateFacture(UUID id, FactureUpdateRequest request);

    /**
     * Récupère une facture par son ID
     */
    FactureResponse getFactureById(UUID id);

    /**
     * Récupère une facture avec tous les détails
     */
    FactureDetailsResponse getFactureDetails(UUID id);

    /**
     * Récupère une facture par son numéro
     */
    FactureResponse getFactureByNumero(String numeroFacture);

    /**
     * Récupère toutes les factures avec pagination
     */
    Page<FactureResponse> getAllFactures(Pageable pageable);

    /**
     * Récupère les factures d'un client
     */
    List<FactureResponse> getFacturesByClient(UUID clientId);

    /**
     * Récupère les factures par statut
     */
    List<FactureResponse> getFacturesByEtat(StatutFacture etat);

    /**
     * Récupère les factures d'un client avec un statut donné
     */
    List<FactureResponse> getFacturesByClientAndEtat(UUID clientId, StatutFacture etat);

    /**
     * Récupère les factures par période
     */
    List<FactureResponse> getFacturesByPeriode(LocalDate startDate, LocalDate endDate);

    /**
     * Récupère les factures par montant
     */
    List<FactureResponse> getFacturesByMontant(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Récupère les factures en retard
     */
    List<FactureResponse> getFacturesEnRetard();

    /**
     * Récupère les factures impayées
     */
    List<FactureResponse> getFacturesImpayes();

    /**
     * Récupère les factures par devise
     */
    List<FactureResponse> getFacturesByDevise(String devise);

    /**
     * Récupère les factures envoyées par email
     */
    List<FactureResponse> getFacturesEnvoyeesParEmail(Boolean envoyees);

    /**
     * Recherche des factures avec filtres multiples
     */
    List<FactureResponse> searchFactures(UUID clientId, StatutFacture etat,
                                         LocalDate dateStart, LocalDate dateEnd,
                                         BigDecimal montantMin, BigDecimal montantMax,
                                         String devise);

    /**
     * Recherche paginée des factures avec filtres
     */
    Page<FactureResponse> searchFacturesWithPagination(UUID clientId, StatutFacture etat,
                                                       LocalDate dateStart, LocalDate dateEnd,
                                                       BigDecimal montantMin, BigDecimal montantMax,
                                                       String devise, Pageable pageable);

    /**
     * Change le statut d'une facture
     */
    FactureResponse changeStatutFacture(UUID id, StatutFacture nouveauStatut);

    /**
     * Marque une facture comme envoyée par email
     */
    FactureResponse marquerEnvoyeeParEmail(UUID id);

    /**
     * Calcule et met à jour les montants d'une facture
     */
    FactureResponse calculerMontantsFacture(UUID id);

    /**
     * Met à jour le montant restant après un paiement
     */
    FactureResponse updateMontantRestant(UUID id, BigDecimal montantPaye);

    /**
     * Génère le PDF d'une facture
     */
    String genererPdfFacture(UUID id);

    /**
     * Envoie une facture par email
     */
    void envoyerFactureParEmail(UUID id);

    /**
     * Supprime une facture (soft delete)
     */
    void deleteFacture(UUID id);

    /**
     * Duplique une facture
     */
    FactureResponse dupliquerFacture(UUID id);

    /**
     * Vérifie si une facture existe par son numéro
     */
    boolean existsByNumero(String numeroFacture);

    /**
     * Compte les factures par statut
     */
    Long countFacturesByEtat(StatutFacture etat);

    /**
     * Compte les factures d'un client
     */
    Long countFacturesByClient(UUID clientId);

    /**
     * Obtient des statistiques sur les factures
     */
    Map<String, Object> getFactureStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * Obtient le chiffre d'affaires par mois
     */
    Map<String, BigDecimal> getChiffreAffairesByMonth(int year);

    /**
     * Obtient les top clients par chiffre d'affaires
     */
    List<Map<String, Object>> getTopClientsByChiffreAffaires(int limit);

    /**
     * Obtient les factures approchant de l'échéance
     */
    List<FactureResponse> getFacturesApprochantEcheance(int nombreJours);
}