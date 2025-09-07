package com.yooyob.erp.controller;

import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.StatistiqueResponse;
import com.yooyob.erp.model.enums.StatutFacture;
import com.yooyob.erp.service.StatistiqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/statistiques")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Statistique", description = "API de gestion des statistiques et analyses")
public class StatistiqueController {

    private final StatistiqueService statistiqueService;

    @GetMapping("/globales")
    @Operation(summary = "Obtenir les statistiques globales")
    public ResponseEntity<ApiResponse<StatistiqueResponse>> getStatistiquesGlobales() {
        
        StatistiqueResponse response = statistiqueService.getStatistiquesGlobales();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/periode")
    @Operation(summary = "Obtenir les statistiques par période")
    public ResponseEntity<ApiResponse<StatistiqueResponse>> getStatistiquesByPeriode(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        StatistiqueResponse response = statistiqueService.getStatistiquesByPeriode(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/chiffre-affaires/total")
    @Operation(summary = "Obtenir le chiffre d'affaires total")
    public ResponseEntity<ApiResponse<BigDecimal>> getChiffreAffairesTotal() {
        
        BigDecimal chiffreAffaires = statistiqueService.getChiffreAffairesTotal();
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/chiffre-affaires/mois-courant")
    @Operation(summary = "Obtenir le chiffre d'affaires du mois courant")
    public ResponseEntity<ApiResponse<BigDecimal>> getChiffreAffairesMoisCourant() {
        
        BigDecimal chiffreAffaires = statistiqueService.getChiffreAffairesMoisCourant();
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/chiffre-affaires/annee-courante")
    @Operation(summary = "Obtenir le chiffre d'affaires de l'année courante")
    public ResponseEntity<ApiResponse<BigDecimal>> getChiffreAffairesAnneeCourante() {
        
        BigDecimal chiffreAffaires = statistiqueService.getChiffreAffairesAnneeCourante();
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/chiffre-affaires/mois/{annee}")
    @Operation(summary = "Obtenir le chiffre d'affaires par mois")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getChiffreAffairesByMois(
            @Parameter(description = "Année") @PathVariable @Positive int annee) {
        
        Map<String, BigDecimal> chiffreAffaires = statistiqueService.getChiffreAffairesByMois(annee);
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/chiffre-affaires/trimestre/{annee}")
    @Operation(summary = "Obtenir le chiffre d'affaires par trimestre")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getChiffreAffairesByTrimestre(
            @Parameter(description = "Année") @PathVariable @Positive int annee) {
        
        Map<String, BigDecimal> chiffreAffaires = statistiqueService.getChiffreAffairesByTrimestre(annee);
        return ResponseEntity.ok(ApiResponse.success(chiffreAffaires));
    }

    @GetMapping("/evolution-mensuelle/{annee}")
    @Operation(summary = "Obtenir l'évolution mensuelle du chiffre d'affaires")
    public ResponseEntity<ApiResponse<List<StatistiqueResponse.ChiffreAffairesMensuel>>> getEvolutionMensuelle(
            @Parameter(description = "Année") @PathVariable @Positive int annee) {
        
        List<StatistiqueResponse.ChiffreAffairesMensuel> evolution = statistiqueService.getEvolutionMensuelle(annee);
        return ResponseEntity.ok(ApiResponse.success(evolution));
    }

    @GetMapping("/factures/total")
    @Operation(summary = "Obtenir le nombre total de factures")
    public ResponseEntity<ApiResponse<Long>> getNombreFacturesTotal() {
        
        Long nombreFactures = statistiqueService.getNombreFacturesTotal();
        return ResponseEntity.ok(ApiResponse.success(nombreFactures));
    }

    @GetMapping("/factures/statut")
    @Operation(summary = "Obtenir le nombre de factures par statut")
    public ResponseEntity<ApiResponse<Map<StatutFacture, Long>>> getNombreFacturesByStatut() {
        
        Map<StatutFacture, Long> repartition = statistiqueService.getNombreFacturesByStatut();
        return ResponseEntity.ok(ApiResponse.success(repartition));
    }

    @GetMapping("/clients/total")
    @Operation(summary = "Obtenir le nombre total de clients")
    public ResponseEntity<ApiResponse<Long>> getNombreClientsTotal() {
        
        Long nombreClients = statistiqueService.getNombreClientsTotal();
        return ResponseEntity.ok(ApiResponse.success(nombreClients));
    }

    @GetMapping("/clients/actifs")
    @Operation(summary = "Obtenir le nombre de clients actifs")
    public ResponseEntity<ApiResponse<Long>> getNombreClientsActifs() {
        
        Long nombreClients = statistiqueService.getNombreClientsActifs();
        return ResponseEntity.ok(ApiResponse.success(nombreClients));
    }

    @GetMapping("/impaye/total")
    @Operation(summary = "Obtenir le montant total impayé")
    public ResponseEntity<ApiResponse<BigDecimal>> getMontantTotalImpaye() {
        
        BigDecimal montantImpaye = statistiqueService.getMontantTotalImpaye();
        return ResponseEntity.ok(ApiResponse.success(montantImpaye));
    }

    @GetMapping("/retard/total")
    @Operation(summary = "Obtenir le montant total en retard")
    public ResponseEntity<ApiResponse<BigDecimal>> getMontantTotalEnRetard() {
        
        BigDecimal montantEnRetard = statistiqueService.getMontantTotalEnRetard();
        return ResponseEntity.ok(ApiResponse.success(montantEnRetard));
    }

    @GetMapping("/top-clients")
    @Operation(summary = "Obtenir les top clients par chiffre d'affaires")
    public ResponseEntity<ApiResponse<List<StatistiqueResponse.TopClient>>> getTopClients(
            @Parameter(description = "Nombre de clients à retourner") @RequestParam(defaultValue = "10") @Positive int limite) {
        
        List<StatistiqueResponse.TopClient> topClients = statistiqueService.getTopClients(limite);
        return ResponseEntity.ok(ApiResponse.success(topClients));
    }

    @GetMapping("/repartition/devise")
    @Operation(summary = "Obtenir la répartition par devise")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getRepartitionParDevise() {
        
        Map<String, BigDecimal> repartition = statistiqueService.getRepartitionParDevise();
        return ResponseEntity.ok(ApiResponse.success(repartition));
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Obtenir les statistiques d'un client spécifique")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistiquesClient(
            @Parameter(description = "ID du client") @PathVariable UUID clientId) {
        
        Map<String, Object> statistiques = statistiqueService.getStatistiquesClient(clientId);
        return ResponseEntity.ok(ApiResponse.success(statistiques));
    }

    @GetMapping("/montant-moyen/factures")
    @Operation(summary = "Obtenir le montant moyen des factures")
    public ResponseEntity<ApiResponse<BigDecimal>> getMontantMoyenFactures() {
        
        BigDecimal montantMoyen = statistiqueService.getMontantMoyenFactures();
        return ResponseEntity.ok(ApiResponse.success(montantMoyen));
    }

    @GetMapping("/montant-moyen/factures/periode")
    @Operation(summary = "Obtenir le montant moyen des factures par période")
    public ResponseEntity<ApiResponse<BigDecimal>> getMontantMoyenFactures(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal montantMoyen = statistiqueService.getMontantMoyenFactures(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(montantMoyen));
    }

    @GetMapping("/delai-moyen-paiement")
    @Operation(summary = "Obtenir le délai moyen de paiement")
    public ResponseEntity<ApiResponse<Double>> getDelaiMoyenPaiement() {
        
        Double delaiMoyen = statistiqueService.getDelaiMoyenPaiement();
        return ResponseEntity.ok(ApiResponse.success(delaiMoyen));
    }

    @GetMapping("/taux-recouvrement")
    @Operation(summary = "Obtenir le taux de recouvrement")
    public ResponseEntity<ApiResponse<BigDecimal>> getTauxRecouvrement() {
        
        BigDecimal tauxRecouvrement = statistiqueService.getTauxRecouvrement();
        return ResponseEntity.ok(ApiResponse.success(tauxRecouvrement));
    }

    @GetMapping("/taux-recouvrement/periode")
    @Operation(summary = "Obtenir le taux de recouvrement par période")
    public ResponseEntity<ApiResponse<BigDecimal>> getTauxRecouvrement(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        BigDecimal tauxRecouvrement = statistiqueService.getTauxRecouvrement(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(tauxRecouvrement));
    }

    @GetMapping("/factures/approchant-echeance")
    @Operation(summary = "Obtenir les factures approchant de l'échéance")
    public ResponseEntity<ApiResponse<Long>> getNombreFacturesApprochantEcheance(
            @Parameter(description = "Nombre de jours avant échéance") @RequestParam(defaultValue = "7") @Positive int nombreJours) {
        
        Long nombreFactures = statistiqueService.getNombreFacturesApprochantEcheance(nombreJours);
        return ResponseEntity.ok(ApiResponse.success(nombreFactures));
    }

    @GetMapping("/factures/retard")
    @Operation(summary = "Obtenir les factures en retard")
    public ResponseEntity<ApiResponse<Long>> getNombreFacturesEnRetard() {
        
        Long nombreFactures = statistiqueService.getNombreFacturesEnRetard();
        return ResponseEntity.ok(ApiResponse.success(nombreFactures));
    }

    @GetMapping("/repartition/factures-tranche-montant")
    @Operation(summary = "Obtenir la répartition des factures par tranche de montant")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getRepartitionFacturesParTrancheMontant() {
        
        Map<String, Long> repartition = statistiqueService.getRepartitionFacturesParTrancheMontant();
        return ResponseEntity.ok(ApiResponse.success(repartition));
    }

    @GetMapping("/paiements/mode")
    @Operation(summary = "Obtenir les statistiques de paiement par mode")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistiquesPaiementParMode() {
        
        Map<String, Object> statistiques = statistiqueService.getStatistiquesPaiementParMode();
        return ResponseEntity.ok(ApiResponse.success(statistiques));
    }

    @GetMapping("/evolution/clients/{annee}")
    @Operation(summary = "Obtenir l'évolution du nombre de clients")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getEvolutionNombreClients(
            @Parameter(description = "Année") @PathVariable @Positive int annee) {
        
        Map<String, Long> evolution = statistiqueService.getEvolutionNombreClients(annee);
        return ResponseEntity.ok(ApiResponse.success(evolution));
    }

    @GetMapping("/tendances/dernier-12-mois")
    @Operation(summary = "Obtenir les tendances sur les 12 derniers mois")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTendancesDernier12Mois() {
        
        Map<String, Object> tendances = statistiqueService.getTendancesDernier12Mois();
        return ResponseEntity.ok(ApiResponse.success(tendances));
    }

    @GetMapping("/rapport-performance")
    @Operation(summary = "Générer un rapport de performance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> genererRapportPerformance(
            @Parameter(description = "Date de début") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Génération du rapport de performance pour la période {} - {}", startDate, endDate);
        
        Map<String, Object> rapport = statistiqueService.genererRapportPerformance(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(rapport));
    }

    @GetMapping("/comparer-performances")
    @Operation(summary = "Comparer les performances entre deux périodes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> comparerPerformancesPeriodes(
            @Parameter(description = "Date de début période 1") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate1,
            @Parameter(description = "Date de fin période 1") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate1,
            @Parameter(description = "Date de début période 2") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate2,
            @Parameter(description = "Date de fin période 2") @RequestParam 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate2) {
        log.info("Comparaison des performances entre {} - {} et {} - {}", 
                startDate1, endDate1, startDate2, endDate2);
        
        Map<String, Object> comparaison = statistiqueService.comparerPerformancesPeriodes(
                startDate1, endDate1, startDate2, endDate2);
        return ResponseEntity.ok(ApiResponse.success(comparaison));
    }
}