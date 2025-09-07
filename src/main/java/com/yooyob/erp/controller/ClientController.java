package com.yooyob.erp.controller;

import com.yooyob.erp.dto.request.ClientCreateRequest;
import com.yooyob.erp.dto.request.ClientUpdateRequest;
import com.yooyob.erp.dto.response.ApiResponse;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.model.enums.TypeClient;
import com.yooyob.erp.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Client", description = "API de gestion des clients")
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @Operation(summary = "Créer un nouveau client")
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @Valid @RequestBody ClientCreateRequest request) {
        log.info("Création d'un nouveau client: {}", request.getUsername());
        
        ClientResponse response = clientService.createClient(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Client créé avec succès"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un client existant")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @Parameter(description = "ID du client") @PathVariable UUID id,
            @Valid @RequestBody ClientUpdateRequest request) {
        log.info("Mise à jour du client: {}", id);
        
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Client mis à jour avec succès"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un client par son ID")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(
            @Parameter(description = "ID du client") @PathVariable UUID id) {
        
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Récupérer un client par son nom d'utilisateur")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientByUsername(
            @Parameter(description = "Nom d'utilisateur") @PathVariable @NotBlank String username) {
        
        ClientResponse response = clientService.getClientByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Récupérer un client par son email")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientByEmail(
            @Parameter(description = "Email du client") @PathVariable @Email String email) {
        
        ClientResponse response = clientService.getClientByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/code/{codeClient}")
    @Operation(summary = "Récupérer un client par son code")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientByCode(
            @Parameter(description = "Code client") @PathVariable @NotBlank String codeClient) {
        
        ClientResponse response = clientService.getClientByCode(codeClient);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Récupérer tous les clients avec pagination")
    public ResponseEntity<ApiResponse<Page<ClientResponse>>> getAllClients(
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ClientResponse> response = clientService.getAllClients(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @Operation(summary = "Récupérer tous les clients actifs")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllActiveClients() {
        
        List<ClientResponse> response = clientService.getAllActiveClients();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/type/{typeClient}")
    @Operation(summary = "Récupérer les clients par type")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getClientsByType(
            @Parameter(description = "Type de client") @PathVariable @NotNull TypeClient typeClient) {
        
        List<ClientResponse> response = clientService.getClientsByType(typeClient);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/categorie/{categorie}")
    @Operation(summary = "Récupérer les clients par catégorie")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getClientsByCategorie(
            @Parameter(description = "Catégorie") @PathVariable @NotBlank String categorie) {
        
        List<ClientResponse> response = clientService.getClientsByCategorie(categorie);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/username")
    @Operation(summary = "Rechercher des clients par nom d'utilisateur")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> searchClientsByUsername(
            @Parameter(description = "Nom d'utilisateur à rechercher") @RequestParam @NotBlank String username) {
        
        List<ClientResponse> response = clientService.searchClientsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search/email")
    @Operation(summary = "Rechercher des clients par email")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> searchClientsByEmail(
            @Parameter(description = "Email à rechercher") @RequestParam @NotBlank String email) {
        
        List<ClientResponse> response = clientService.searchClientsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/solde-superieur/{solde}")
    @Operation(summary = "Récupérer les clients avec un solde supérieur à un montant donné")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getClientsWithSoldeGreaterThan(
            @Parameter(description = "Montant minimum du solde") @PathVariable @NotNull Double solde) {
        
        List<ClientResponse> response = clientService.getClientsWithSoldeGreaterThan(solde);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/solde-negatif")
    @Operation(summary = "Récupérer les clients avec un solde négatif")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getClientsWithNegativeBalance() {
        
        List<ClientResponse> response = clientService.getClientsWithNegativeBalance();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deleteClient(
            @Parameter(description = "ID du client") @PathVariable UUID id) {
        log.info("Suppression du client: {}", id);
        
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Client supprimé avec succès"));
    }

    @PatchMapping("/{id}/toggle-status")
    @Operation(summary = "Activer/désactiver un client")
    public ResponseEntity<ApiResponse<ClientResponse>> toggleClientStatus(
            @Parameter(description = "ID du client") @PathVariable UUID id) {
        log.info("Changement du statut du client: {}", id);
        
        ClientResponse response = clientService.toggleClientStatus(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Statut du client modifié avec succès"));
    }

    @PatchMapping("/{id}/solde")
    @Operation(summary = "Mettre à jour le solde d'un client")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClientSolde(
            @Parameter(description = "ID du client") @PathVariable UUID id,
            @Parameter(description = "Nouveau solde") @RequestParam @NotNull Double nouveauSolde) {
        log.info("Mise à jour du solde du client {} à: {}", id, nouveauSolde);
        
        ClientResponse response = clientService.updateClientSolde(id, nouveauSolde);
        return ResponseEntity.ok(ApiResponse.success(response, "Solde du client mis à jour avec succès"));
    }

    @GetMapping("/exists/username/{username}")
    @Operation(summary = "Vérifier si un client existe par son nom d'utilisateur")
    public ResponseEntity<ApiResponse<Boolean>> existsByUsername(
            @Parameter(description = "Nom d'utilisateur") @PathVariable @NotBlank String username) {
        
        boolean exists = clientService.existsByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/email/{email}")
    @Operation(summary = "Vérifier si un client existe par son email")
    public ResponseEntity<ApiResponse<Boolean>> existsByEmail(
            @Parameter(description = "Email") @PathVariable @Email String email) {
        
        boolean exists = clientService.existsByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/exists/code/{codeClient}")
    @Operation(summary = "Vérifier si un client existe par son code")
    public ResponseEntity<ApiResponse<Boolean>> existsByCode(
            @Parameter(description = "Code client") @PathVariable @NotBlank String codeClient) {
        
        boolean exists = clientService.existsByCode(codeClient);
        return ResponseEntity.ok(ApiResponse.success(exists));
    }

    @GetMapping("/count/active")
    @Operation(summary = "Compter le nombre de clients actifs")
    public ResponseEntity<ApiResponse<Long>> countActiveClients() {
        
        Long count = clientService.countActiveClients();
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/count/type/{typeClient}")
    @Operation(summary = "Compter le nombre de clients par type")
    public ResponseEntity<ApiResponse<Long>> countClientsByType(
            @Parameter(description = "Type de client") @PathVariable @NotNull TypeClient typeClient) {
        
        Long count = clientService.countClientsByType(typeClient);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}