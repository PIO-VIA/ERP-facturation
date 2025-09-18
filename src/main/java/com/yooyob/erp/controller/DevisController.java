package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.DevisCreateRequest;
import com.yooyob.erp.dto.response.DevisResponse;
import com.yooyob.erp.model.enums.StatutDevis;
import com.yooyob.erp.service.DevisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/devis")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Devis", description = "API de gestion des devis")
public class DevisController {

    private final DevisService devisService;

    @PostMapping
    @Operation(summary = "Créer un nouveau devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> createDevis(@Valid @RequestBody DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());
        DevisResponse response = devisService.createDevis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un devis par son ID")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL') or hasRole('CLIENT')")
    public ResponseEntity<DevisResponse> getDevis(@PathVariable UUID id) {
        log.debug("Récupération du devis: {}", id);
        DevisResponse response = devisService.getDevis(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/numero/{numero}")
    @Operation(summary = "Récupérer un devis par son numéro")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> getDevisByNumero(@PathVariable String numero) {
        log.debug("Récupération du devis par numéro: {}", numero);
        DevisResponse response = devisService.getDevisByNumero(numero);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Lister tous les devis avec pagination")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<Page<DevisResponse>> getAllDevis(Pageable pageable) {
        log.debug("Récupération des devis paginés");
        Page<DevisResponse> response = devisService.getDevisPaginated(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Lister les devis d'un client")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL') or (hasRole('CLIENT') and #clientId == authentication.principal.id)")
    public ResponseEntity<List<DevisResponse>> getDevisByClient(@PathVariable UUID clientId) {
        log.debug("Récupération des devis pour le client: {}", clientId);
        List<DevisResponse> response = devisService.getDevisByClient(clientId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statut/{statut}")
    @Operation(summary = "Lister les devis par statut")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<List<DevisResponse>> getDevisByStatut(@PathVariable StatutDevis statut) {
        log.debug("Récupération des devis avec le statut: {}", statut);
        List<DevisResponse> response = devisService.getDevisByStatut(statut);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/periode")
    @Operation(summary = "Lister les devis dans une période")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<List<DevisResponse>> getDevisByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.debug("Récupération des devis entre {} et {}", startDate, endDate);
        List<DevisResponse> response = devisService.getDevisByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> updateDevis(
            @PathVariable UUID id,
            @Valid @RequestBody DevisCreateRequest request) {
        log.info("Mise à jour du devis: {}", id);
        DevisResponse response = devisService.updateDevis(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un devis")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDevis(@PathVariable UUID id) {
        log.info("Suppression du devis: {}", id);
        devisService.deleteDevis(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/statut")
    @Operation(summary = "Changer le statut d'un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> changerStatut(
            @PathVariable UUID id,
            @RequestParam StatutDevis statut,
            @RequestParam(required = false) String motif) {
        log.info("Changement de statut du devis {} vers {}", id, statut);
        DevisResponse response = devisService.changerStatut(id, statut, motif);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/accepter")
    @Operation(summary = "Accepter un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL') or hasRole('CLIENT')")
    public ResponseEntity<DevisResponse> accepterDevis(@PathVariable UUID id) {
        log.info("Acceptation du devis: {}", id);
        DevisResponse response = devisService.accepterDevis(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/refuser")
    @Operation(summary = "Refuser un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL') or hasRole('CLIENT')")
    public ResponseEntity<DevisResponse> refuserDevis(
            @PathVariable UUID id,
            @RequestParam(required = false) String motif) {
        log.info("Refus du devis: {} avec motif: {}", id, motif);
        DevisResponse response = devisService.refuserDevis(id, motif);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/convertir-facture")
    @Operation(summary = "Convertir un devis en facture")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<UUID> convertirEnFacture(@PathVariable UUID id) {
        log.info("Conversion du devis en facture: {}", id);
        UUID factureId = devisService.convertirEnFacture(id);
        return ResponseEntity.ok(factureId);
    }

    @PostMapping("/{id}/envoyer-email")
    @Operation(summary = "Envoyer un devis par email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> envoyerParEmail(@PathVariable UUID id) {
        log.info("Envoi du devis par email: {}", id);
        DevisResponse response = devisService.envoyerParEmail(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/generer-pdf")
    @Operation(summary = "Générer le PDF d'un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<String> genererPdf(@PathVariable UUID id) {
        log.info("Génération du PDF pour le devis: {}", id);
        String pdfPath = devisService.genererPdf(id);
        return ResponseEntity.ok(pdfPath);
    }

    @PostMapping("/{id}/dupliquer")
    @Operation(summary = "Dupliquer un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> dupliquerDevis(@PathVariable UUID id) {
        log.info("Duplication du devis: {}", id);
        DevisResponse response = devisService.dupliquerDevis(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/expires")
    @Operation(summary = "Lister les devis expirés")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<List<DevisResponse>> getDevisExpires() {
        log.debug("Récupération des devis expirés");
        List<DevisResponse> response = devisService.getDevisExpires();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/marquer-expires")
    @Operation(summary = "Marquer les devis expirés")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> marquerDevisCommeExpires() {
        log.info("Marquage des devis expirés");
        devisService.marquerDevisCommeExpires();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/convertis")
    @Operation(summary = "Lister les devis convertis en factures")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<List<DevisResponse>> getDevisConvertis() {
        log.debug("Récupération des devis convertis");
        List<DevisResponse> response = devisService.getDevisConverties();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/recalculer-totaux")
    @Operation(summary = "Recalculer les totaux d'un devis")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<DevisResponse> calculerTotaux(@PathVariable UUID id) {
        log.info("Recalcul des totaux pour le devis: {}", id);
        DevisResponse response = devisService.calculerTotaux(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/modifiable")
    @Operation(summary = "Vérifier si un devis est modifiable")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL')")
    public ResponseEntity<Boolean> isDevisModifiable(@PathVariable UUID id) {
        boolean modifiable = devisService.isDevisModifiable(id);
        return ResponseEntity.ok(modifiable);
    }

    @GetMapping("/{id}/expire")
    @Operation(summary = "Vérifier si un devis est expiré")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMERCIAL') or hasRole('CLIENT')")
    public ResponseEntity<Boolean> isDevisExpire(@PathVariable UUID id) {
        boolean expire = devisService.isDevisExpire(id);
        return ResponseEntity.ok(expire);
    }
}