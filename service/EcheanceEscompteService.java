package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.EcheancePaiementCreateRequest;
import com.yooyob.erp.dto.request.EscomptePaiementCreateRequest;
import com.yooyob.erp.dto.response.EcheancePaiementResponse;
import com.yooyob.erp.dto.response.EscomptePaiementResponse;
import com.yooyob.erp.dto.response.PlanEcheanceResponse;
import com.yooyob.erp.model.enums.TypeEcheance;
import com.yooyob.erp.model.enums.TypeEscompte;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface EcheanceEscompteService {

    // Gestion des échéances
    EcheancePaiementResponse creerEcheance(EcheancePaiementCreateRequest request);
    
    EcheancePaiementResponse modifierEcheance(UUID idEcheance, EcheancePaiementCreateRequest request);
    
    EcheancePaiementResponse getEcheance(UUID idEcheance);
    
    List<EcheancePaiementResponse> getEcheancesFacture(UUID idFacture);
    
    List<EcheancePaiementResponse> getEcheancesClient(UUID idClient);
    
    void supprimerEcheance(UUID idEcheance);
    
    EcheancePaiementResponse validerEcheance(UUID idEcheance, UUID validateur);

    // Création automatique d'échéances
    List<EcheancePaiementResponse> creerEcheancesMultiples(UUID idFacture, BigDecimal montantTotal, List<Map<String, Object>> parametresEcheances);
    
    List<EcheancePaiementResponse> creerEcheancesProgressives(UUID idFacture, BigDecimal montantTotal, int nombreEcheances, LocalDate dateDebut, int intervalleJours);
    
    List<EcheancePaiementResponse> creerEcheancesConditionnelles(UUID idFacture, Map<String, Object> conditions);
    
    PlanEcheanceResponse genererPlanEcheance(UUID idFacture, TypeEcheance typeEcheance, Map<String, Object> parametres);

    // Gestion des escomptes
    EscomptePaiementResponse creerEscompte(EscomptePaiementCreateRequest request);
    
    EscomptePaiementResponse modifierEscompte(UUID idEscompte, EscomptePaiementCreateRequest request);
    
    EscomptePaiementResponse getEscompte(UUID idEscompte);
    
    List<EscomptePaiementResponse> getAllEscomptes();
    
    List<EscomptePaiementResponse> getEscomptesActifs();
    
    List<EscomptePaiementResponse> getEscomptesByType(TypeEscompte typeEscompte);
    
    void supprimerEscompte(UUID idEscompte);
    
    EscomptePaiementResponse activerEscompte(UUID idEscompte);
    
    EscomptePaiementResponse desactiverEscompte(UUID idEscompte);

    // Application des escomptes
    List<EscomptePaiementResponse> getEscomptesApplicables(UUID idFacture, UUID idClient, BigDecimal montant);
    
    EscomptePaiementResponse appliquerEscompte(UUID idEcheance, UUID idEscompte);
    
    BigDecimal calculerMontantEscompte(UUID idEscompte, BigDecimal montantBase, Map<String, Object> contexte);
    
    boolean isEscompteApplicable(UUID idEscompte, UUID idFacture, UUID idClient, BigDecimal montant);
    
    EscomptePaiementResponse retirerEscompte(UUID idEcheance);

    // Suivi et monitoring
    List<EcheancePaiementResponse> getEcheancesEnRetard();
    
    List<EcheancePaiementResponse> getEcheancesProchesEcheance(int nombreJours);
    
    List<EcheancePaiementResponse> getEcheancesByStatut(String statut);
    
    List<EcheancePaiementResponse> getEcheancesByPeriode(LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> getStatistiquesEcheances();
    
    Map<String, Object> getStatistiquesEscomptes();

    // Calculs et simulations
    BigDecimal calculerMontantEcheance(UUID idFacture, BigDecimal pourcentage);
    
    LocalDate calculerDateEcheance(LocalDate dateFacture, String conditionsPaiement);
    
    BigDecimal calculerPenaliteRetard(UUID idEcheance, int joursRetard);
    
    BigDecimal simulerEconomieEscompte(UUID idEcheance, UUID idEscompte);
    
    List<Map<String, Object>> simulerPlanEcheance(BigDecimal montantTotal, TypeEcheance typeEcheance, Map<String, Object> parametres);

    // Actions automatiques
    void detecterEcheancesEnRetard();
    
    void appliquerPenalitesRetard();
    
    void envoyerRappelsEcheances();
    
    void proposerEscomptesAutomatiques();
    
    void bloquerClientsDefaillants();
    
    void actualiserStatutsEcheances();

    // Workflows et validations
    EcheancePaiementResponse demanderValidationEcheance(UUID idEcheance, String motif);
    
    EcheancePaiementResponse validerModificationEcheance(UUID idEcheance, UUID validateur);
    
    EcheancePaiementResponse rejeterValidationEcheance(UUID idEcheance, UUID validateur, String motif);
    
    List<EcheancePaiementResponse> getEcheancesEnAttenteValidation();

    // Reporting et analyse
    Map<String, Object> genererRapportEcheances(LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> genererRapportEscomptes(LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> analyserPerformanceRecouvrement(UUID idClient);
    
    List<Map<String, Object>> getTopClientsRetard();
    
    Map<String, Object> analyserEfficaciteEscomptes();

    // Configuration et paramétrage
    void configurerConditionsPaiementDefaut(String conditionsPaiement);
    
    void configurerTauxPenaliteDefaut(BigDecimal tauxPenalite);
    
    void configurerDelaisBlocageClient(int joursRetard);
    
    void configurerRappelsAutomatiques(List<Integer> joursAvantEcheance);
    
    Map<String, Object> getParametresConfiguration();

    // Import/Export
    void importerEcheances(String cheminFichier);
    
    void exporterEcheances(LocalDate dateDebut, LocalDate dateFin, String cheminExport);
    
    void importerEscomptes(String cheminFichier);
    
    void exporterEscomptes(String cheminExport);

    // Intégrations
    void synchroniserAvecComptabilite();
    
    void notifierSystemePaiement(UUID idEcheance);
    
    void integrerAvecBanque(UUID idEcheance);
    
    Map<String, Object> verifierStatutPaiementBancaire(UUID idEcheance);

    // Utilitaires
    boolean isEcheanceModifiable(UUID idEcheance);
    
    boolean isEscompteValide(UUID idEscompte);
    
    String formaterConditionsPaiement(Map<String, Object> conditions);
    
    LocalDate calculerProchaineEcheance(UUID idEcheance);
    
    List<String> validerCoherenceEcheances(UUID idFacture);
}