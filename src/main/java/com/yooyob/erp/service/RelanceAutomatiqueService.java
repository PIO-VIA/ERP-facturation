package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.ConfigurationRelanceCreateRequest;
import com.yooyob.erp.dto.request.PlanificationRelanceCreateRequest;
import com.yooyob.erp.dto.response.ConfigurationRelanceResponse;
import com.yooyob.erp.dto.response.PlanificationRelanceResponse;
import com.yooyob.erp.dto.response.StatistiqueRelanceResponse;
import com.yooyob.erp.model.enums.TypeRelance;
import com.yooyob.erp.model.enums.StatutRelance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RelanceAutomatiqueService {

    // Gestion des configurations de relance
    ConfigurationRelanceResponse createConfiguration(ConfigurationRelanceCreateRequest request);
    
    ConfigurationRelanceResponse updateConfiguration(UUID idConfiguration, ConfigurationRelanceCreateRequest request);
    
    ConfigurationRelanceResponse getConfiguration(UUID idConfiguration);
    
    List<ConfigurationRelanceResponse> getAllConfigurations();
    
    List<ConfigurationRelanceResponse> getConfigurationsByType(TypeRelance typeRelance);
    
    void deleteConfiguration(UUID idConfiguration);
    
    ConfigurationRelanceResponse activerConfiguration(UUID idConfiguration);
    
    ConfigurationRelanceResponse desactiverConfiguration(UUID idConfiguration);

    // Planification automatique des relances
    void planifierRelancesAutomatiques();
    
    void planifierRelancesFacture(UUID idFacture);
    
    void planifierRelancePersonnalisee(PlanificationRelanceCreateRequest request);
    
    List<PlanificationRelanceResponse> getPlanificationsEnAttente();
    
    List<PlanificationRelanceResponse> getPlanificationsAEnvoyer(LocalDateTime dateLimit);

    // Exécution des relances
    void executerRelancesPlanifiees();
    
    void executerRelance(UUID idPlanification);
    
    void envoyerRelanceManuelle(UUID idFacture, TypeRelance typeRelance, String contenuPersonnalise);
    
    void retenterEnvoiEchec(UUID idPlanification);

    // Gestion des planifications
    PlanificationRelanceResponse getPlanification(UUID idPlanification);
    
    List<PlanificationRelanceResponse> getPlanificationsByFacture(UUID idFacture);
    
    List<PlanificationRelanceResponse> getPlanificationsByClient(UUID idClient);
    
    List<PlanificationRelanceResponse> getPlanificationsByStatut(StatutRelance statut);
    
    Page<PlanificationRelanceResponse> getPlanificationsPaginated(Pageable pageable);

    // Actions sur les planifications
    PlanificationRelanceResponse annulerPlanification(UUID idPlanification, String motif);
    
    PlanificationRelanceResponse reporterPlanification(UUID idPlanification, LocalDateTime nouvelleDate);
    
    PlanificationRelanceResponse marquerCommeRepondue(UUID idPlanification, String contenuReponse);
    
    void supprimerPlanificationsObsoletes(UUID idFacture);

    // Monitoring et statistiques
    StatistiqueRelanceResponse getStatistiquesGlobales();
    
    StatistiqueRelanceResponse getStatistiquesParPeriode(LocalDate startDate, LocalDate endDate);
    
    StatistiqueRelanceResponse getStatistiquesParClient(UUID idClient);
    
    Map<TypeRelance, Integer> getRepartitionParType();
    
    Map<StatutRelance, Integer> getRepartitionParStatut();
    
    List<PlanificationRelanceResponse> getRelancesEchouees();
    
    List<PlanificationRelanceResponse> getRelancesEnRetard();

    // Automatisation et règles métier
    void analyserEfficaciteRelances();
    
    void optimiserConfigurationsAutomatiquement();
    
    void detecterFacturesProblematiques();
    
    void genererRapportPerformance();

    // Gestion des événements
    void onPaiementRecu(UUID idFacture);
    
    void onAvoirEmis(UUID idFacture);
    
    void onEcheanceModifiee(UUID idFacture);
    
    void onClientSuspendu(UUID idClient);

    // Configuration système
    void configurerJoursFeries(List<LocalDate> joursFeries);
    
    void configurerHeuresEnvoi(int heureDebut, int heureFin);
    
    void configurerTemplatesParDefaut();
    
    void sauvegarderLogsExecution();

    // Nettoyage et maintenance
    void nettoyerPlanificationsAnciennes(int joursConservation);
    
    void archiver Relances(LocalDate dateAvant);
    
    void purgerLogsAnciens(int joursConservation);
    
    void verifierIntegriteData();

    // Utilitaires
    boolean isRelanceNecessaire(UUID idFacture);
    
    boolean isClientExcluDesRelances(UUID idClient);
    
    LocalDateTime calculerProchaineDate(TypeRelance typeRelance, LocalDateTime dateEcheance);
    
    String genererContenuRelance(UUID idFacture, TypeRelance typeRelance, String template);
    
    List<ConfigurationRelanceResponse> getConfigurationsApplicables(UUID idFacture);
}