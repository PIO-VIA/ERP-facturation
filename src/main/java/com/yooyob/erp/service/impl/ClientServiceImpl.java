package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.ClientCreateRequest;
import com.yooyob.erp.dto.request.ClientUpdateRequest;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.mapper.ClientMapper;
import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.enums.TypeClient;
import com.yooyob.erp.repository.ClientRepository;
import com.yooyob.erp.service.ClientService;
import com.yooyob.erp.util.CacheUtil;
import com.yooyob.erp.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final CacheUtil cacheUtil;

    @Override
    public ClientResponse createClient(ClientCreateRequest request) {
        log.info("Création d'un nouveau client: {}", request.getUsername());

        validateClientCreateRequest(request);

        Client client = clientMapper.toEntity(request);
        client.setCreatedAt(LocalDateTime.now());
        client.setUpdatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(client);
        log.info("Client créé avec succès: {}", savedClient.getIdClient());

        ClientResponse response = clientMapper.toResponse(savedClient);
        cacheUtil.cacheClient(savedClient.getIdClient(), response);

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.CLIENT_CACHE, key = "#id")
    public ClientResponse updateClient(UUID id, ClientUpdateRequest request) {
        log.info("Mise à jour du client: {}", id);

        Client existingClient = findClientById(id);
        validateClientUpdateRequest(request, existingClient);

        clientMapper.updateEntityFromRequest(request, existingClient);
        existingClient.setUpdatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(existingClient);
        log.info("Client mis à jour avec succès: {}", id);

        ClientResponse response = clientMapper.toResponse(savedClient);
        cacheUtil.cacheClient(id, response);

        return response;
    }

    @Override
    @Cacheable(value = CacheUtil.CLIENT_CACHE, key = "#id")
    public ClientResponse getClientById(UUID id) {
        log.debug("Récupération du client par ID: {}", id);

        Client client = findClientById(id);
        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse getClientByUsername(String username) {
        log.debug("Récupération du client par username: {}", username);

        if (ValidationUtil.isBlank(username)) {
            throw new ValidationException("Le nom d'utilisateur est requis");
        }

        Client client = clientRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "username", username));

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse getClientByEmail(String email) {
        log.debug("Récupération du client par email: {}", email);

        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("Format d'email invalide: " + email);
        }

        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "email", email));

        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse getClientByCode(String codeClient) {
        log.debug("Récupération du client par code: {}", codeClient);

        if (ValidationUtil.isBlank(codeClient)) {
            throw new ValidationException("Le code client est requis");
        }

        Client client = clientRepository.findByCodeClient(codeClient)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "codeClient", codeClient));

        return clientMapper.toResponse(client);
    }

    @Override
    public Page<ClientResponse> getAllClients(Pageable pageable) {
        log.debug("Récupération de tous les clients avec pagination");

        Page<Client> clientsPage = clientRepository.findAll(pageable);
        List<ClientResponse> responses = clientMapper.toResponseList(clientsPage.getContent());

        return new PageImpl<>(responses, pageable, clientsPage.getTotalElements());
    }

    @Override
    public List<ClientResponse> getAllActiveClients() {
        log.debug("Récupération de tous les clients actifs");

        List<Client> clients = clientRepository.findAllActiveClients();
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> getClientsByType(TypeClient typeClient) {
        log.debug("Récupération des clients par type: {}", typeClient);

        if (typeClient == null) {
            throw new ValidationException("Le type de client est requis");
        }

        List<Client> clients = clientRepository.findByTypeClient(typeClient);
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> getClientsByCategorie(String categorie) {
        log.debug("Récupération des clients par catégorie: {}", categorie);

        if (ValidationUtil.isBlank(categorie)) {
            throw new ValidationException("La catégorie est requise");
        }

        List<Client> clients = clientRepository.findByCategorie(categorie);
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> searchClientsByUsername(String username) {
        log.debug("Recherche de clients par username: {}", username);

        if (ValidationUtil.isBlank(username)) {
            throw new ValidationException("Le nom d'utilisateur de recherche est requis");
        }

        List<Client> clients = clientRepository.findByUsernameContaining(username);
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> searchClientsByEmail(String email) {
        log.debug("Recherche de clients par email: {}", email);

        if (ValidationUtil.isBlank(email)) {
            throw new ValidationException("L'email de recherche est requis");
        }

        List<Client> clients = clientRepository.findByEmailContaining(email);
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> getClientsWithSoldeGreaterThan(Double solde) {
        log.debug("Récupération des clients avec solde supérieur à: {}", solde);

        if (solde == null) {
            throw new ValidationException("Le montant de solde est requis");
        }

        List<Client> clients = clientRepository.findClientsWithSoldeGreaterThan(solde);
        return clientMapper.toResponseList(clients);
    }

    @Override
    public List<ClientResponse> getClientsWithNegativeBalance() {
        log.debug("Récupération des clients avec solde négatif");

        List<Client> clients = clientRepository.findClientsWithNegativeBalance();
        return clientMapper.toResponseList(clients);
    }

    @Override
    @CacheEvict(value = CacheUtil.CLIENT_CACHE, key = "#id")
    public void deleteClient(UUID id) {
        log.info("Suppression du client: {}", id);

        Client client = findClientById(id);
        client.setActif(false);
        client.setUpdatedAt(LocalDateTime.now());

        clientRepository.save(client);
        log.info("Client désactivé avec succès: {}", id);

        cacheUtil.invalidateClientRelatedCaches(id);
    }

    @Override
    @CacheEvict(value = CacheUtil.CLIENT_CACHE, key = "#id")
    public ClientResponse toggleClientStatus(UUID id) {
        log.info("Changement du statut du client: {}", id);

        Client client = findClientById(id);
        client.setActif(!client.getActif());
        client.setUpdatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(client);
        log.info("Statut du client modifié avec succès: {} -> {}", id, savedClient.getActif());

        ClientResponse response = clientMapper.toResponse(savedClient);
        cacheUtil.cacheClient(id, response);

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.CLIENT_CACHE, key = "#id")
    public ClientResponse updateClientSolde(UUID id, Double nouveauSolde) {
        log.info("Mise à jour du solde du client {} à: {}", id, nouveauSolde);

        if (nouveauSolde == null) {
            throw new ValidationException("Le nouveau solde est requis");
        }

        Client client = findClientById(id);
        client.setSoldeCourant(nouveauSolde);
        client.setUpdatedAt(LocalDateTime.now());

        Client savedClient = clientRepository.save(client);
        log.info("Solde du client mis à jour avec succès: {}", id);

        ClientResponse response = clientMapper.toResponse(savedClient);
        cacheUtil.cacheClient(id, response);

        return response;
    }

    @Override
    public boolean existsByUsername(String username) {
        return ValidationUtil.isNotBlank(username) && clientRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return ValidationUtil.isValidEmail(email) && clientRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByCode(String codeClient) {
        return ValidationUtil.isNotBlank(codeClient) && clientRepository.existsByCodeClient(codeClient);
    }

    @Override
    public Long countActiveClients() {
        return clientRepository.countActiveClients();
    }

    @Override
    public Long countClientsByType(TypeClient typeClient) {
        if (typeClient == null) {
            return 0L;
        }
        return clientRepository.countClientsByType(typeClient);
    }

    // Méthodes privées utilitaires

    private Client findClientById(UUID id) {
        if (!ValidationUtil.isValidUuid(id)) {
            throw new ValidationException("ID client invalide");
        }

        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private void validateClientCreateRequest(ClientCreateRequest request) {
        if (!ValidationUtil.isNotBlank(request.getUsername())) {
            throw new ValidationException("Le nom d'utilisateur est obligatoire");
        }

        if (!ValidationUtil.hasValidLength(request.getUsername(), 2, 50)) {
            throw new ValidationException("Le nom d'utilisateur doit contenir entre 2 et 50 caractères");
        }

        if (request.getEmail() != null && !ValidationUtil.isValidEmail(request.getEmail())) {
            throw new ValidationException("Format d'email invalide");
        }

        if (request.getTelephone() != null && !ValidationUtil.isValidPhoneNumber(request.getTelephone())) {
            throw new ValidationException("Format de téléphone invalide");
        }

        if (request.getCodeClient() != null && !ValidationUtil.isValidCode(request.getCodeClient())) {
            throw new ValidationException("Format de code client invalide");
        }

        if (request.getNumeroTva() != null && !ValidationUtil.isValidTvaNumber(request.getNumeroTva())) {
            throw new ValidationException("Format de numéro TVA invalide");
        }

        // Vérification d'unicité
        if (existsByUsername(request.getUsername())) {
            throw new ValidationException("Un client avec ce nom d'utilisateur existe déjà");
        }

        if (request.getEmail() != null && existsByEmail(request.getEmail())) {
            throw new ValidationException("Un client avec cet email existe déjà");
        }

        if (request.getCodeClient() != null && existsByCode(request.getCodeClient())) {
            throw new ValidationException("Un client avec ce code existe déjà");
        }
    }

    private void validateClientUpdateRequest(ClientUpdateRequest request, Client existingClient) {
        if (request.getUsername() != null) {
            if (!ValidationUtil.hasValidLength(request.getUsername(), 2, 50)) {
                throw new ValidationException("Le nom d'utilisateur doit contenir entre 2 et 50 caractères");
            }

            if (!request.getUsername().equals(existingClient.getUsername()) &&
                    existsByUsername(request.getUsername())) {
                throw new ValidationException("Un client avec ce nom d'utilisateur existe déjà");
            }
        }

        if (request.getEmail() != null && !ValidationUtil.isValidEmail(request.getEmail())) {
            throw new ValidationException("Format d'email invalide");
        }

        if (request.getEmail() != null &&
                !request.getEmail().equals(existingClient.getEmail()) &&
                existsByEmail(request.getEmail())) {
            throw new ValidationException("Un client avec cet email existe déjà");
        }

        if (request.getTelephone() != null && !ValidationUtil.isValidPhoneNumber(request.getTelephone())) {
            throw new ValidationException("Format de téléphone invalide");
        }

        if (request.getCodeClient() != null && !ValidationUtil.isValidCode(request.getCodeClient())) {
            throw new ValidationException("Format de code client invalide");
        }

        if (request.getCodeClient() != null &&
                !request.getCodeClient().equals(existingClient.getCodeClient()) &&
                existsByCode(request.getCodeClient())) {
            throw new ValidationException("Un client avec ce code existe déjà");
        }

        if (request.getNumeroTva() != null && !ValidationUtil.isValidTvaNumber(request.getNumeroTva())) {
            throw new ValidationException("Format de numéro TVA invalide");
        }
    }
}