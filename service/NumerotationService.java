package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.ConfigurationNumerotationCreateRequest;
import com.yooyob.erp.dto.response.ConfigurationNumerotationResponse;
import com.yooyob.erp.dto.response.SequenceNumerotationResponse;
import com.yooyob.erp.model.enums.TypeNumerotation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface NumerotationService {

    // Gestion des configurations
    ConfigurationNumerotationResponse creerConfiguration(ConfigurationNumerotationCreateRequest request);
    
    ConfigurationNumerotationResponse modifierConfiguration(UUID idConfiguration, ConfigurationNumerotationCreateRequest request);
    
    ConfigurationNumerotationResponse getConfiguration(UUID idConfiguration);
    
    List<ConfigurationNumerotationResponse> getAllConfigurations();
    
    List<ConfigurationNumerotationResponse> getConfigurationsByType(TypeNumerotation typeNumerotation);
    
    void supprimerConfiguration(UUID idConfiguration);
    
    ConfigurationNumerotationResponse activerConfiguration(UUID idConfiguration);
    
    ConfigurationNumerotationResponse desactiverConfiguration(UUID idConfiguration);
    
    ConfigurationNumerotationResponse definirParDefaut(UUID idConfiguration);

    // Génération de numéros
    String genererNumero(TypeNumerotation typeNumerotation);
    
    String genererNumero(TypeNumerotation typeNumerotation, Map<String, Object> variablesContext);
    
    String genererNumeroAvecConfiguration(UUID idConfiguration);
    
    String genererNumeroAvecConfiguration(UUID idConfiguration, Map<String, Object> variablesContext);
    
    List<String> genererPlusieursNumeros(TypeNumerotation typeNumerotation, int nombre);
    
    String previsualiserNumero(UUID idConfiguration, Map<String, Object> variablesContext);

    // Gestion des séquences
    SequenceNumerotationResponse getSequence(UUID idSequence);
    
    List<SequenceNumerotationResponse> getSequencesByConfiguration(UUID idConfiguration);
    
    List<SequenceNumerotationResponse> getSequencesByType(TypeNumerotation typeNumerotation);
    
    SequenceNumerotationResponse resetSequence(UUID idSequence);
    
    SequenceNumerotationResponse modifierValeurSequence(UUID idSequence, Long nouvelleValeur);
    
    SequenceNumerotationResponse verrouillerSequence(UUID idSequence, String motif);
    
    SequenceNumerotationResponse deverrouillerSequence(UUID idSequence);

    // Validation et vérification
    boolean validerModeleNumerotation(String modele);
    
    boolean isNumeroExistant(String numero, TypeNumerotation typeNumerotation);
    
    boolean isConfigurationValide(UUID idConfiguration);
    
    String corrigerModeleNumerotation(String modele);
    
    List<String> detecterProblemes(UUID idConfiguration);

    // Templates et exemples
    List<String> getTemplatesPredefinis(TypeNumerotation typeNumerotation);
    
    String genererExemple(String modele, Map<String, Object> variablesTest);
    
    List<String> genererExemplesMultiples(UUID idConfiguration, int nombre);
    
    Map<String, String> getVariablesDisponibles();
    
    String getDocumentationModele();

    // Import/Export
    void importerConfigurations(String cheminFichier);
    
    void exporterConfigurations(String cheminExport);
    
    void sauvegarderSequences();
    
    void restaurerSequences(String cheminBackup);

    // Recherche et analyse
    ConfigurationNumerotationResponse trouverMeilleureConfiguration(TypeNumerotation typeNumerotation, Map<String, Object> contexte);
    
    List<ConfigurationNumerotationResponse> getConfigurationsActives();
    
    List<String> getHistoriqueNumeros(TypeNumerotation typeNumerotation, int limite);
    
    Map<String, Object> getStatistiquesUtilisation(UUID idConfiguration);
    
    List<String> detecterDoublons(TypeNumerotation typeNumerotation);

    // Maintenance et nettoyage
    void nettoyerSequencesAnciennes(int joursConservation);
    
    void optimiserSequences();
    
    void verifierIntegriteSequences();
    
    void repairerSequencesCorrompues();
    
    void archiverSequences(LocalDateTime dateAvant);

    // Migration et utilitaires
    void migrerVersNouvelleConfiguration(UUID ancienneConfig, UUID nouvelleConfig);
    
    void synchroniserSequences();
    
    void regenererExemplesConfigurations();
    
    Map<String, Long> getStatistiquesGlobales();

    // Gestion des variables personnalisées
    void ajouterVariablePersonnalisee(String nom, String valeur, String description);
    
    void modifierVariablePersonnalisee(String nom, String nouvelleValeur);
    
    void supprimerVariablePersonnalisee(String nom);
    
    Map<String, String> getVariablesPersonnalisees();
    
    String evaluerVariable(String nomVariable, Map<String, Object> contexte);

    // Gestion multi-sites/départements
    ConfigurationNumerotationResponse creerConfigurationSite(TypeNumerotation typeNumerotation, String site, String modele);
    
    ConfigurationNumerotationResponse creerConfigurationDepartement(TypeNumerotation typeNumerotation, String departement, String modele);
    
    List<ConfigurationNumerotationResponse> getConfigurationsSite(String site);
    
    List<ConfigurationNumerotationResponse> getConfigurationsDepartement(String departement);

    // Alertes et monitoring
    void configurerAlertesSequence(UUID idSequence, Long seuilAlerte);
    
    void verifierSeuilsAlertes();
    
    void envoyerAlertes();
    
    List<String> getSequencesProchesLimite();
    
    void surveillerPerformanceGeneration();

    // Test et débogage
    String testerGeneration(UUID idConfiguration, Map<String, Object> variables);
    
    List<String> diagnostiquerConfiguration(UUID idConfiguration);
    
    Map<String, Object> getInformationsDebug(UUID idConfiguration);
    
    void activerModeDebug(UUID idConfiguration);
    
    void desactiverModeDebug(UUID idConfiguration);
}