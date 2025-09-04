package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.ClientCreateRequest;
import com.yooyob.erp.dto.request.ClientUpdateRequest;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.model.enums.TypeClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    /**
     * Crée un nouveau client
     */
    ClientResponse createClient(ClientCreateRequest request);

    /**
     * Met à jour un client existant
     */
    ClientResponse updateClient(UUID id, ClientUpdateRequest request);

    /**
     * Récupère un client par son ID
     */
    ClientResponse getClientById(UUID id);

    /**
     * Récupère un client par son nom d'utilisateur
     */
    ClientResponse getClientByUsername(String username);

    /**
     * Récupère un client par son email
     */
    ClientResponse getClientByEmail(String email);

    /**
     * Récupère un client par son code client
     */
    ClientResponse getClientByCode(String codeClient);

    /**
     * Récupère tous les clients avec pagination
     */
    Page<ClientResponse> getAllClients(Pageable pageable);

    /**
     * Récupère tous les clients actifs
     */
    List<ClientResponse> getAllActiveClients();

    /**
     * Récupère les clients par type
     */
    List<ClientResponse> getClientsByType(TypeClient typeClient);

    /**
     * Récupère les clients par catégorie
     */
    List<ClientResponse> getClientsByCategorie(String categorie);

    /**
     * Recherche des clients par nom d'utilisateur
     */
    List<ClientResponse> searchClientsByUsername(String username);

    /**
     * Recherche des clients par email
     */
    List<ClientResponse> searchClientsByEmail(String email);

    /**
     * Récupère les clients avec un solde supérieur à un montant donné
     */
    List<ClientResponse> getClientsWithSoldeGreaterThan(Double solde);

    /**
     * Récupère les clients avec un solde négatif
     */
    List<ClientResponse> getClientsWithNegativeBalance();

    /**
     * Supprime un client (soft delete en passant actif à false)
     */
    void deleteClient(UUID id);

    /**
     * Active/désactive un client
     */
    ClientResponse toggleClientStatus(UUID id);

    /**
     * Met à jour le solde d'un client
     */
    ClientResponse updateClientSolde(UUID id, Double nouveauSolde);

    /**
     * Vérifie si un client existe par son nom d'utilisateur
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un client existe par son email
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un client existe par son code client
     */
    boolean existsByCode(String codeClient);

    /**
     * Compte le nombre de clients actifs
     */
    Long countActiveClients();

    /**
     * Compte le nombre de clients par type
     */
    Long countClientsByType(TypeClient typeClient);
}