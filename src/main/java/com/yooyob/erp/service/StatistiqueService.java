package com.yooyob.erp.service;

import com.yooyob.erp.dto.response.StatistiqueResponse;
import com.yooyob.erp.model.enums.StatutFacture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface StatistiqueService {

    /**
     * Obtient les statistiques globales
     */
    StatistiqueResponse getStatistiquesGlobales();

    /**
     * Obtient les statistiques par période
     */
    StatistiqueResponse getStatistiquesByPeriode(LocalDate startDate, LocalDate endDate);

    /**
     * Obtient le chiffre d'affaires total
     */
    BigDecimal getChiffreAffairesTotal();

    /**
     * Obtient le chiffre d'affaires du mois courant
     */
    BigDecimal getChiffreAffairesMoisCourant();

    /**
     * Obtient le chiffre d'affaires de l'année courante
     */
    BigDecimal getChiffreAffairesAnneeCourante();

    /**
     * Obtient le chiffre d'affaires par mois
     */
    Map<String, BigDecimal> getChiffreAffairesByMois(int annee);

    /**
     * Obtient le chiffre d'affaires par trimestre
     */
    Map<String, BigDecimal> getChiffreAffairesByTrimestre(int annee);

    /**
     * Obtient l'évolution mensuelle du chiffre d'affaires
     */
    List<StatistiqueResponse.ChiffreAffairesMensuel> getEvolutionMensuelle(int annee);

    /**
     * Obtient le nombre total de factures
     */
    Long getNombreFacturesTotal();

    /**
     * Obtient le nombre de factures par statut
     */
    Map<StatutFacture, Long> getNombreFacturesByStatut();

    /**
     * Obtient le nombre total de clients
     */
    Long getNombreClientsTotal();

    /**
     * Obtient le nombre de clients actifs
     */
    Long getNombreClientsActifs();

    /**
     * Obtient le montant total impayé
     */
    BigDecimal getMontantTotalImpaye();

    /**
     * Obtient le montant total en retard
     */
    BigDecimal getMontantTotalEnRetard();

    /**
     * Obtient les top clients par chiffre d'affaires
     */
    List<StatistiqueResponse.TopClient> getTopClients(int limite);

    /**
     * Obtient la répartition par devise
     */
    Map<String, BigDecimal> getRepartitionParDevise();

    /**
     * Obtient les statistiques d'un client spécifique
     */
    Map<String, Object> getStatistiquesClient(UUID clientId);

    /**
     * Obtient le montant moyen des factures
     */
    BigDecimal getMontantMoyenFactures();

    /**
     * Obtient le montant moyen des factures par période
     */
    BigDecimal getMontantMoyenFactures(LocalDate startDate, LocalDate endDate);

    /**
     * Obtient le délai moyen de paiement
     */
    Double getDelaiMoyenPaiement();

    /**
     * Obtient le taux de recouvrement
     */
    BigDecimal getTauxRecouvrement();

    /**
     * Obtient le taux de recouvrement par période
     */
    BigDecimal getTauxRecouvrement(LocalDate startDate, LocalDate endDate);

    /**
     * Obtient les factures approchant de l'échéance
     */
    Long getNombreFacturesApprochantEcheance(int nombreJours);

    /**
     * Obtient les factures en retard
     */
    Long getNombreFacturesEnRetard();

    /**
     * Obtient la répartition des factures par tranche de montant
     */
    Map<String, Long> getRepartitionFacturesParTrancheMontant();

    /**
     * Obtient les statistiques de paiement par mode
     */
    Map<String, Object> getStatistiquesPaiementParMode();

    /**
     * Obtient l'évolution du nombre de clients
     */
    Map<String, Long> getEvolutionNombreClients(int annee);

    /**
     * Obtient les tendances sur les 12 derniers mois
     */
    Map<String, Object> getTendancesDernier12Mois();

    /**
     * Génère un rapport de performance
     */
    Map<String, Object> genererRapportPerformance(LocalDate startDate, LocalDate endDate);

    /**
     * Compare les performances entre deux périodes
     */
    Map<String, Object> comparerPerformancesPeriodes(LocalDate startDate1, LocalDate endDate1,
                                                     LocalDate startDate2, LocalDate endDate2);
}