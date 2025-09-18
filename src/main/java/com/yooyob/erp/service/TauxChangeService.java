package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.HistoriqueTauxChangeCreateRequest;
import com.yooyob.erp.dto.response.HistoriqueTauxChangeResponse;
import com.yooyob.erp.dto.response.ConversionDeviseResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TauxChangeService {

    // Gestion des taux de change
    HistoriqueTauxChangeResponse creerTauxChange(HistoriqueTauxChangeCreateRequest request);
    
    HistoriqueTauxChangeResponse mettreAJourTauxChange(UUID idHistorique, HistoriqueTauxChangeCreateRequest request);
    
    HistoriqueTauxChangeResponse getTauxChange(UUID idHistorique);
    
    List<HistoriqueTauxChangeResponse> getAllTauxChange();
    
    void supprimerTauxChange(UUID idHistorique);

    // Récupération des taux
    BigDecimal getTauxChangeActuel(String deviseSource, String deviseCible);
    
    BigDecimal getTauxChangeADate(String deviseSource, String deviseCible, LocalDateTime date);
    
    List<HistoriqueTauxChangeResponse> getHistoriqueTaux(String deviseSource, String deviseCible);
    
    List<HistoriqueTauxChangeResponse> getTauxParPeriode(String deviseSource, String deviseCible, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    Map<String, BigDecimal> getTousLesTauxActuels(String deviseBase);

    // Conversions
    ConversionDeviseResponse convertirMontant(BigDecimal montant, String deviseSource, String deviseCible);
    
    ConversionDeviseResponse convertirMontantADate(BigDecimal montant, String deviseSource, String deviseCible, LocalDateTime date);
    
    List<ConversionDeviseResponse> convertirVersMultiplesDevises(BigDecimal montant, String deviseSource, List<String> devisesTarget);
    
    BigDecimal calculerMontantAvecCommission(BigDecimal montant, String deviseSource, String deviseCible);

    // Mise à jour automatique
    void mettreAJourTauxAutomatiquement();
    
    void mettreAJourTauxDepuisApi(String sourceTaux);
    
    void mettreAJourTauxDevise(String codeDevise);
    
    void planifierMiseAJourAutomatique();

    // Configuration
    void configurerSourceTauxAutomatique(String deviseSource, String deviseCible, String sourceApi);
    
    void activerMiseAJourAutomatique(String deviseSource, String deviseCible);
    
    void desactiverMiseAJourAutomatique(String deviseSource, String deviseCible);
    
    void definirCommissionDefaut(String deviseSource, String deviseCible, BigDecimal commissionPourcentage, BigDecimal commissionFixe);

    // Alertes et monitoring
    void configurerAlerteVariationTaux(String deviseSource, String deviseCible, BigDecimal seuilVariationPourcentage);
    
    void verifierVariationsTaux();
    
    List<String> detecterTauxAnormaux();
    
    void envoyerAlertesTaux();

    // Statistiques et analyse
    BigDecimal getTauxMoyenPeriode(String deviseSource, String deviseCible, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    BigDecimal getVariationTauxPeriode(String deviseSource, String deviseCible, LocalDateTime dateDebut, LocalDateTime dateFin);
    
    List<HistoriqueTauxChangeResponse> getTendanceTaux(String deviseSource, String deviseCible, int nombreJours);
    
    Map<String, Object> getStatistiquesTaux(String deviseSource, String deviseCible);

    // Gestion des devises
    void ajouterNouvelleDevise(String codeIso, String nom, String symbole);
    
    void activerDevise(String codeIso);
    
    void desactiverDevise(String codeIso);
    
    void definirDeviseBase(String codeIso);
    
    List<String> getDevisesActives();
    
    List<String> getDevisesDisponibles();

    // Import/Export
    void importerTauxDepuisFichier(String cheminFichier);
    
    void exporterHistoriqueTaux(String deviseSource, String deviseCible, String cheminExport);
    
    void sauvegarderTauxPourBackup();
    
    void restaurerTauxDepuisBackup(String cheminBackup);

    // Nettoyage et maintenance
    void nettoyerTauxAnciens(int joursConservation);
    
    void archiverTauxAnciens(LocalDateTime dateAvant);
    
    void optimiserHistoriqueTaux();
    
    void verifierCoherenceTaux();

    // Utilitaires
    boolean isTauxValide(String deviseSource, String deviseCible, BigDecimal taux);
    
    boolean isConversionPossible(String deviseSource, String deviseCible);
    
    String formaterMontant(BigDecimal montant, String codeDevise);
    
    BigDecimal arrondirSelonDevise(BigDecimal montant, String codeDevise);
    
    LocalDateTime getDateDerniereMiseAJour(String deviseSource, String deviseCible);

    // API externes
    void configurerCleApi(String fournisseur, String cleApi);
    
    Map<String, BigDecimal> recupererTauxDepuisApi(String fournisseur);
    
    void testerConnexionApi(String fournisseur);
    
    List<String> getFournisseursApiDisponibles();
}