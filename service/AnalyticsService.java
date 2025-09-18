package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.TableauBordCreateRequest;
import com.yooyob.erp.dto.request.WidgetCreateRequest;
import com.yooyob.erp.dto.response.TableauBordResponse;
import com.yooyob.erp.dto.response.WidgetResponse;
import com.yooyob.erp.dto.response.KpiResponse;
import com.yooyob.erp.dto.response.RapportResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {

    // Gestion des tableaux de bord
    TableauBordResponse creerTableauBord(TableauBordCreateRequest request);
    
    TableauBordResponse modifierTableauBord(UUID idTableau, TableauBordCreateRequest request);
    
    TableauBordResponse getTableauBord(UUID idTableau);
    
    List<TableauBordResponse> getTableauxBordUtilisateur(UUID utilisateur);
    
    List<TableauBordResponse> getTableauxBordPublics();
    
    void supprimerTableauBord(UUID idTableau);
    
    TableauBordResponse dupliquerTableauBord(UUID idTableau, String nouveauNom);
    
    TableauBordResponse definirTableauDefaut(UUID idTableau, UUID utilisateur);

    // Gestion des widgets
    WidgetResponse creerWidget(UUID idTableau, WidgetCreateRequest request);
    
    WidgetResponse modifierWidget(UUID idWidget, WidgetCreateRequest request);
    
    WidgetResponse getWidget(UUID idWidget);
    
    List<WidgetResponse> getWidgetsTableau(UUID idTableau);
    
    void supprimerWidget(UUID idWidget);
    
    WidgetResponse deplacerWidget(UUID idWidget, int nouvellePositionX, int nouvellePositionY);
    
    WidgetResponse redimensionnerWidget(UUID idWidget, int nouvelleLargeur, int nouvelleHauteur);

    // Données et KPIs
    Map<String, Object> getDonneesWidget(UUID idWidget, Map<String, Object> filtres);
    
    KpiResponse calculerKpi(String typeKpi, Map<String, Object> parametres);
    
    List<KpiResponse> getKpisPrincipaux(LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> getMetriquesTempsReel();
    
    List<Map<String, Object>> executerRequetePersonnalisee(String requete, Map<String, Object> parametres);

    // KPIs financiers
    KpiResponse getChiffreAffaires(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getMargeCommerciale(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getTauxRecouvrement(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getDelaiMoyenPaiement(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getCreancesEnCours();
    
    KpiResponse getEvolutionFacturation(int nombreMois);

    // KPIs commerciaux
    KpiResponse getNombreNouveauxClients(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getTauxConversionDevis(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getValeurMoyenneCommande(LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getTopClients(int limite, LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getTopProduits(int limite, LocalDate dateDebut, LocalDate dateFin);
    
    KpiResponse getAnalyseGeographique();

    // Analyses prédictives
    Map<String, Object> getPrevisionChiffreAffaires(int nombreMoisFuturs);
    
    Map<String, Object> getAnalyseTendance(String metrique, LocalDate dateDebut, LocalDate dateFin);
    
    List<Map<String, Object>> detecterAnomalies(String metrique, LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> getScoreRisqueClient(UUID idClient);
    
    List<Map<String, Object>> getClientsARisque();

    // Rapports et exports
    RapportResponse genererRapportExecutif(LocalDate dateDebut, LocalDate dateFin);
    
    RapportResponse genererRapportCommercial(LocalDate dateDebut, LocalDate dateFin);
    
    RapportResponse genererRapportFinancier(LocalDate dateDebut, LocalDate dateFin);
    
    byte[] exporterTableauBordPdf(UUID idTableau);
    
    byte[] exporterTableauBordExcel(UUID idTableau);
    
    void programmerExportAutomatique(UUID idTableau, String frequence, List<String> destinataires);

    // Comparaisons et benchmarking
    Map<String, Object> comparerPeriodes(LocalDate periode1Debut, LocalDate periode1Fin, LocalDate periode2Debut, LocalDate periode2Fin);
    
    Map<String, Object> comparerClients(List<UUID> idsClients, LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> comparerProduits(List<UUID> idsProduits, LocalDate dateDebut, LocalDate dateFin);
    
    Map<String, Object> getBenchmarksSecteur();

    // Alertes et monitoring
    void configurerAlerte(UUID idWidget, String metrique, String operateur, Double seuil);
    
    void verifierAlertes();
    
    void envoyerNotificationAlerte(UUID idAlerte, String message);
    
    List<Map<String, Object>> getHistoriqueAlertes(UUID idTableau);
    
    void suspendreAlerte(UUID idAlerte);
    
    void activerAlerte(UUID idAlerte);

    // Analytics avancés
    Map<String, Object> getAnalyseCohorte(String typeCohorte, LocalDate dateDebut);
    
    Map<String, Object> getAnalyseRFM(); // Récence, Fréquence, Montant
    
    Map<String, Object> getAnalyseABC(); // Classification clients
    
    Map<String, Object> getAnalysePanier();
    
    Map<String, Object> getAnalyseSaisonnalite(String metrique, int nombreAnnees);

    // Performance et optimisation
    void actualiserCacheTableau(UUID idTableau);
    
    void actualiserCacheWidget(UUID idWidget);
    
    void optimiserRequetes();
    
    Map<String, Object> getStatistiquesPerformance();
    
    void prechargerDonnees(List<UUID> idsTableaux);

    // Configuration et personnalisation
    List<String> getTemplatesTableauxDisponibles();
    
    TableauBordResponse creerDepuisTemplate(String nomTemplate, UUID utilisateur);
    
    List<String> getTypesWidgetsDisponibles();
    
    Map<String, Object> getOptionsWidget(String typeWidget);
    
    List<String> getSourcesDonneesDisponibles();

    // Partage et collaboration
    void partagerTableau(UUID idTableau, List<UUID> utilisateurs, String niveauAcces);
    
    void retirerPartage(UUID idTableau, UUID utilisateur);
    
    List<Map<String, Object>> getUtilisateursPartage(UUID idTableau);
    
    void ajouterCommentaire(UUID idTableau, String commentaire);
    
    List<Map<String, Object>> getCommentaires(UUID idTableau);

    // Audit et historique
    List<Map<String, Object>> getHistoriqueModifications(UUID idTableau);
    
    void sauvegarderVersion(UUID idTableau, String description);
    
    TableauBordResponse restaurerVersion(UUID idTableau, UUID idVersion);
    
    List<Map<String, Object>> getVersionsTableau(UUID idTableau);
    
    Map<String, Object> getStatistiquesUtilisation(UUID idTableau);
}