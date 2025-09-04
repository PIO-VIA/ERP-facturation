package com.yooyob.erp.repository.custom;

import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.enums.StatutFacture;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CustomFactureRepository {

    /**
     * Recherche de factures avec filtres multiples
     */
    List<Facture> findFacturesWithFilters(
            UUID clientId,
            StatutFacture etat,
            LocalDate dateStart,
            LocalDate dateEnd,
            BigDecimal montantMin,
            BigDecimal montantMax,
            String devise
    );

    /**
     * Recherche paginée avec filtres
     */
    Page<Facture> findFacturesWithFiltersAndPagination(
            UUID clientId,
            StatutFacture etat,
            LocalDate dateStart,
            LocalDate dateEnd,
            BigDecimal montantMin,
            BigDecimal montantMax,
            String devise,
            Pageable pageable
    );

    /**
     * Statistiques des factures par période
     */
    Map<String, Object> getFactureStatisticsByPeriod(LocalDate startDate, LocalDate endDate);

    /**
     * Chiffre d'affaires par mois
     */
    Map<String, BigDecimal> getChiffreAffairesByMonth(int year);

    /**
     * Top clients par chiffre d'affaires
     */
    List<Map<String, Object>> getTopClientsByChiffreAffaires(int limit);

    /**
     * Factures en retard
     */
    List<Facture> getOverdueFactures();

    /**
     * Montant total impayé par client
     */
    Map<UUID, BigDecimal> getMontantImpayeByClient();

    /**
     * Evolution du chiffre d'affaires par trimestre
     */
    Map<String, BigDecimal> getChiffreAffairesByQuarter(int year);

    /**
     * Factures proches de l'échéance
     */
    List<Facture> getFacturesApprochantEcheance(int nombreJours);

    /**
     * Répartition des factures par statut
     */
    Map<StatutFacture, Long> getFactureCountByStatut();

    /**
     * Montant moyen des factures par période
     */
    BigDecimal getMontantMoyenFactures(LocalDate startDate, LocalDate endDate);
}