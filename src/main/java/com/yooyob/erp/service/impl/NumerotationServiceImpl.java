package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.ParametreNumerotationCreateRequest;
import com.yooyob.erp.dto.response.ParametreNumerotationResponse;
import com.yooyob.erp.model.enums.TypeDocument;
import com.yooyob.erp.service.NumerotationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class NumerotationServiceImpl implements NumerotationService {

    // Maps pour simuler le stockage (en production, utiliser une base de données)
    private final Map<String, ParametreNumerotationResponse> parametres = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> compteurs = new ConcurrentHashMap<>();
    private final Map<String, String> derniersNumeros = new ConcurrentHashMap<>();

    @Override
    public ParametreNumerotationResponse creerParametreNumerotation(ParametreNumerotationCreateRequest request) {
        log.info("Création d'un paramètre de numérotation pour: {}", request.getTypeDocument());
        
        String cle = generateKey(request.getTypeDocument(), request.getSocieteName());
        
        ParametreNumerotationResponse parametre = ParametreNumerotationResponse.builder()
                .idParametre(UUID.randomUUID())
                .typeDocument(request.getTypeDocument())
                .societeName(request.getSocieteName())
                .prefixe(request.getPrefixe())
                .formatDate(request.getFormatDate())
                .nombreChiffres(request.getNombreChiffres())
                .compteurInitial(request.getCompteurInitial())
                .compteurActuel(request.getCompteurInitial())
                .separateur(request.getSeparateur())
                .resetAnnuel(request.getResetAnnuel())
                .resetMensuel(request.getResetMensuel())
                .anneeReference(LocalDate.now().getYear())
                .moisReference(LocalDate.now().getMonthValue())
                .actif(true)
                .build();
        
        parametres.put(cle, parametre);
        compteurs.put(cle, new AtomicInteger(request.getCompteurInitial()));
        
        log.info("Paramètre de numérotation créé avec l'ID: {}", parametre.getIdParametre());
        return parametre;
    }

    @Override
    public ParametreNumerotationResponse modifierParametreNumerotation(UUID idParametre, ParametreNumerotationCreateRequest request) {
        log.info("Modification du paramètre de numérotation: {}", idParametre);
        
        // Simplification: on recrée le paramètre
        ParametreNumerotationResponse parametre = creerParametreNumerotation(request);
        parametre.setIdParametre(idParametre);
        
        return parametre;
    }

    @Override
    public ParametreNumerotationResponse getParametreNumerotation(UUID idParametre) {
        log.debug("Récupération du paramètre de numérotation: {}", idParametre);
        
        return parametres.values().stream()
                .filter(p -> p.getIdParametre().equals(idParametre))
                .findFirst()
                .orElse(ParametreNumerotationResponse.builder()
                        .idParametre(idParametre)
                        .typeDocument(TypeDocument.FACTURE)
                        .prefixe("FAC")
                        .build());
    }

    @Override
    public List<ParametreNumerotationResponse> getAllParametresNumerotation() {
        log.debug("Récupération de tous les paramètres de numérotation");
        return new ArrayList<>(parametres.values());
    }

    @Override
    public List<ParametreNumerotationResponse> getParametresParType(TypeDocument typeDocument) {
        log.debug("Récupération des paramètres pour le type: {}", typeDocument);
        return parametres.values().stream()
                .filter(p -> p.getTypeDocument().equals(typeDocument))
                .toList();
    }

    @Override
    public List<ParametreNumerotationResponse> getParametresParSociete(String societeName) {
        log.debug("Récupération des paramètres pour la société: {}", societeName);
        return parametres.values().stream()
                .filter(p -> p.getSocieteName().equals(societeName))
                .toList();
    }

    @Override
    public void supprimerParametreNumerotation(UUID idParametre) {
        log.info("Suppression du paramètre de numérotation: {}", idParametre);
        
        parametres.entrySet().removeIf(entry -> entry.getValue().getIdParametre().equals(idParametre));
    }

    @Override
    public ParametreNumerotationResponse activerParametre(UUID idParametre) {
        log.info("Activation du paramètre: {}", idParametre);
        ParametreNumerotationResponse parametre = getParametreNumerotation(idParametre);
        parametre.setActif(true);
        return parametre;
    }

    @Override
    public ParametreNumerotationResponse desactiverParametre(UUID idParametre) {
        log.info("Désactivation du paramètre: {}", idParametre);
        ParametreNumerotationResponse parametre = getParametreNumerotation(idParametre);
        parametre.setActif(false);
        return parametre;
    }

    @Override
    public String genererNumeroDocument(TypeDocument typeDocument, String societeName) {
        log.debug("Génération d'un numéro pour: {} - {}", typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        ParametreNumerotationResponse parametre = getParametreActif(typeDocument, societeName);
        
        if (parametre == null) {
            // Paramètre par défaut
            parametre = ParametreNumerotationResponse.builder()
                    .typeDocument(typeDocument)
                    .prefixe(getDefaultPrefixe(typeDocument))
                    .formatDate("yyyyMM")
                    .nombreChiffres(6)
                    .separateur("-")
                    .resetAnnuel(true)
                    .compteurActuel(1)
                    .build();
        }
        
        // Vérifier si un reset est nécessaire
        if (needsReset(parametre)) {
            resetCompteur(cle, parametre);
        }
        
        // Générer le numéro
        AtomicInteger compteur = compteurs.computeIfAbsent(cle, k -> new AtomicInteger(parametre.getCompteurActuel()));
        int numero = compteur.getAndIncrement();
        
        String numeroGenere = buildNumber(parametre, numero);
        derniersNumeros.put(cle, numeroGenere);
        
        log.info("Numéro généré: {}", numeroGenere);
        return numeroGenere;
    }

    @Override
    public String genererNumeroPersonnalise(TypeDocument typeDocument, String societeName, String prefixeCustom) {
        log.debug("Génération d'un numéro personnalisé: {} - {} - {}", typeDocument, societeName, prefixeCustom);
        
        // Utiliser le préfixe personnalisé
        String cle = generateKey(typeDocument, societeName) + "_" + prefixeCustom;
        AtomicInteger compteur = compteurs.computeIfAbsent(cle, k -> new AtomicInteger(1));
        
        int numero = compteur.getAndIncrement();
        String numeroGenere = prefixeCustom + "-" + String.format("%06d", numero);
        
        log.info("Numéro personnalisé généré: {}", numeroGenere);
        return numeroGenere;
    }

    @Override
    public String previsualiserProchainNumero(TypeDocument typeDocument, String societeName) {
        log.debug("Prévisualisation du prochain numéro: {} - {}", typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        ParametreNumerotationResponse parametre = getParametreActif(typeDocument, societeName);
        
        if (parametre == null) {
            return getDefaultPrefixe(typeDocument) + "-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + "-000001";
        }
        
        AtomicInteger compteur = compteurs.get(cle);
        int prochainNumero = compteur != null ? compteur.get() : parametre.getCompteurActuel();
        
        return buildNumber(parametre, prochainNumero);
    }

    @Override
    public boolean isNumeroValide(String numero, TypeDocument typeDocument) {
        log.debug("Validation du numéro: {} pour le type: {}", numero, typeDocument);
        
        if (numero == null || numero.trim().isEmpty()) {
            return false;
        }
        
        // Validation basique - vérifie que le numéro contient le préfixe attendu
        String prefixeAttendu = getDefaultPrefixe(typeDocument);
        return numero.startsWith(prefixeAttendu);
    }

    @Override
    public boolean isNumeroUnique(String numero, TypeDocument typeDocument) {
        log.debug("Vérification de l'unicité du numéro: {} pour le type: {}", numero, typeDocument);
        
        // Simplification: vérifier dans nos derniers numéros générés
        return !derniersNumeros.values().contains(numero);
    }

    @Override
    public String getDernierNumeroGenere(TypeDocument typeDocument, String societeName) {
        log.debug("Récupération du dernier numéro généré: {} - {}", typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        return derniersNumeros.get(cle);
    }

    @Override
    public void resetCompteur(TypeDocument typeDocument, String societeName) {
        log.info("Reset du compteur: {} - {}", typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        ParametreNumerotationResponse parametre = getParametreActif(typeDocument, societeName);
        
        if (parametre != null) {
            compteurs.put(cle, new AtomicInteger(parametre.getCompteurInitial()));
            log.info("Compteur réinitialisé à: {}", parametre.getCompteurInitial());
        }
    }

    @Override
    public void resetCompteurAnnuel(TypeDocument typeDocument) {
        log.info("Reset annuel des compteurs pour le type: {}", typeDocument);
        
        parametres.entrySet().stream()
                .filter(entry -> entry.getValue().getTypeDocument().equals(typeDocument))
                .filter(entry -> entry.getValue().getResetAnnuel())
                .forEach(entry -> {
                    String cle = entry.getKey();
                    ParametreNumerotationResponse parametre = entry.getValue();
                    compteurs.put(cle, new AtomicInteger(parametre.getCompteurInitial()));
                    parametre.setAnneeReference(LocalDate.now().getYear());
                });
    }

    @Override
    public void resetCompteurMensuel(TypeDocument typeDocument) {
        log.info("Reset mensuel des compteurs pour le type: {}", typeDocument);
        
        parametres.entrySet().stream()
                .filter(entry -> entry.getValue().getTypeDocument().equals(typeDocument))
                .filter(entry -> entry.getValue().getResetMensuel())
                .forEach(entry -> {
                    String cle = entry.getKey();
                    ParametreNumerotationResponse parametre = entry.getValue();
                    compteurs.put(cle, new AtomicInteger(parametre.getCompteurInitial()));
                    parametre.setMoisReference(LocalDate.now().getMonthValue());
                });
    }

    @Override
    public void mettreAJourCompteur(TypeDocument typeDocument, String societeName, Integer nouveauCompteur) {
        log.info("Mise à jour du compteur: {} - {} -> {}", typeDocument, societeName, nouveauCompteur);
        
        String cle = generateKey(typeDocument, societeName);
        compteurs.put(cle, new AtomicInteger(nouveauCompteur));
    }

    @Override
    public Integer getCompteurActuel(TypeDocument typeDocument, String societeName) {
        log.debug("Récupération du compteur actuel: {} - {}", typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        AtomicInteger compteur = compteurs.get(cle);
        return compteur != null ? compteur.get() : 1;
    }

    @Override
    public List<String> genererNumerosBatch(TypeDocument typeDocument, String societeName, int quantite) {
        log.info("Génération de {} numéros en batch: {} - {}", quantite, typeDocument, societeName);
        
        List<String> numeros = new ArrayList<>();
        for (int i = 0; i < quantite; i++) {
            numeros.add(genererNumeroDocument(typeDocument, societeName));
        }
        
        return numeros;
    }

    @Override
    public void reserverNumeros(TypeDocument typeDocument, String societeName, int quantite) {
        log.info("Réservation de {} numéros: {} - {}", quantite, typeDocument, societeName);
        
        String cle = generateKey(typeDocument, societeName);
        AtomicInteger compteur = compteurs.computeIfAbsent(cle, k -> new AtomicInteger(1));
        compteur.addAndGet(quantite);
    }

    @Override
    public void libererNumero(String numero, TypeDocument typeDocument) {
        log.info("Libération du numéro: {} pour le type: {}", numero, typeDocument);
        // Implémentation simplifiée - dans un vrai système, marquer le numéro comme disponible
    }

    @Override
    public List<String> getNumerosByDateCreation(TypeDocument typeDocument, LocalDate dateDebut, LocalDate dateFin) {
        log.debug("Récupération des numéros créés entre {} et {} pour le type: {}", dateDebut, dateFin, typeDocument);
        return new ArrayList<>(); // Implémentation simplifiée
    }

    @Override
    public Map<String, Integer> getStatistiquesNumerotation(TypeDocument typeDocument) {
        log.debug("Récupération des statistiques de numérotation pour: {}", typeDocument);
        
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalGeneres", derniersNumeros.size());
        stats.put("totalParametres", (int) parametres.values().stream().filter(p -> p.getTypeDocument().equals(typeDocument)).count());
        
        return stats;
    }

    // Méthodes utilitaires privées
    private String generateKey(TypeDocument typeDocument, String societeName) {
        return typeDocument.name() + "_" + (societeName != null ? societeName : "DEFAULT");
    }

    private ParametreNumerotationResponse getParametreActif(TypeDocument typeDocument, String societeName) {
        String cle = generateKey(typeDocument, societeName);
        return parametres.get(cle);
    }

    private String getDefaultPrefixe(TypeDocument typeDocument) {
        return switch (typeDocument) {
            case FACTURE -> "FAC";
            case DEVIS -> "DEV";
            case AVOIR -> "AV";
            case COMMANDE -> "CMD";
            case BON_LIVRAISON -> "BL";
            case BON_COMMANDE -> "BC";
            default -> "DOC";
        };
    }

    private boolean needsReset(ParametreNumerotationResponse parametre) {
        LocalDate now = LocalDate.now();
        
        if (parametre.getResetAnnuel() && parametre.getAnneeReference() != now.getYear()) {
            return true;
        }
        
        if (parametre.getResetMensuel() && parametre.getMoisReference() != now.getMonthValue()) {
            return true;
        }
        
        return false;
    }

    private void resetCompteur(String cle, ParametreNumerotationResponse parametre) {
        log.info("Reset automatique du compteur pour: {}", cle);
        
        compteurs.put(cle, new AtomicInteger(parametre.getCompteurInitial()));
        
        LocalDate now = LocalDate.now();
        if (parametre.getResetAnnuel()) {
            parametre.setAnneeReference(now.getYear());
        }
        if (parametre.getResetMensuel()) {
            parametre.setMoisReference(now.getMonthValue());
        }
    }

    private String buildNumber(ParametreNumerotationResponse parametre, int numero) {
        StringBuilder sb = new StringBuilder();
        
        // Préfixe
        if (parametre.getPrefixe() != null && !parametre.getPrefixe().isEmpty()) {
            sb.append(parametre.getPrefixe());
        }
        
        // Séparateur
        if (parametre.getSeparateur() != null && !sb.isEmpty()) {
            sb.append(parametre.getSeparateur());
        }
        
        // Date formatée
        if (parametre.getFormatDate() != null && !parametre.getFormatDate().isEmpty()) {
            try {
                String dateFormatee = LocalDate.now().format(DateTimeFormatter.ofPattern(parametre.getFormatDate()));
                sb.append(dateFormatee);
                
                if (parametre.getSeparateur() != null) {
                    sb.append(parametre.getSeparateur());
                }
            } catch (Exception e) {
                log.warn("Erreur lors du formatage de la date: {}", e.getMessage());
            }
        }
        
        // Numéro avec zéros à gauche
        String numeroFormate = String.format("%0" + parametre.getNombreChiffres() + "d", numero);
        sb.append(numeroFormate);
        
        return sb.toString();
    }

    // Implémentations stubs pour les autres méthodes
    @Override public void configurerFormatGlobal(String formatDate, String separateur, Integer nombreChiffres) { log.info("Configuration format global"); }
    @Override public void migrerNumerotationAncienneVersNouvelle(TypeDocument typeDocument) { log.info("Migration numérotation pour: {}", typeDocument); }
    @Override public void synchroniserCompteursMultiSites() { log.info("Synchronisation compteurs multi-sites"); }
    @Override public void sauvegarderParametrages() { log.info("Sauvegarde des paramétrages"); }
    @Override public void restaurerParametrages(String cheminSauvegarde) { log.info("Restauration depuis: {}", cheminSauvegarde); }
    @Override public void verifierCoherenceNumerotation() { log.info("Vérification cohérence numérotation"); }
    @Override public void repererTrousNumerotation(TypeDocument typeDocument) { log.info("Recherche trous numérotation: {}", typeDocument); }
    @Override public void corrigerTrousNumerotation(TypeDocument typeDocument, List<String> numerosManquants) { log.info("Correction trous pour: {}", typeDocument); }
    @Override public void genererRapportNumerotation(TypeDocument typeDocument) { log.info("Génération rapport pour: {}", typeDocument); }
    @Override public void exporterParametrages(String cheminExport) { log.info("Export paramétrages vers: {}", cheminExport); }
    @Override public void importerParametrages(String cheminImport) { log.info("Import paramétrages depuis: {}", cheminImport); }
    @Override public void planifierResetAutomatique(TypeDocument typeDocument, String cronExpression) { log.info("Planification reset auto pour: {}", typeDocument); }
    @Override public void configurerNotificationsReset(List<String> emailsNotification) { log.info("Configuration notifications reset"); }
    @Override public void testGeneration(TypeDocument typeDocument, String societeName, int nombreTests) { log.info("Test génération: {} x{}", typeDocument, nombreTests); }
    @Override public void optimiserPerformances() { log.info("Optimisation des performances"); }
    @Override public boolean isParametrageValide(ParametreNumerotationCreateRequest request) { return true; }
    @Override public List<String> validerCoherenceParametrages() { return new ArrayList<>(); }
    @Override public void configurerValidationPersonnalisee(TypeDocument typeDocument, String regleValidation) { log.info("Configuration validation personnalisée: {}", typeDocument); }
}