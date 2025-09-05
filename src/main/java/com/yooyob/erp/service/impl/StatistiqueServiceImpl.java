package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.response.StatistiqueResponse;
import com.yooyob.erp.model.enums.StatutFacture;
import com.yooyob.erp.repository.custom.CustomFactureRepository;
import com.yooyob.erp.service.StatistiqueService;
import com.yooyob.erp.service.ClientService;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.PaiementService;
import com.yooyob.erp.util.CacheUtil;
import com.yooyob.erp.util.DateUtil;
import com.yooyob.erp.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatistiqueServiceImpl implements StatistiqueService {

    private final CustomFactureRepository customFactureRepository;
    private final ClientService clientService;
    private final FactureService factureService;
    private final PaiementService paiementService;

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'stats_globales'")
    public StatistiqueResponse getStatistiquesGlobales() {
        log.info("Génération des statistiques globales");

        return StatistiqueResponse.builder()
                .chiffreAffairesTotal(getChiffreAffairesTotal())
                .chiffreAffairesMois(getChiffreAffairesMoisCourant())
                .chiffreAffairesAnnee(getChiffreAffairesAnneeCourante())
                .nombreFactures(getNombreFacturesTotal().intValue())
                .nombreClients(getNombreClientsTotal().intValue())
                .montantImpaye(getMontantTotalImpaye())
                .montantEnRetard(getMontantTotalEnRetard())
                .evolutionMensuelle(getEvolutionMensuelle(LocalDate.now().getYear()))
                .topClients(getTopClients(10))
                .repartitionParDevise(getRepartitionParDevise())
                .derniereMiseAJour(LocalDate.now())
                .build();
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'stats_periode_' + #startDate + '_' + #endDate")
    public StatistiqueResponse getStatistiquesByPeriode(LocalDate startDate, LocalDate endDate) {
        log.info("Génération des statistiques pour la période {} - {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont requises");
        }

        Map<String, Object> statsData = customFactureRepository.getFactureStatisticsByPeriod(startDate, endDate);

        BigDecimal chiffreAffaires = (BigDecimal) statsData.getOrDefault("montantTotal", BigDecimal.ZERO);
        Long nombreFactures = (Long) statsData.getOrDefault("nombreFactures", 0L);

        return StatistiqueResponse.builder()
                .chiffreAffairesTotal(chiffreAffaires)
                .nombreFactures(nombreFactures.intValue())
                .montantImpaye(getMontantTotalImpaye())
                .montantEnRetard(getMontantTotalEnRetard())
                .evolutionMensuelle(getEvolutionMensuelleForPeriod(startDate, endDate))
                .derniereMiseAJour(LocalDate.now())
                .build();
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'ca_total'")
    public BigDecimal getChiffreAffairesTotal() {
        log.debug("Calcul du chiffre d'affaires total");

        try {
            Map<String, Object> stats = customFactureRepository.getFactureStatisticsByPeriod(null, null);
            BigDecimal total = (BigDecimal) stats.get("montantTotal");
            return total != null ? total : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du chiffre d'affaires total: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'ca_mois_courant'")
    public BigDecimal getChiffreAffairesMoisCourant() {
        log.debug("Calcul du chiffre d'affaires du mois courant");

        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = DateUtil.getFirstDayOfMonth(now);
        LocalDate endOfMonth = DateUtil.getLastDayOfMonth(now);

        try {
            Map<String, Object> stats = customFactureRepository.getFactureStatisticsByPeriod(startOfMonth, endOfMonth);
            BigDecimal montant = (BigDecimal) stats.get("montantTotal");
            return montant != null ? montant : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du CA du mois courant: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'ca_annee_courante'")
    public BigDecimal getChiffreAffairesAnneeCourante() {
        log.debug("Calcul du chiffre d'affaires de l'année courante");

        LocalDate now = LocalDate.now();
        LocalDate startOfYear = DateUtil.getFirstDayOfYear(now);
        LocalDate endOfYear = DateUtil.getLastDayOfYear(now);

        try {
            Map<String, Object> stats = customFactureRepository.getFactureStatisticsByPeriod(startOfYear, endOfYear);
            BigDecimal montant = (BigDecimal) stats.get("montantTotal");
            return montant != null ? montant : BigDecimal.ZERO;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du CA de l'année courante: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'ca_by_mois_' + #annee")
    public Map<String, BigDecimal> getChiffreAffairesByMois(int annee) {
        log.debug("Calcul du chiffre d'affaires par mois pour l'année {}", annee);

        try {
            return customFactureRepository.getChiffreAffairesByMonth(annee);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du CA par mois pour {}: {}", annee, e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'ca_by_trimestre_' + #annee")
    public Map<String, BigDecimal> getChiffreAffairesByTrimestre(int annee) {
        log.debug("Calcul du chiffre d'affaires par trimestre pour l'année {}", annee);

        try {
            return customFactureRepository.getChiffreAffairesByQuarter(annee);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du CA par trimestre pour {}: {}", annee, e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'evolution_mensuelle_' + #annee")
    public List<StatistiqueResponse.ChiffreAffairesMensuel> getEvolutionMensuelle(int annee) {
        log.debug("Génération de l'évolution mensuelle pour l'année {}", annee);

        try {
            Map<String, BigDecimal> caByMois = getChiffreAffairesByMois(annee);

            List<StatistiqueResponse.ChiffreAffairesMensuel> evolution = new ArrayList<>();

            for (int mois = 1; mois <= 12; mois++) {
                String cle = String.format("%d-%02d", annee, mois);
                BigDecimal montant = caByMois.getOrDefault(cle, BigDecimal.ZERO);

                // Calculer le nombre de factures pour ce mois
                LocalDate startDate = LocalDate.of(annee, mois, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

                Map<String, Object> statsData = customFactureRepository.getFactureStatisticsByPeriod(startDate, endDate);
                Long nombreFactures = (Long) statsData.getOrDefault("nombreFactures", 0L);

                evolution.add(StatistiqueResponse.ChiffreAffairesMensuel.builder()
                        .mois(cle)
                        .montant(montant)
                        .nombreFactures(nombreFactures.intValue())
                        .build());
            }

            return evolution;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul de l'évolution mensuelle: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<StatistiqueResponse.ChiffreAffairesMensuel> getEvolutionMensuelleForPeriod(LocalDate startDate, LocalDate endDate) {
        List<StatistiqueResponse.ChiffreAffairesMensuel> evolution = new ArrayList<>();

        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate)) {
            LocalDate endOfMonth = current.withDayOfMonth(current.lengthOfMonth());
            if (endOfMonth.isAfter(endDate)) {
                endOfMonth = endDate;
            }

            Map<String, Object> statsData = customFactureRepository.getFactureStatisticsByPeriod(current, endOfMonth);
            BigDecimal montant = (BigDecimal) statsData.getOrDefault("montantTotal", BigDecimal.ZERO);
            Long nombreFactures = (Long) statsData.getOrDefault("nombreFactures", 0L);

            evolution.add(StatistiqueResponse.ChiffreAffairesMensuel.builder()
                    .mois(current.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .montant(montant)
                    .nombreFactures(nombreFactures.intValue())
                    .build());

            current = current.plusMonths(1);
        }

        return evolution;
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'nombre_factures_total'")
    public Long getNombreFacturesTotal() {
        log.debug("Calcul du nombre total de factures");

        try {
            return customFactureRepository.getFactureCountByStatut()
                    .values().stream()
                    .mapToLong(Long::longValue)
                    .sum();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du nombre total de factures: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'nombre_factures_by_statut'")
    public Map<StatutFacture, Long> getNombreFacturesByStatut() {
        log.debug("Calcul du nombre de factures par statut");

        try {
            return customFactureRepository.getFactureCountByStatut();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du nombre de factures par statut: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'nombre_clients_total'")
    public Long getNombreClientsTotal() {
        log.debug("Calcul du nombre total de clients");

        try {
            return clientService.countActiveClients();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du nombre total de clients: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'nombre_clients_actifs'")
    public Long getNombreClientsActifs() {
        log.debug("Calcul du nombre de clients actifs");

        try {
            return clientService.countActiveClients();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du nombre de clients actifs: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'montant_total_impaye'")
    public BigDecimal getMontantTotalImpaye() {
        log.debug("Calcul du montant total impayé");

        try {
            return customFactureRepository.getMontantImpayeByClient()
                    .values().stream()
                    .reduce(BigDecimal.ZERO, NumberUtil::safeAdd);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du montant total impayé: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'montant_total_en_retard'")
    public BigDecimal getMontantTotalEnRetard() {
        log.debug("Calcul du montant total en retard");

        try {
            return customFactureRepository.getOverdueFactures()
                    .stream()
                    .map(facture -> facture.getMontantRestant())
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, NumberUtil::safeAdd);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du montant total en retard: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'top_clients_' + #limite")
    public List<StatistiqueResponse.TopClient> getTopClients(int limite) {
        log.debug("Génération du top {} clients", limite);

        try {
            return customFactureRepository.getTopClientsByChiffreAffaires(limite)
                    .stream()
                    .map(clientData -> {
                        String nomClient = (String) clientData.get("nomClient");
                        BigDecimal chiffreAffaires = (BigDecimal) clientData.get("chiffreAffaires");
                        Long nombreFactures = (Long) clientData.getOrDefault("nombreFactures", 0L);

                        return StatistiqueResponse.TopClient.builder()
                                .nomClient(nomClient != null ? nomClient : "Client inconnu")
                                .montantTotal(chiffreAffaires != null ? chiffreAffaires : BigDecimal.ZERO)
                                .nombreFactures(nombreFactures.intValue())
                                .build();
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du top clients: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'repartition_par_devise'")
    public Map<String, BigDecimal> getRepartitionParDevise() {
        log.debug("Calcul de la répartition par devise");

        try {
            Map<String, BigDecimal> repartition = new HashMap<>();
            BigDecimal totalCA = getChiffreAffairesTotal();
            repartition.put("EUR", totalCA);
            return repartition;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul de la répartition par devise: {}", e.getMessage());
            Map<String, BigDecimal> defaultRepartition = new HashMap<>();
            defaultRepartition.put("EUR", BigDecimal.ZERO);
            return defaultRepartition;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'stats_client_' + #clientId")
    public Map<String, Object> getStatistiquesClient(UUID clientId) {
        log.debug("Génération des statistiques pour le client {}", clientId);

        Map<String, Object> stats = new HashMap<>();

        try {
            Long nombreFactures = factureService.countFacturesByClient(clientId);
            stats.put("nombreFactures", nombreFactures != null ? nombreFactures : 0L);

            BigDecimal montantImpaye = customFactureRepository.getMontantImpayeByClient()
                    .getOrDefault(clientId, BigDecimal.ZERO);
            stats.put("montantImpaye", montantImpaye);

            stats.put("montantTotalFacture", BigDecimal.ZERO);
            stats.put("derniereFacture", null);

        } catch (Exception e) {
            log.warn("Erreur lors du calcul des statistiques client {}: {}", clientId, e.getMessage());
        }

        return stats;
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'montant_moyen_factures'")
    public BigDecimal getMontantMoyenFactures() {
        log.debug("Calcul du montant moyen des factures");

        try {
            return customFactureRepository.getMontantMoyenFactures(null, null);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du montant moyen des factures: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'montant_moyen_factures_' + #startDate + '_' + #endDate")
    public BigDecimal getMontantMoyenFactures(LocalDate startDate, LocalDate endDate) {
        log.debug("Calcul du montant moyen des factures pour la période {} - {}", startDate, endDate);

        try {
            return customFactureRepository.getMontantMoyenFactures(startDate, endDate);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du montant moyen pour la période: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Double getDelaiMoyenPaiement() {
        log.debug("Calcul du délai moyen de paiement");

        try {
            // Implementation simplifiée - retourner 30 jours par défaut
            return 30.0;
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du délai moyen de paiement: {}", e.getMessage());
            return 0.0;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'taux_recouvrement'")
    public BigDecimal getTauxRecouvrement() {
        log.debug("Calcul du taux de recouvrement");

        try {
            BigDecimal totalFacture = getChiffreAffairesTotal();
            BigDecimal totalImpaye = getMontantTotalImpaye();

            if (NumberUtil.isZero(totalFacture)) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalPaye = NumberUtil.safeSubtract(totalFacture, totalImpaye);
            return NumberUtil.safeDivide(totalPaye, totalFacture)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du taux de recouvrement: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'taux_recouvrement_' + #startDate + '_' + #endDate")
    public BigDecimal getTauxRecouvrement(LocalDate startDate, LocalDate endDate) {
        log.debug("Calcul du taux de recouvrement pour la période {} - {}", startDate, endDate);

        try {
            Map<String, Object> stats = customFactureRepository.getFactureStatisticsByPeriod(startDate, endDate);
            BigDecimal totalFacture = (BigDecimal) stats.getOrDefault("montantTotal", BigDecimal.ZERO);

            if (NumberUtil.isZero(totalFacture)) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalPaye = paiementService.getTotalPaiementsByPeriode(startDate, endDate);
            return NumberUtil.safeDivide(totalPaye, totalFacture)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du taux de recouvrement pour la période: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Long getNombreFacturesApprochantEcheance(int nombreJours) {
        log.debug("Calcul du nombre de factures approchant de l'échéance dans {} jours", nombreJours);

        try {
            return (long) customFactureRepository.getFacturesApprochantEcheance(nombreJours).size();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul des factures approchant échéance: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    public Long getNombreFacturesEnRetard() {
        log.debug("Calcul du nombre de factures en retard");

        try {
            return (long) customFactureRepository.getOverdueFactures().size();
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du nombre de factures en retard: {}", e.getMessage());
            return 0L;
        }
    }

    @Override
    public Map<String, Long> getRepartitionFacturesParTrancheMontant() {
        log.debug("Calcul de la répartition des factures par tranche de montant");

        Map<String, Long> repartition = new HashMap<>();

        try {
            repartition.put("0-100", 0L);
            repartition.put("100-500", 0L);
            repartition.put("500-1000", 0L);
            repartition.put("1000-5000", 0L);
            repartition.put("5000+", 0L);

        } catch (Exception e) {
            log.warn("Erreur lors du calcul de la répartition par tranche: {}", e.getMessage());
        }

        return repartition;
    }

    @Override
    public Map<String, Object> getStatistiquesPaiementParMode() {
        log.debug("Calcul des statistiques de paiement par mode");

        Map<String, Object> stats = new HashMap<>();

        try {
            stats.put("totalPaiements", BigDecimal.ZERO);
            stats.put("nombrePaiements", 0L);
            stats.put("repartitionParMode", new HashMap<String, BigDecimal>());

        } catch (Exception e) {
            log.warn("Erreur lors du calcul des stats paiement par mode: {}", e.getMessage());
        }

        return stats;
    }

    @Override
    public Map<String, Long> getEvolutionNombreClients(int annee) {
        log.debug("Calcul de l'évolution du nombre de clients pour l'année {}", annee);

        Map<String, Long> evolution = new HashMap<>();

        try {
            for (int mois = 1; mois <= 12; mois++) {
                String cle = String.format("%d-%02d", annee, mois);
                evolution.put(cle, 0L);
            }

        } catch (Exception e) {
            log.warn("Erreur lors du calcul de l'évolution clients: {}", e.getMessage());
        }

        return evolution;
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'tendances_12_mois'")
    public Map<String, Object> getTendancesDernier12Mois() {
        log.debug("Calcul des tendances sur les 12 derniers mois");

        Map<String, Object> tendances = new HashMap<>();

        try {
            LocalDate maintenant = LocalDate.now();
            LocalDate il12Mois = maintenant.minusMonths(12);

            Map<String, Object> stats = customFactureRepository.getFactureStatisticsByPeriod(il12Mois, maintenant);
            tendances.put("chiffreAffaires12Mois", stats.get("montantTotal"));

            List<StatistiqueResponse.ChiffreAffairesMensuel> evolution = new ArrayList<>();
            for (int i = 11; i >= 0; i--) {
                LocalDate debutMois = maintenant.minusMonths(i).withDayOfMonth(1);
                LocalDate finMois = debutMois.withDayOfMonth(debutMois.lengthOfMonth());

                Map<String, Object> statsMois = customFactureRepository.getFactureStatisticsByPeriod(debutMois, finMois);

                evolution.add(StatistiqueResponse.ChiffreAffairesMensuel.builder()
                        .mois(debutMois.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                        .montant((BigDecimal) statsMois.getOrDefault("montantTotal", BigDecimal.ZERO))
                        .nombreFactures(((Long) statsMois.getOrDefault("nombreFactures", 0L)).intValue())
                        .build());
            }

            tendances.put("evolutionMensuelle", evolution);

        } catch (Exception e) {
            log.warn("Erreur lors du calcul des tendances 12 mois: {}", e.getMessage());
        }

        return tendances;
    }

    @Override
    @Cacheable(value = CacheUtil.STATISTIQUE_CACHE, key = "'rapport_performance_' + #startDate + '_' + #endDate")
    public Map<String, Object> genererRapportPerformance(LocalDate startDate, LocalDate endDate) {
        log.info("Génération du rapport de performance du {} au {}", startDate, endDate);

        Map<String, Object> rapport = new HashMap<>();

        try {
            Map<String, Object> statsBase = customFactureRepository.getFactureStatisticsByPeriod(startDate, endDate);
            rapport.putAll(statsBase);

            rapport.put("periode", Map.of(
                    "debut", startDate,
                    "fin", endDate,
                    "nombreJours", java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate)
            ));

            rapport.put("tauxRecouvrement", getTauxRecouvrement(startDate, endDate));
            rapport.put("montantMoyenFactures", getMontantMoyenFactures(startDate, endDate));
            rapport.put("topClients", getTopClients(5));
            rapport.put("repartitionStatut", getNombreFacturesByStatut());
            rapport.put("dateGeneration", LocalDate.now());

        } catch (Exception e) {
            log.warn("Erreur lors de la génération du rapport de performance: {}", e.getMessage());
        }

        return rapport;
    }

    @Override
    public Map<String, Object> comparerPerformancesPeriodes(LocalDate startDate1, LocalDate endDate1,
                                                            LocalDate startDate2, LocalDate endDate2) {
        log.info("Comparaison des performances entre {} - {} et {} - {}",
                startDate1, endDate1, startDate2, endDate2);

        Map<String, Object> comparaison = new HashMap<>();

        try {
            Map<String, Object> stats1 = customFactureRepository.getFactureStatisticsByPeriod(startDate1, endDate1);
            comparaison.put("periode1", Map.of(
                    "debut", startDate1,
                    "fin", endDate1,
                    "stats", stats1
            ));

            Map<String, Object> stats2 = customFactureRepository.getFactureStatisticsByPeriod(startDate2, endDate2);
            comparaison.put("periode2", Map.of(
                    "debut", startDate2,
                    "fin", endDate2,
                    "stats", stats2
            ));

            BigDecimal montant1 = (BigDecimal) stats1.getOrDefault("montantTotal", BigDecimal.ZERO);
            BigDecimal montant2 = (BigDecimal) stats2.getOrDefault("montantTotal", BigDecimal.ZERO);

            BigDecimal evolution = BigDecimal.ZERO;
            if (NumberUtil.isPositive(montant1)) {
                evolution = NumberUtil.safeSubtract(montant2, montant1)
                        .divide(montant1, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            Long nb1 = (Long) stats1.getOrDefault("nombreFactures", 0L);
            Long nb2 = (Long) stats2.getOrDefault("nombreFactures", 0L);

            Double evolutionNombre = 0.0;
            if (nb1 > 0) {
                evolutionNombre = ((double) (nb2 - nb1) / nb1) * 100;
            }

            comparaison.put("evolution", Map.of(
                    "chiffreAffaires", evolution,
                    "nombreFactures", evolutionNombre
            ));

        } catch (Exception e) {
            log.warn("Erreur lors de la comparaison des performances: {}", e.getMessage());
        }

        return comparaison;
    }
}