package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.DeviseCreateRequest;
import com.yooyob.erp.dto.request.DeviseUpdateRequest;
import com.yooyob.erp.dto.response.DeviseResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.DeviseMapper;
import com.yooyob.erp.model.entity.Devise;
import com.yooyob.erp.repository.DeviseRepository;
import com.yooyob.erp.service.DeviseService;
import com.yooyob.erp.util.ValidationUtil;
import com.yooyob.erp.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DeviseServiceImpl implements DeviseService {

    private final DeviseRepository deviseRepository;
    private final DeviseMapper deviseMapper;

    private static final String DEFAULT_DEVISE = "EUR";

    @Override
    public DeviseResponse createDevise(DeviseCreateRequest request) {
        log.info("Création d'une nouvelle devise: {}", request.getNomDevise());

        validateDeviseCreateRequest(request);

        Devise devise = deviseMapper.toEntity(request);
        devise.setCreatedAt(LocalDateTime.now());
        devise.setUpdatedAt(LocalDateTime.now());

        Devise savedDevise = deviseRepository.save(devise);
        log.info("Devise créée avec succès: {}", savedDevise.getIdDevise());

        return deviseMapper.toResponse(savedDevise);
    }

    @Override
    @CacheEvict(value = "devises", key = "#id")
    public DeviseResponse updateDevise(UUID id, DeviseUpdateRequest request) {
        log.info("Mise à jour de la devise: {}", id);

        Devise existingDevise = findDeviseById(id);
        validateDeviseUpdateRequest(request, existingDevise);

        deviseMapper.updateEntityFromRequest(request, existingDevise);
        existingDevise.setUpdatedAt(LocalDateTime.now());

        Devise savedDevise = deviseRepository.save(existingDevise);
        log.info("Devise mise à jour avec succès: {}", id);

        return deviseMapper.toResponse(savedDevise);
    }

    @Override
    @Cacheable(value = "devises", key = "#id")
    public DeviseResponse getDeviseById(UUID id) {
        log.debug("Récupération de la devise par ID: {}", id);

        Devise devise = findDeviseById(id);
        return deviseMapper.toResponse(devise);
    }

    @Override
    public DeviseResponse getDeviseByNom(String nomDevise) {
        log.debug("Récupération de la devise par nom: {}", nomDevise);

        if (ValidationUtil.isBlank(nomDevise)) {
            throw new ValidationException("Le nom de la devise est requis");
        }

        Devise devise = deviseRepository.findByNomDevise(nomDevise)
                .orElseThrow(() -> new ResourceNotFoundException("Devise", "nomDevise", nomDevise));

        return deviseMapper.toResponse(devise);
    }

    @Override
    public DeviseResponse getDeviseBySymbole(String symbole) {
        log.debug("Récupération de la devise par symbole: {}", symbole);

        if (ValidationUtil.isBlank(symbole)) {
            throw new ValidationException("Le symbole de la devise est requis");
        }

        Devise devise = deviseRepository.findBySymbole(symbole)
                .orElseThrow(() -> new ResourceNotFoundException("Devise", "symbole", symbole));

        return deviseMapper.toResponse(devise);
    }

    @Override
    public Page<DeviseResponse> getAllDevises(Pageable pageable) {
        log.debug("Récupération de toutes les devises avec pagination");

        Page<Devise> devisesPage = deviseRepository.findAll(pageable);
        List<DeviseResponse> responses = deviseMapper.toResponseList(devisesPage.getContent());

        return new PageImpl<>(responses, pageable, devisesPage.getTotalElements());
    }

    @Override
    @Cacheable(value = "devises", key = "'active_devises'")
    public List<DeviseResponse> getAllActiveDevises() {
        log.debug("Récupération de toutes les devises actives");

        List<Devise> devises = deviseRepository.findAllActiveDevises();
        return deviseMapper.toResponseList(devises);
    }

    @Override
    public List<DeviseResponse> searchDevisesByNom(String nomDevise) {
        log.debug("Recherche de devises par nom: {}", nomDevise);

        if (ValidationUtil.isBlank(nomDevise)) {
            throw new ValidationException("Le nom de devise pour la recherche est requis");
        }

        List<Devise> devises = deviseRepository.findByNomDeviseContaining(nomDevise);
        return deviseMapper.toResponseList(devises);
    }

    @Override
    @CacheEvict(value = "devises", key = "#id")
    public DeviseResponse toggleDeviseStatus(UUID id) {
        log.info("Changement du statut de la devise: {}", id);

        Devise devise = findDeviseById(id);
        devise.setActif(!devise.getActif());
        devise.setUpdatedAt(LocalDateTime.now());

        Devise savedDevise = deviseRepository.save(devise);
        log.info("Statut de la devise modifié avec succès: {} -> {}", id, savedDevise.getActif());

        return deviseMapper.toResponse(savedDevise);
    }

    @Override
    @CacheEvict(value = "devises", key = "#id")
    public DeviseResponse updateFacteurConversion(UUID id, BigDecimal nouveauFacteur) {
        log.info("Mise à jour du facteur de conversion de la devise {} à: {}", id, nouveauFacteur);

        if (!NumberUtil.isPositive(nouveauFacteur)) {
            throw new ValidationException("Le facteur de conversion doit être positif");
        }

        Devise devise = findDeviseById(id);
        devise.setFacteurConversion(nouveauFacteur);
        devise.setUpdatedAt(LocalDateTime.now());

        Devise savedDevise = deviseRepository.save(devise);
        log.info("Facteur de conversion mis à jour avec succès: {}", id);

        return deviseMapper.toResponse(savedDevise);
    }

    @Override
    @CacheEvict(value = "devises", key = "#id")
    public void deleteDevise(UUID id) {
        log.info("Suppression de la devise: {}", id);

        Devise devise = findDeviseById(id);

        // Vérifier si la devise peut être supprimée
        if (DEFAULT_DEVISE.equals(devise.getNomDevise()) || DEFAULT_DEVISE.equals(devise.getSymbole())) {
            throw new BusinessException("Impossible de supprimer la devise par défaut");
        }

        // Vérifier qu'aucune facture n'utilise cette devise
        // Cette vérification devrait être implémentée selon vos besoins

        deviseRepository.delete(devise);
        log.info("Devise supprimée avec succès: {}", id);
    }

    @Override
    public BigDecimal convertAmount(BigDecimal montant, String deviseSource, String deviseTarget) {
        log.debug("Conversion de {} de {} vers {}", montant, deviseSource, deviseTarget);

        if (!NumberUtil.isPositive(montant)) {
            return BigDecimal.ZERO;
        }

        if (deviseSource.equals(deviseTarget)) {
            return montant;
        }

        try {
            DeviseResponse source = getDeviseBySymbole(deviseSource);
            DeviseResponse target = getDeviseBySymbole(deviseTarget);

            // Convertir vers la devise de base puis vers la devise cible
            BigDecimal montantBase = NumberUtil.safeDivide(montant, source.getFacteurConversion());
            BigDecimal montantConverti = NumberUtil.safeMultiply(montantBase, target.getFacteurConversion());

            return NumberUtil.round(montantConverti);

        } catch (Exception e) {
            log.warn("Erreur lors de la conversion de devise: {}", e.getMessage());
            return montant; // Retourner le montant original en cas d'erreur
        }
    }

    @Override
    public BigDecimal convertToBaseCurrency(BigDecimal montant, String deviseSource) {
        return convertAmount(montant, deviseSource, DEFAULT_DEVISE);
    }

    @Override
    public BigDecimal convertFromBaseCurrency(BigDecimal montant, String deviseTarget) {
        return convertAmount(montant, DEFAULT_DEVISE, deviseTarget);
    }

    @Override
    public DeviseResponse getDefaultDevise() {
        log.debug("Récupération de la devise par défaut");

        try {
            return getDeviseBySymbole(DEFAULT_DEVISE);
        } catch (ResourceNotFoundException e) {
            // Créer la devise par défaut si elle n'existe pas
            return createDefaultDevise();
        }
    }

    @Override
    public DeviseResponse setDefaultDevise(UUID id) {
        log.info("Définition de la devise {} comme devise par défaut", id);

        // Cette fonctionnalité pourrait être implémentée avec une table de configuration
        // Pour l'instant, on retourne juste la devise demandée
        return getDeviseById(id);
    }

    @Override
    public boolean existsByNom(String nomDevise) {
        return ValidationUtil.isNotBlank(nomDevise) && deviseRepository.existsByNomDevise(nomDevise);
    }

    @Override
    public boolean existsBySymbole(String symbole) {
        return ValidationUtil.isNotBlank(symbole) && deviseRepository.existsBySymbole(symbole);
    }

    @Override
    public Long countActiveDevises() {
        return deviseRepository.countActiveDevises();
    }

    @Override
    public List<DeviseResponse> getMostUsedDevises(int limit) {
        log.debug("Récupération des {} devises les plus utilisées", limit);

        // Implementation simplifiée - récupérer les devises actives
        List<Devise> devises = deviseRepository.findAllActiveDevises();

        // Limiter le nombre de résultats
        List<Devise> limitedDevises = devises.stream()
                .limit(limit)
                .toList();

        return deviseMapper.toResponseList(limitedDevises);
    }

    @Override
    public void updateExchangeRatesFromExternalSource() {
        log.info("Mise à jour des taux de change depuis une source externe");

        // Cette fonctionnalité pourrait être implémentée avec une API externe
        // comme celle de la BCE ou d'un fournisseur de taux de change

        List<Devise> activeDevises = deviseRepository.findAllActiveDevises();

        for (Devise devise : activeDevises) {
            if (!DEFAULT_DEVISE.equals(devise.getSymbole())) {
                // Simuler la récupération d'un taux de change externe
                // BigDecimal newRate = fetchExchangeRateFromExternalAPI(devise.getSymbole());
                // devise.setFacteurConversion(newRate);
                devise.setUpdatedAt(LocalDateTime.now());
                deviseRepository.save(devise);
            }
        }

        log.info("Mise à jour des taux de change terminée");
    }

    // Méthodes privées utilitaires

    private Devise findDeviseById(UUID id) {
        if (!ValidationUtil.isValidUuid(id)) {
            throw new ValidationException("ID devise invalide");
        }

        return deviseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devise", id));
    }

    private void validateDeviseCreateRequest(DeviseCreateRequest request) {
        if (!ValidationUtil.isNotBlank(request.getNomDevise())) {
            throw new ValidationException("Le nom de la devise est obligatoire");
        }

        if (!ValidationUtil.isNotBlank(request.getSymbole())) {
            throw new ValidationException("Le symbole de la devise est obligatoire");
        }

        if (!ValidationUtil.hasValidLength(request.getSymbole(), 1, 5)) {
            throw new ValidationException("Le symbole doit contenir entre 1 et 5 caractères");
        }

        if (!NumberUtil.isPositive(request.getFacteurConversion())) {
            throw new ValidationException("Le facteur de conversion doit être positif");
        }

        // Vérification d'unicité
        if (existsByNom(request.getNomDevise())) {
            throw new ValidationException("Une devise avec ce nom existe déjà");
        }

        if (existsBySymbole(request.getSymbole())) {
            throw new ValidationException("Une devise avec ce symbole existe déjà");
        }
    }

    private void validateDeviseUpdateRequest(DeviseUpdateRequest request, Devise existingDevise) {
        if (request.getNomDevise() != null) {
            if (!ValidationUtil.hasValidLength(request.getNomDevise(), 1, 50)) {
                throw new ValidationException("Le nom de la devise doit contenir entre 1 et 50 caractères");
            }

            if (!request.getNomDevise().equals(existingDevise.getNomDevise()) &&
                    existsByNom(request.getNomDevise())) {
                throw new ValidationException("Une devise avec ce nom existe déjà");
            }
        }

        if (request.getSymbole() != null) {
            if (!ValidationUtil.hasValidLength(request.getSymbole(), 1, 5)) {
                throw new ValidationException("Le symbole doit contenir entre 1 et 5 caractères");
            }

            if (!request.getSymbole().equals(existingDevise.getSymbole()) &&
                    existsBySymbole(request.getSymbole())) {
                throw new ValidationException("Une devise avec ce symbole existe déjà");
            }
        }

        if (request.getFacteurConversion() != null && !NumberUtil.isPositive(request.getFacteurConversion())) {
            throw new ValidationException("Le facteur de conversion doit être positif");
        }
    }

    private DeviseResponse createDefaultDevise() {
        log.info("Création de la devise par défaut: {}", DEFAULT_DEVISE);

        DeviseCreateRequest defaultRequest = DeviseCreateRequest.builder()
                .nomDevise("Euro")
                .symbole(DEFAULT_DEVISE)
                .actif(true)
                .uniteMonetaire("Euro")
                .sousUniteMonetaire("Centime")
                .facteurConversion(BigDecimal.ONE)
                .nomMesure("Monétaire")
                .build();

        return createDevise(defaultRequest);
    }
}