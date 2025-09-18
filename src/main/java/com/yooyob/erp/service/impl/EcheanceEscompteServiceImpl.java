package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.ConfigurationEscompteCreateRequest;
import com.yooyob.erp.dto.request.EcheancePaiementCreateRequest;
import com.yooyob.erp.dto.response.ConfigurationEscompteResponse;
import com.yooyob.erp.dto.response.EcheancePaiementResponse;
import com.yooyob.erp.dto.response.CalculEscompteResponse;
import com.yooyob.erp.model.enums.TypeEscompte;
import com.yooyob.erp.model.enums.StatutEcheance;
import com.yooyob.erp.service.EcheanceEscompteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class EcheanceEscompteServiceImpl implements EcheanceEscompteService {

    // Maps pour simuler le stockage (en production, utiliser une base de données)
    private final Map<UUID, ConfigurationEscompteResponse> configurations = new ConcurrentHashMap<>();
    private final Map<UUID, EcheancePaiementResponse> echeances = new ConcurrentHashMap<>();
    private final Map<UUID, List<EcheancePaiementResponse>> echeancesParFacture = new ConcurrentHashMap<>();

    // ===== GESTION DES ÉCHÉANCES DE PAIEMENT =====

    @Override
    public EcheancePaiementResponse creerEcheancePaiement(EcheancePaiementCreateRequest request) {
        log.info("Création d'une échéance de paiement pour la facture: {}", request.getIdFacture());
        
        UUID idEcheance = UUID.randomUUID();
        
        EcheancePaiementResponse echeance = EcheancePaiementResponse.builder()
                .idEcheance(idEcheance)
                .idFacture(request.getIdFacture())
                .numeroEcheance(request.getNumeroEcheance())
                .montantEcheance(request.getMontantEcheance())
                .dateEcheance(request.getDateEcheance())
                .statut(StatutEcheance.EN_ATTENTE)
                .montantRestant(request.getMontantEcheance())
                .montantPaye(BigDecimal.ZERO)
                .typeEcheance(request.getTypeEcheance())
                .description(request.getDescription())
                .penalitesCalculees(BigDecimal.ZERO)
                .escompteDisponible(request.getEscompteAutorise())
                .createdAt(LocalDateTime.now())
                .build();
        
        // Stocker l'échéance
        echeances.put(idEcheance, echeance);
        
        // Ajouter à la liste des échéances de la facture
        echeancesParFacture.computeIfAbsent(request.getIdFacture(), k -> new ArrayList<>()).add(echeance);
        
        log.info("Échéance créée avec l'ID: {}", idEcheance);
        return echeance;
    }

    @Override
    public List<EcheancePaiementResponse> creerEcheancesMultiples(UUID idFacture, List<EcheancePaiementCreateRequest> echeances) {
        log.info("Création de {} échéances pour la facture: {}", echeances.size(), idFacture);
        
        List<EcheancePaiementResponse> echeancesCrees = new ArrayList<>();
        
        for (EcheancePaiementCreateRequest request : echeances) {
            request.setIdFacture(idFacture);
            echeancesCrees.add(creerEcheancePaiement(request));
        }
        
        return echeancesCrees;
    }

    @Override
    public EcheancePaiementResponse modifierEcheancePaiement(UUID idEcheance, EcheancePaiementCreateRequest request) {
        log.info("Modification de l'échéance: {}", idEcheance);
        
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance == null) {
            log.warn("Échéance non trouvée: {}", idEcheance);
            return null;
        }
        
        // Mise à jour des champs modifiables
        echeance.setMontantEcheance(request.getMontantEcheance());
        echeance.setDateEcheance(request.getDateEcheance());
        echeance.setDescription(request.getDescription());
        echeance.setEscompteDisponible(request.getEscompteAutorise());
        echeance.setUpdatedAt(LocalDateTime.now());
        
        // Recalculer le montant restant si nécessaire
        recalculerMontantRestant(echeance);
        
        return echeance;
    }

    @Override
    public EcheancePaiementResponse getEcheancePaiement(UUID idEcheance) {
        log.debug("Récupération de l'échéance: {}", idEcheance);
        return echeances.get(idEcheance);
    }

    @Override
    public List<EcheancePaiementResponse> getEcheancesByFacture(UUID idFacture) {
        log.debug("Récupération des échéances pour la facture: {}", idFacture);
        return echeancesParFacture.getOrDefault(idFacture, new ArrayList<>());
    }

    @Override
    public List<EcheancePaiementResponse> getEcheancesEchues(LocalDate dateReference) {
        log.debug("Récupération des échéances échues à la date: {}", dateReference);
        
        return echeances.values().stream()
                .filter(e -> e.getDateEcheance().isBefore(dateReference) || e.getDateEcheance().isEqual(dateReference))
                .filter(e -> e.getStatut() == StatutEcheance.EN_ATTENTE)
                .toList();
    }

    @Override
    public List<EcheancePaiementResponse> getEcheancesAVenir(LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Récupération des échéances à venir entre {} et {}", dateDebut, dateFin);
        
        return echeances.values().stream()
                .filter(e -> !e.getDateEcheance().isBefore(dateDebut) && !e.getDateEcheance().isAfter(dateFin))
                .filter(e -> e.getStatut() == StatutEcheance.EN_ATTENTE)
                .toList();
    }

    @Override
    public Page<EcheancePaiementResponse> getEcheancesPaginated(Pageable pageable, StatutEcheance statut) {
        log.debug("Récupération des échéances paginées avec statut: {}", statut);
        
        List<EcheancePaiementResponse> filteredEcheances = echeances.values().stream()
                .filter(e -> statut == null || e.getStatut() == statut)
                .sorted((e1, e2) -> e1.getDateEcheance().compareTo(e2.getDateEcheance()))
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filteredEcheances.size());
        
        return new PageImpl<>(filteredEcheances.subList(start, end), pageable, filteredEcheances.size());
    }

    @Override
    public void supprimerEcheancePaiement(UUID idEcheance) {
        log.info("Suppression de l'échéance: {}", idEcheance);
        
        EcheancePaiementResponse echeance = echeances.remove(idEcheance);
        if (echeance != null) {
            // Retirer de la liste des échéances de la facture
            List<EcheancePaiementResponse> echeancesFacture = echeancesParFacture.get(echeance.getIdFacture());
            if (echeancesFacture != null) {
                echeancesFacture.removeIf(e -> e.getIdEcheance().equals(idEcheance));
            }
        }
    }

    // ===== GESTION DES ESCOMPTES =====

    @Override
    public ConfigurationEscompteResponse creerConfigurationEscompte(ConfigurationEscompteCreateRequest request) {
        log.info("Création d'une configuration d'escompte: {}", request.getNomConfiguration());
        
        UUID idConfiguration = UUID.randomUUID();
        
        ConfigurationEscompteResponse configuration = ConfigurationEscompteResponse.builder()
                .idConfiguration(idConfiguration)
                .nomConfiguration(request.getNomConfiguration())
                .typeEscompte(request.getTypeEscompte())
                .tauxEscompte(request.getTauxEscompte())
                .montantFixeEscompte(request.getMontantFixeEscompte())
                .nombreJoursAvance(request.getNombreJoursAvance())
                .montantMinimal(request.getMontantMinimal())
                .montantMaximal(request.getMontantMaximal())
                .dateDebutValidite(request.getDateDebutValidite())
                .dateFinValidite(request.getDateFinValidite())
                .actif(true)
                .automatique(request.getAutomatique())
                .cumulable(request.getCumulable())
                .createdAt(LocalDateTime.now())
                .build();
        
        configurations.put(idConfiguration, configuration);
        
        log.info("Configuration d'escompte créée avec l'ID: {}", idConfiguration);
        return configuration;
    }

    @Override
    public ConfigurationEscompteResponse modifierConfigurationEscompte(UUID idConfiguration, ConfigurationEscompteCreateRequest request) {
        log.info("Modification de la configuration d'escompte: {}", idConfiguration);
        
        ConfigurationEscompteResponse configuration = configurations.get(idConfiguration);
        if (configuration == null) {
            log.warn("Configuration d'escompte non trouvée: {}", idConfiguration);
            return null;
        }
        
        // Mise à jour des champs
        configuration.setNomConfiguration(request.getNomConfiguration());
        configuration.setTypeEscompte(request.getTypeEscompte());
        configuration.setTauxEscompte(request.getTauxEscompte());
        configuration.setMontantFixeEscompte(request.getMontantFixeEscompte());
        configuration.setNombreJoursAvance(request.getNombreJoursAvance());
        configuration.setMontantMinimal(request.getMontantMinimal());
        configuration.setMontantMaximal(request.getMontantMaximal());
        configuration.setDateDebutValidite(request.getDateDebutValidite());
        configuration.setDateFinValidite(request.getDateFinValidite());
        configuration.setAutomatique(request.getAutomatique());
        configuration.setCumulable(request.getCumulable());
        configuration.setUpdatedAt(LocalDateTime.now());
        
        return configuration;
    }

    @Override
    public ConfigurationEscompteResponse getConfigurationEscompte(UUID idConfiguration) {
        log.debug("Récupération de la configuration d'escompte: {}", idConfiguration);
        return configurations.get(idConfiguration);
    }

    @Override
    public List<ConfigurationEscompteResponse> getAllConfigurationsEscompte() {
        log.debug("Récupération de toutes les configurations d'escompte");
        return new ArrayList<>(configurations.values());
    }

    @Override
    public List<ConfigurationEscompteResponse> getConfigurationsActives() {
        log.debug("Récupération des configurations d'escompte actives");
        return configurations.values().stream()
                .filter(ConfigurationEscompteResponse::getActif)
                .toList();
    }

    @Override
    public void supprimerConfigurationEscompte(UUID idConfiguration) {
        log.info("Suppression de la configuration d'escompte: {}", idConfiguration);
        configurations.remove(idConfiguration);
    }

    @Override
    public ConfigurationEscompteResponse activerConfiguration(UUID idConfiguration) {
        log.info("Activation de la configuration d'escompte: {}", idConfiguration);
        ConfigurationEscompteResponse configuration = configurations.get(idConfiguration);
        if (configuration != null) {
            configuration.setActif(true);
            configuration.setUpdatedAt(LocalDateTime.now());
        }
        return configuration;
    }

    @Override
    public ConfigurationEscompteResponse desactiverConfiguration(UUID idConfiguration) {
        log.info("Désactivation de la configuration d'escompte: {}", idConfiguration);
        ConfigurationEscompteResponse configuration = configurations.get(idConfiguration);
        if (configuration != null) {
            configuration.setActif(false);
            configuration.setUpdatedAt(LocalDateTime.now());
        }
        return configuration;
    }

    // ===== CALCULS D'ESCOMPTE =====

    @Override
    public CalculEscompteResponse calculerEscompte(UUID idEcheance, LocalDate datePaiementPrevue) {
        log.debug("Calcul d'escompte pour l'échéance {} avec paiement prévu le {}", idEcheance, datePaiementPrevue);
        
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance == null) {
            log.warn("Échéance non trouvée: {}", idEcheance);
            return null;
        }
        
        if (!echeance.getEscompteDisponible()) {
            log.debug("Escompte non disponible pour cette échéance");
            return CalculEscompteResponse.builder()
                    .idEcheance(idEcheance)
                    .montantOriginal(echeance.getMontantEcheance())
                    .montantEscompte(BigDecimal.ZERO)
                    .montantAPayer(echeance.getMontantEcheance())
                    .tauxEscompteApplique(BigDecimal.ZERO)
                    .joursAvance(0)
                    .escompteApplicable(false)
                    .motifNonApplicable("Escompte non autorisé pour cette échéance")
                    .dateCalcul(LocalDateTime.now())
                    .build();
        }
        
        // Calculer le nombre de jours d'avance
        long joursAvance = ChronoUnit.DAYS.between(datePaiementPrevue, echeance.getDateEcheance());
        
        if (joursAvance <= 0) {
            return CalculEscompteResponse.builder()
                    .idEcheance(idEcheance)
                    .montantOriginal(echeance.getMontantEcheance())
                    .montantEscompte(BigDecimal.ZERO)
                    .montantAPayer(echeance.getMontantEcheance())
                    .tauxEscompteApplique(BigDecimal.ZERO)
                    .joursAvance((int) joursAvance)
                    .escompteApplicable(false)
                    .motifNonApplicable("Paiement après ou à l'échéance")
                    .dateCalcul(LocalDateTime.now())
                    .build();
        }
        
        // Trouver la meilleure configuration d'escompte applicable
        ConfigurationEscompteResponse meilleureConfig = trouverMeilleureConfigurationEscompte(
                echeance.getMontantEcheance(), (int) joursAvance, datePaiementPrevue);
        
        if (meilleureConfig == null) {
            return CalculEscompteResponse.builder()
                    .idEcheance(idEcheance)
                    .montantOriginal(echeance.getMontantEcheance())
                    .montantEscompte(BigDecimal.ZERO)
                    .montantAPayer(echeance.getMontantEcheance())
                    .tauxEscompteApplique(BigDecimal.ZERO)
                    .joursAvance((int) joursAvance)
                    .escompteApplicable(false)
                    .motifNonApplicable("Aucune configuration d'escompte applicable")
                    .dateCalcul(LocalDateTime.now())
                    .build();
        }
        
        // Calculer le montant d'escompte
        BigDecimal montantEscompte = calculerMontantEscompte(meilleureConfig, echeance.getMontantEcheance(), (int) joursAvance);
        BigDecimal montantAPayer = echeance.getMontantEcheance().subtract(montantEscompte);
        
        return CalculEscompteResponse.builder()
                .idEcheance(idEcheance)
                .idConfiguration(meilleureConfig.getIdConfiguration())
                .nomConfiguration(meilleureConfig.getNomConfiguration())
                .montantOriginal(echeance.getMontantEcheance())
                .montantEscompte(montantEscompte)
                .montantAPayer(montantAPayer)
                .tauxEscompteApplique(meilleureConfig.getTauxEscompte())
                .joursAvance((int) joursAvance)
                .escompteApplicable(true)
                .typeEscompte(meilleureConfig.getTypeEscompte())
                .dateCalcul(LocalDateTime.now())
                .build();
    }

    @Override
    public CalculEscompteResponse calculerEscompteAvecConfiguration(UUID idEcheance, UUID idConfiguration, LocalDate datePaiementPrevue) {
        log.debug("Calcul d'escompte pour l'échéance {} avec la configuration {} et paiement prévu le {}", 
                  idEcheance, idConfiguration, datePaiementPrevue);
        
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        ConfigurationEscompteResponse configuration = configurations.get(idConfiguration);
        
        if (echeance == null || configuration == null) {
            log.warn("Échéance ou configuration non trouvée");
            return null;
        }
        
        long joursAvance = ChronoUnit.DAYS.between(datePaiementPrevue, echeance.getDateEcheance());
        
        if (!isConfigurationApplicable(configuration, echeance.getMontantEcheance(), (int) joursAvance, datePaiementPrevue)) {
            return CalculEscompteResponse.builder()
                    .idEcheance(idEcheance)
                    .idConfiguration(idConfiguration)
                    .montantOriginal(echeance.getMontantEcheance())
                    .montantEscompte(BigDecimal.ZERO)
                    .montantAPayer(echeance.getMontantEcheance())
                    .escompteApplicable(false)
                    .motifNonApplicable("Configuration non applicable")
                    .dateCalcul(LocalDateTime.now())
                    .build();
        }
        
        BigDecimal montantEscompte = calculerMontantEscompte(configuration, echeance.getMontantEcheance(), (int) joursAvance);
        
        return CalculEscompteResponse.builder()
                .idEcheance(idEcheance)
                .idConfiguration(idConfiguration)
                .nomConfiguration(configuration.getNomConfiguration())
                .montantOriginal(echeance.getMontantEcheance())
                .montantEscompte(montantEscompte)
                .montantAPayer(echeance.getMontantEcheance().subtract(montantEscompte))
                .tauxEscompteApplique(configuration.getTauxEscompte())
                .joursAvance((int) joursAvance)
                .escompteApplicable(true)
                .typeEscompte(configuration.getTypeEscompte())
                .dateCalcul(LocalDateTime.now())
                .build();
    }

    @Override
    public List<CalculEscompteResponse> calculerEscompteMultiple(List<UUID> idsEcheances, LocalDate datePaiementPrevue) {
        log.debug("Calcul d'escompte multiple pour {} échéances", idsEcheances.size());
        
        List<CalculEscompteResponse> calculs = new ArrayList<>();
        
        for (UUID idEcheance : idsEcheances) {
            CalculEscompteResponse calcul = calculerEscompte(idEcheance, datePaiementPrevue);
            if (calcul != null) {
                calculs.add(calcul);
            }
        }
        
        return calculs;
    }

    @Override
    public BigDecimal calculerTotalEscompte(List<UUID> idsEcheances, LocalDate datePaiementPrevue) {
        log.debug("Calcul du total d'escompte pour {} échéances", idsEcheances.size());
        
        return calculerEscompteMultiple(idsEcheances, datePaiementPrevue).stream()
                .map(CalculEscompteResponse::getMontantEscompte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ===== MÉTHODES UTILITAIRES PRIVÉES =====

    private void recalculerMontantRestant(EcheancePaiementResponse echeance) {
        echeance.setMontantRestant(echeance.getMontantEcheance().subtract(echeance.getMontantPaye()));
    }

    private ConfigurationEscompteResponse trouverMeilleureConfigurationEscompte(BigDecimal montant, int joursAvance, LocalDate datePaiement) {
        return configurations.values().stream()
                .filter(ConfigurationEscompteResponse::getActif)
                .filter(config -> isConfigurationApplicable(config, montant, joursAvance, datePaiement))
                .max((c1, c2) -> {
                    BigDecimal escompte1 = calculerMontantEscompte(c1, montant, joursAvance);
                    BigDecimal escompte2 = calculerMontantEscompte(c2, montant, joursAvance);
                    return escompte1.compareTo(escompte2);
                })
                .orElse(null);
    }

    private boolean isConfigurationApplicable(ConfigurationEscompteResponse config, BigDecimal montant, int joursAvance, LocalDate datePaiement) {
        // Vérifier la période de validité
        if (config.getDateDebutValidite() != null && datePaiement.isBefore(config.getDateDebutValidite())) {
            return false;
        }
        if (config.getDateFinValidite() != null && datePaiement.isAfter(config.getDateFinValidite())) {
            return false;
        }
        
        // Vérifier le nombre de jours d'avance minimum
        if (config.getNombreJoursAvance() != null && joursAvance < config.getNombreJoursAvance()) {
            return false;
        }
        
        // Vérifier les montants minimum et maximum
        if (config.getMontantMinimal() != null && montant.compareTo(config.getMontantMinimal()) < 0) {
            return false;
        }
        if (config.getMontantMaximal() != null && montant.compareTo(config.getMontantMaximal()) > 0) {
            return false;
        }
        
        return true;
    }

    private BigDecimal calculerMontantEscompte(ConfigurationEscompteResponse config, BigDecimal montant, int joursAvance) {
        if (config.getTypeEscompte() == TypeEscompte.POURCENTAGE && config.getTauxEscompte() != null) {
            return montant.multiply(config.getTauxEscompte().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                    .setScale(2, RoundingMode.HALF_UP);
        } else if (config.getTypeEscompte() == TypeEscompte.MONTANT_FIXE && config.getMontantFixeEscompte() != null) {
            return config.getMontantFixeEscompte();
        } else if (config.getTypeEscompte() == TypeEscompte.DEGRESSIF && config.getTauxEscompte() != null) {
            // Escompte dégressif basé sur le nombre de jours
            BigDecimal facteur = BigDecimal.valueOf(joursAvance).divide(BigDecimal.valueOf(30), 4, RoundingMode.HALF_UP);
            return montant.multiply(config.getTauxEscompte().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP))
                    .multiply(facteur)
                    .setScale(2, RoundingMode.HALF_UP);
        }
        
        return BigDecimal.ZERO;
    }

    // ===== IMPLÉMENTATIONS STUBS POUR LES AUTRES MÉTHODES =====

    @Override public EcheancePaiementResponse marquerEcheancePayee(UUID idEcheance, BigDecimal montantPaye, LocalDate datePaiement) { 
        log.info("Marquage échéance {} comme payée: {}", idEcheance, montantPaye);
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setStatut(StatutEcheance.PAYEE);
            echeance.setMontantPaye(montantPaye);
            echeance.setDatePaiementEffectif(datePaiement);
            recalculerMontantRestant(echeance);
        }
        return echeance;
    }

    @Override public EcheancePaiementResponse marquerEcheancePartielle(UUID idEcheance, BigDecimal montantPaye, LocalDate datePaiement) { 
        log.info("Paiement partiel échéance {}: {}", idEcheance, montantPaye);
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setStatut(StatutEcheance.PARTIELLEMENT_PAYEE);
            echeance.setMontantPaye(echeance.getMontantPaye().add(montantPaye));
            recalculerMontantRestant(echeance);
        }
        return echeance;
    }

    @Override public void appliquerEscompte(UUID idEcheance, CalculEscompteResponse calculEscompte) { 
        log.info("Application escompte sur échéance {}: {}", idEcheance, calculEscompte.getMontantEscompte());
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setMontantEscompteApplique(calculEscompte.getMontantEscompte());
            echeance.setTauxEscompteApplique(calculEscompte.getTauxEscompteApplique());
        }
    }

    @Override public void annulerEscompte(UUID idEcheance) { 
        log.info("Annulation escompte échéance: {}", idEcheance);
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setMontantEscompteApplique(BigDecimal.ZERO);
            echeance.setTauxEscompteApplique(BigDecimal.ZERO);
        }
    }

    @Override public List<EcheancePaiementResponse> getEcheancesAvecEscompte() { 
        return echeances.values().stream()
                .filter(e -> e.getMontantEscompteApplique() != null && e.getMontantEscompteApplique().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    @Override public BigDecimal calculerPenalitesRetard(UUID idEcheance, LocalDate dateReference) { 
        log.debug("Calcul pénalités retard échéance: {}", idEcheance);
        return BigDecimal.valueOf(50); // Pénalité fixe simulée
    }

    @Override public void appliquerPenalitesRetard(UUID idEcheance, BigDecimal montantPenalites) { 
        log.info("Application pénalités {} sur échéance: {}", montantPenalites, idEcheance);
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setPenalitesCalculees(montantPenalites);
        }
    }

    @Override public List<EcheancePaiementResponse> detecterEcheancesProblematiques() { 
        return echeances.values().stream()
                .filter(e -> e.getDateEcheance().isBefore(LocalDate.now().minusDays(30)))
                .filter(e -> e.getStatut() == StatutEcheance.EN_ATTENTE)
                .toList();
    }

    @Override public void reporterEcheance(UUID idEcheance, LocalDate nouvelleDate, String motif) { 
        log.info("Report échéance {} à {}: {}", idEcheance, nouvelleDate, motif);
        EcheancePaiementResponse echeance = echeances.get(idEcheance);
        if (echeance != null) {
            echeance.setDateEcheance(nouvelleDate);
        }
    }

    @Override public Map<String, Object> getStatistiquesEcheances(LocalDate dateDebut, LocalDate dateFin) { 
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEcheances", echeances.size());
        stats.put("echeancesPayees", echeances.values().stream().filter(e -> e.getStatut() == StatutEcheance.PAYEE).count());
        stats.put("echeancesEnRetard", getEcheancesEchues(LocalDate.now()).size());
        stats.put("montantTotalEcheances", echeances.values().stream().map(EcheancePaiementResponse::getMontantEcheance).reduce(BigDecimal.ZERO, BigDecimal::add));
        return stats;
    }

    @Override public Map<String, Object> getStatistiquesEscomptes(LocalDate dateDebut, LocalDate dateFin) { 
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConfigurationsActives", configurations.values().stream().filter(ConfigurationEscompteResponse::getActif).count());
        stats.put("totalEscompteAccorde", BigDecimal.valueOf(15000)); // Simulation
        stats.put("nombreEcheancesAvecEscompte", getEcheancesAvecEscompte().size());
        return stats;
    }

    @Override public void genererRapportEcheancier(UUID idFacture, String formatExport) { log.info("Génération rapport échéancier facture {} en {}", idFacture, formatExport); }
    @Override public void exporterEcheances(LocalDate dateDebut, LocalDate dateFin, String cheminExport) { log.info("Export échéances du {} au {} vers {}", dateDebut, dateFin, cheminExport); }
    @Override public void planifierRappelsEcheances() { log.info("Planification des rappels d'échéances"); }
    @Override public void envoyerNotificationsEcheances(List<UUID> idsEcheances) { log.info("Envoi notifications pour {} échéances", idsEcheances.size()); }
    @Override public void synchroniserEcheancesComptabilite() { log.info("Synchronisation échéances avec comptabilité"); }
    @Override public void archiverEcheancesAnciennes(LocalDate dateAvant) { log.info("Archivage échéances avant: {}", dateAvant); }
    @Override public void optimiserPerformances() { log.info("Optimisation des performances"); }
    @Override public void sauvegarderConfigurations() { log.info("Sauvegarde des configurations"); }
    @Override public void restaurerConfigurations(String cheminSauvegarde) { log.info("Restauration depuis: {}", cheminSauvegarde); }
    @Override public boolean validerCoherenceEcheances(UUID idFacture) { return true; }
    @Override public List<String> detecterAnomaliesEcheances() { return new ArrayList<>(); }
    @Override public void corrigerAnomaliesEcheances(List<String> anomalies) { log.info("Correction de {} anomalies", anomalies.size()); }
}