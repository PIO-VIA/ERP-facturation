package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.TaxeCreateRequest;
import com.yooyob.erp.dto.request.TaxeUpdateRequest;
import com.yooyob.erp.dto.response.TaxeResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.mapper.TaxeMapper;
import com.yooyob.erp.model.entity.Taxes;
import com.yooyob.erp.repository.TaxesRepository;
import com.yooyob.erp.service.TaxeService;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaxeServiceImpl implements TaxeService {

    private final TaxesRepository taxesRepository;
    private final TaxeMapper taxeMapper;

    @Override
    public TaxeResponse createTaxe(TaxeCreateRequest request) {
        log.info("Création d'une nouvelle taxe: {}", request.getNomTaxe());

        validateTaxeCreateRequest(request);

        Taxes taxe = taxeMapper.toEntity(request);
        taxe.setCreatedAt(LocalDateTime.now());
        taxe.setUpdatedAt(LocalDateTime.now());

        Taxes savedTaxe = taxesRepository.save(taxe);
        log.info("Taxe créée avec succès: {}", savedTaxe.getIdTaxe());

        return taxeMapper.toResponse(savedTaxe);
    }

    @Override
    @CacheEvict(value = "taxes", key = "#id")
    public TaxeResponse updateTaxe(UUID id, TaxeUpdateRequest request) {
        log.info("Mise à jour de la taxe: {}", id);

        Taxes existingTaxe = findTaxeById(id);
        validateTaxeUpdateRequest(request);

        taxeMapper.updateEntityFromRequest(request, existingTaxe);
        existingTaxe.setUpdatedAt(LocalDateTime.now());

        Taxes savedTaxe = taxesRepository.save(existingTaxe);
        log.info("Taxe mise à jour avec succès: {}", id);

        return taxeMapper.toResponse(savedTaxe);
    }

    @Override
    @Cacheable(value = "taxes", key = "#id")
    public TaxeResponse getTaxeById(UUID id) {
        log.debug("Récupération de la taxe par ID: {}", id);

        Taxes taxe = findTaxeById(id);
        return taxeMapper.toResponse(taxe);
    }

    @Override
    public TaxeResponse getTaxeByNom(String nomTaxe) {
        log.debug("Récupération de la taxe par nom: {}", nomTaxe);

        if (ValidationUtil.isBlank(nomTaxe)) {
            throw new ValidationException("Le nom de la taxe est requis");
        }

        Taxes taxe = taxesRepository.findByNomTaxe(nomTaxe)
                .orElseThrow(() -> new ResourceNotFoundException("Taxe", "nomTaxe", nomTaxe));

        return taxeMapper.toResponse(taxe);
    }

    @Override
    public Page<TaxeResponse> getAllTaxes(Pageable pageable) {
        log.debug("Récupération de toutes les taxes avec pagination");

        Page<Taxes> taxesPage = taxesRepository.findAll(pageable);
        List<TaxeResponse> responses = taxeMapper.toResponseList(taxesPage.getContent());

        return new PageImpl<>(responses, pageable, taxesPage.getTotalElements());
    }

    @Override
    @Cacheable(value = "taxes", key = "'active_taxes'")
    public List<TaxeResponse> getAllActiveTaxes() {
        log.debug("Récupération de toutes les taxes actives");

        List<Taxes> taxes = taxesRepository.findAllActiveTaxes();
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getTaxesByType(String typeTaxe) {
        log.debug("Récupération des taxes par type: {}", typeTaxe);

        if (ValidationUtil.isBlank(typeTaxe)) {
            throw new ValidationException("Le type de taxe est requis");
        }

        List<Taxes> taxes = taxesRepository.findByTypeTaxe(typeTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getTaxesByPorte(String porteTaxe) {
        log.debug("Récupération des taxes par porte: {}", porteTaxe);

        if (ValidationUtil.isBlank(porteTaxe)) {
            throw new ValidationException("La porte de taxe est requise");
        }

        List<Taxes> taxes = taxesRepository.findByPorteTaxe(porteTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getTaxesByPositionFiscale(String positionFiscale) {
        log.debug("Récupération des taxes par position fiscale: {}", positionFiscale);

        if (ValidationUtil.isBlank(positionFiscale)) {
            throw new ValidationException("La position fiscale est requise");
        }

        List<Taxes> taxes = taxesRepository.findByPositionFiscale(positionFiscale);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getActiveTaxesByType(String typeTaxe) {
        log.debug("Récupération des taxes actives par type: {}", typeTaxe);

        if (ValidationUtil.isBlank(typeTaxe)) {
            throw new ValidationException("Le type de taxe est requis");
        }

        List<Taxes> taxes = taxesRepository.findActiveByTypeTaxe(typeTaxe);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getTaxesByTauxRange(BigDecimal minTaux, BigDecimal maxTaux) {
        log.debug("Récupération des taxes par plage de taux: {} - {}", minTaux, maxTaux);

        if (minTaux != null && maxTaux != null && minTaux.compareTo(maxTaux) > 0) {
            throw new ValidationException("Le taux minimum ne peut pas être supérieur au taux maximum");
        }

        List<Taxes> taxes = taxesRepository.findByCalculTaxeBetween(minTaux, maxTaux);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public List<TaxeResponse> getTaxesByMontantRange(BigDecimal minMontant, BigDecimal maxMontant) {
        log.debug("Récupération des taxes par plage de montant: {} - {}", minMontant, maxMontant);

        if (minMontant != null && maxMontant != null && minMontant.compareTo(maxMontant) > 0) {
            throw new ValidationException("Le montant minimum ne peut pas être supérieur au montant maximum");
        }

        List<Taxes> taxes = taxesRepository.findByMontantBetween(minMontant, maxMontant);
        return taxeMapper.toResponseList(taxes);
    }

    @Override
    @CacheEvict(value = "taxes", key = "#id")
    public TaxeResponse toggleTaxeStatus(UUID id) {
        log.info("Changement du statut de la taxe: {}", id);

        Taxes taxe = findTaxeById(id);
        taxe.setActif(!taxe.getActif());
        taxe.setUpdatedAt(LocalDateTime.now());

        Taxes savedTaxe = taxesRepository.save(taxe);
        log.info("Statut de la taxe modifié avec succès: {} -> {}", id, savedTaxe.getActif());

        return taxeMapper.toResponse(savedTaxe);
    }

    @Override
    @CacheEvict(value = "taxes", key = "#id")
    public TaxeResponse updateCalculTaxe(UUID id, BigDecimal nouveauTaux) {
        log.info("Mise à jour du taux de calcul de la taxe {} à: {}", id, nouveauTaux);

        if (!NumberUtil.isNonNegative(nouveauTaux)) {
            throw new ValidationException("Le taux de calcul doit être positif ou nul");
        }

        if (!ValidationUtil.isValidTaxRate(nouveauTaux)) {
            throw new ValidationException("Le taux de taxe doit être compris entre 0 et 100%");
        }

        Taxes taxe = findTaxeById(id);
        taxe.setCalculTaxe(nouveauTaux);
        taxe.setUpdatedAt(LocalDateTime.now());

        Taxes savedTaxe = taxesRepository.save(taxe);
        log.info("Taux de calcul mis à jour avec succès: {}", id);

        return taxeMapper.toResponse(savedTaxe);
    }

    @Override
    @CacheEvict(value = "taxes", key = "#id")
    public TaxeResponse updateMontantTaxe(UUID id, BigDecimal nouveauMontant) {
        log.info("Mise à jour du montant de la taxe {} à: {}", id, nouveauMontant);

        if (!NumberUtil.isNonNegative(nouveauMontant)) {
            throw new ValidationException("Le montant de la taxe doit être positif ou nul");
        }

        Taxes taxe = findTaxeById(id);
        taxe.setMontant(nouveauMontant);
        taxe.setUpdatedAt(LocalDateTime.now());

        Taxes savedTaxe = taxesRepository.save(taxe);
        log.info("Montant de la taxe mis à jour avec succès: {}", id);

        return taxeMapper.toResponse(savedTaxe);
    }

    @Override
    @CacheEvict(value = "taxes", key = "#id")
    public void deleteTaxe(UUID id) {
        log.info("Suppression de la taxe: {}", id);

        Taxes taxe = findTaxeById(id);

        // Vérifier si la taxe peut être supprimée
        // Cette vérification devrait être implémentée selon vos besoins
        // Par exemple, vérifier qu'aucune facture n'utilise cette taxe

        taxesRepository.delete(taxe);
        log.info("Taxe supprimée avec succès: {}", id);
    }

    @Override
    public BigDecimal calculerMontantTaxe(UUID taxeId, BigDecimal montantHT) {
        log.debug("Calcul du montant de taxe {} pour le montant HT: {}", taxeId, montantHT);

        if (!NumberUtil.isNonNegative(montantHT)) {
            return BigDecimal.ZERO;
        }

        Taxes taxe = findTaxeById(taxeId);
        if (!taxe.getActif()) {
            log.warn("Tentative de calcul avec une taxe inactive: {}", taxeId);
            return BigDecimal.ZERO;
        }

        return NumberUtil.safeMultiply(montantHT,
                NumberUtil.safeDivide(taxe.getCalculTaxe(), BigDecimal.valueOf(100)));
    }

    @Override
    public BigDecimal calculerMontantTTC(BigDecimal montantHT, UUID taxeId) {
        log.debug("Calcul du montant TTC pour HT: {} avec taxe: {}", montantHT, taxeId);

        BigDecimal montantTaxe = calculerMontantTaxe(taxeId, montantHT);
        return NumberUtil.safeAdd(montantHT, montantTaxe);
    }

    @Override
    public BigDecimal calculerMontantHT(BigDecimal montantTTC, UUID taxeId) {
        log.debug("Calcul du montant HT pour TTC: {} avec taxe: {}", montantTTC, taxeId);

        if (!NumberUtil.isNonNegative(montantTTC)) {
            return BigDecimal.ZERO;
        }

        Taxes taxe = findTaxeById(taxeId);
        if (!taxe.getActif()) {
            log.warn("Tentative de calcul avec une taxe inactive: {}", taxeId);
            return montantTTC;
        }

        BigDecimal diviseur = BigDecimal.ONE.add(
                NumberUtil.safeDivide(taxe.getCalculTaxe(), BigDecimal.valueOf(100)));

        return NumberUtil.safeDivide(montantTTC, diviseur);
    }

    @Override
    public BigDecimal appliquerMultiplesTaxes(BigDecimal montantHT, List<UUID> taxeIds) {
        log.debug("Application de multiples taxes au montant HT: {}", montantHT);

        if (!NumberUtil.isNonNegative(montantHT) || ValidationUtil.isEmpty(taxeIds)) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalTaxes = BigDecimal.ZERO;

        for (UUID taxeId : taxeIds) {
            try {
                BigDecimal montantTaxe = calculerMontantTaxe(taxeId, montantHT);
                totalTaxes = NumberUtil.safeAdd(totalTaxes, montantTaxe);
            } catch (Exception e) {
                log.warn("Erreur lors du calcul de la taxe {}: {}", taxeId, e.getMessage());
            }
        }

        return totalTaxes;
    }

    @Override
    public Map<UUID, BigDecimal> calculerRepartitionTaxes(BigDecimal montantHT, List<UUID> taxeIds) {
        log.debug("Calcul de la répartition des taxes pour le montant HT: {}", montantHT);

        Map<UUID, BigDecimal> repartition = new HashMap<>();

        if (!NumberUtil.isNonNegative(montantHT) || ValidationUtil.isEmpty(taxeIds)) {
            return repartition;
        }

        for (UUID taxeId : taxeIds) {
            try {
                BigDecimal montantTaxe = calculerMontantTaxe(taxeId, montantHT);
                repartition.put(taxeId, montantTaxe);
            } catch (Exception e) {
                log.warn("Erreur lors du calcul de la taxe {}: {}", taxeId, e.getMessage());
                repartition.put(taxeId, BigDecimal.ZERO);
            }
        }

        return repartition;
    }

    @Override
    public List<TaxeResponse> getTaxesParDefaut(String typeProduit) {
        log.debug("Récupération des taxes par défaut pour le type de produit: {}", typeProduit);

        // Implementation simplifiée - retourner les taxes actives les plus courantes
        // Cette logique pourrait être plus sophistiquée selon les besoins métier

        List<Taxes> taxes = taxesRepository.findAllActiveTaxes();

        // Filtrer par type de produit si nécessaire
        if (ValidationUtil.isNotBlank(typeProduit)) {
            taxes = taxes.stream()
                    .filter(taxe -> typeProduit.equalsIgnoreCase(taxe.getTypeTaxe()) ||
                            "GENERAL".equalsIgnoreCase(taxe.getTypeTaxe()))
                    .collect(Collectors.toList());
        }

        return taxeMapper.toResponseList(taxes);
    }

    @Override
    public boolean existsByNom(String nomTaxe) {
        return ValidationUtil.isNotBlank(nomTaxe) && taxesRepository.existsByNomTaxe(nomTaxe);
    }

    @Override
    public Long countActiveTaxes() {
        return taxesRepository.countActiveTaxes();
    }

    @Override
    public Long countTaxesByType(String typeTaxe) {
        if (ValidationUtil.isBlank(typeTaxe)) {
            return 0L;
        }
        return taxesRepository.countByTypeTaxe(typeTaxe);
    }

    @Override
    public List<String> getAvailableTaxTypes() {
        log.debug("Récupération des types de taxes disponibles");

        // Implementation simplifiée - retourner les types les plus courants
        // Cette liste pourrait être configurée dynamiquement
        return Arrays.asList(
                "TVA",
                "TAXE_ENVIRONNEMENTALE",
                "TAXE_LUXE",
                "TAXE_IMPORTATION",
                "TAXE_REGIONALE",
                "GENERAL"
        );
    }

    @Override
    public boolean isValidTaxRate(BigDecimal taux) {
        return ValidationUtil.isValidTaxRate(taux);
    }

    @Override
    public List<TaxeResponse> getMostUsedTaxes(int limit) {
        log.debug("Récupération des {} taxes les plus utilisées", limit);

        // Implementation simplifiée - récupérer les taxes actives les plus récentes
        List<Taxes> taxes = taxesRepository.findAllActiveTaxes();

        // Trier par date de création (plus récent en premier) et limiter
        List<Taxes> sortedTaxes = taxes.stream()
                .sorted((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());

        return taxeMapper.toResponseList(sortedTaxes);
    }

    // Méthodes privées utilitaires

    private Taxes findTaxeById(UUID id) {
        if (!ValidationUtil.isValidUuid(id)) {
            throw new ValidationException("ID taxe invalide");
        }

        return taxesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Taxe", id));
    }

    private void validateTaxeCreateRequest(TaxeCreateRequest request) {
        if (!ValidationUtil.isNotBlank(request.getNomTaxe())) {
            throw new ValidationException("Le nom de la taxe est obligatoire");
        }

        if (!ValidationUtil.hasValidLength(request.getNomTaxe(), 2, 100)) {
            throw new ValidationException("Le nom de la taxe doit contenir entre 2 et 100 caractères");
        }

        if (!NumberUtil.isNonNegative(request.getCalculTaxe())) {
            throw new ValidationException("Le calcul de la taxe doit être positif ou nul");
        }

        if (!ValidationUtil.isValidTaxRate(request.getCalculTaxe())) {
            throw new ValidationException("Le taux de taxe doit être compris entre 0 et 100%");
        }

        if (!ValidationUtil.isNotBlank(request.getTypeTaxe())) {
            throw new ValidationException("Le type de taxe est obligatoire");
        }

        if (request.getMontant() == null || request.getMontant().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Le montant de la taxe doit être positif ou nul");
        }

        // Vérification d'unicité
        if (existsByNom(request.getNomTaxe())) {
            throw new ValidationException("Une taxe avec ce nom existe déjà");
        }
    }

    private void validateTaxeUpdateRequest(TaxeUpdateRequest request) {
        if (request.getNomTaxe() != null &&
                !ValidationUtil.hasValidLength(request.getNomTaxe(), 2, 100)) {
            throw new ValidationException("Le nom de la taxe doit contenir entre 2 et 100 caractères");
        }

        if (request.getCalculTaxe() != null) {
            if (!NumberUtil.isNonNegative(request.getCalculTaxe())) {
                throw new ValidationException("Le calcul de la taxe doit être positif ou nul");
            }
            if (!ValidationUtil.isValidTaxRate(request.getCalculTaxe())) {
                throw new ValidationException("Le taux de taxe doit être compris entre 0 et 100%");
            }
        }

        if (request.getMontant() != null && request.getMontant().compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Le montant de la taxe doit être positif ou nul");
        }
    }
}