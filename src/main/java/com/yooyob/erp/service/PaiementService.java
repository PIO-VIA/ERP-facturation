package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.PaiementCreateRequest;
import com.yooyob.erp.dto.request.PaiementUpdateRequest;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.model.enums.TypePaiement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaiementService {

    /**
     * Crée un nouveau paiement
     */
    PaiementResponse createPaiement(PaiementCreateRequest request);

    /**
     * Met à jour un paiement existant
     */
    PaiementResponse updatePaiement(UUID id, PaiementUpdateRequest request);

    /**
     * Récupère un paiement par son ID
     */
    PaiementResponse getPaiementById(UUID id);

    /**
     * Récupère tous les paiements avec pagination
     */
    Page<PaiementResponse> getAllPaiements(Pageable pageable);

    /**
     * Récupère les paiements d'un client
     */
    List<PaiementResponse> getPaiementsByClient(UUID clientId);

    /**
     * Récupère les paiements d'une facture
     */
    List<PaiementResponse> getPaiementsByFacture(UUID factureId);

    /**
     * Récupère les paiements par mode de paiement
     */
    List<PaiementResponse> getPaiementsByModePaiement(TypePaiement modePaiement);

    /**
     * Récupère les paiements par journal
     */
    List<PaiementResponse> getPaiementsByJournal(String journal);

    /**
     * Récupère les paiements par période
     */
    List<PaiementResponse> getPaiementsByPeriode(LocalDate startDate, LocalDate endDate);

    /**
     * Récupère les paiements par montant
     */
    List<PaiementResponse> getPaiementsByMontant(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Récupère les paiements d'un client par période
     */
    List<PaiementResponse> getPaiementsByClientAndPeriode(UUID clientId, LocalDate startDate, LocalDate endDate);

    /**
     * Récupère les paiements d'une facture triés par date
     */
    List<PaiementResponse> getPaiementsByFactureOrderByDate(UUID factureId);

    /**
     * Récupère les paiements par mode et période
     */
    List<PaiementResponse> getPaiementsByModePaiementAndPeriode(TypePaiement modePaiement, LocalDate startDate, LocalDate endDate);

    /**
     * Supprime un paiement
     */
    void deletePaiement(UUID id);

    /**
     * Calcule le montant total des paiements d'un client
     */
    BigDecimal getTotalPaiementsByClient(UUID clientId);

    /**
     * Calcule le montant total des paiements d'une facture
     */
    BigDecimal getTotalPaiementsByFacture(UUID factureId);

    /**
     * Calcule le montant total des paiements par période
     */
    BigDecimal getTotalPaiementsByPeriode(LocalDate startDate, LocalDate endDate);

    /**
     * Compte les paiements d'un client
     */
    Long countPaiementsByClient(UUID clientId);

    /**
     * Compte les paiements par mode de paiement
     */
    Long countPaiementsByModePaiement(TypePaiement modePaiement);

    /**
     * Compte les paiements par période
     */
    Long countPaiementsByPeriode(LocalDate startDate, LocalDate endDate);

    /**
     * Valide qu'un paiement peut être effectué pour une facture
     */
    boolean canPayFacture(UUID factureId, BigDecimal montantPaiement);

    /**
     * Traite un paiement de facture (met à jour la facture et crée le paiement)
     */
    PaiementResponse traiterPaiementFacture(UUID factureId, PaiementCreateRequest request);
}