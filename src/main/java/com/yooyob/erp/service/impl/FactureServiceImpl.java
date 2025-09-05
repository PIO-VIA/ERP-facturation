package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.request.FactureUpdateRequest;
import com.yooyob.erp.dto.response.FactureResponse;
import com.yooyob.erp.dto.response.FactureDetailsResponse;
import com.yooyob.erp.dto.response.ClientResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.FactureMapper;
import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.enums.StatutFacture;
import com.yooyob.erp.repository.FactureRepository;
import com.yooyob.erp.repository.ClientRepository;
import com.yooyob.erp.repository.custom.CustomFactureRepository;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.EmailService;
import com.yooyob.erp.service.PdfService;
import com.yooyob.erp.util.CacheUtil;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;

@Service
@Slf4j
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final CustomFactureRepository customFactureRepository;
    private final FactureMapper factureMapper;
    private final EmailService emailService;
    private final PdfService pdfService;
    private final CacheUtil cacheUtil;

    public FactureServiceImpl(FactureRepository factureRepository, ClientRepository clientRepository, CustomFactureRepository customFactureRepository, FactureMapper factureMapper, @Lazy EmailService emailService, PdfService pdfService, CacheUtil cacheUtil) {
        this.factureRepository = factureRepository;
        this.clientRepository = clientRepository;
        this.customFactureRepository = customFactureRepository;
        this.factureMapper = factureMapper;
        this.emailService = emailService;
        this.pdfService = pdfService;
        this.cacheUtil = cacheUtil;
    }

    @Override
    public FactureResponse createFacture(FactureCreateRequest request) {
        log.info("Création d'une nouvelle facture pour le client: {}", request.getIdClient());

        validateFactureCreateRequest(request);

        // Récupérer les informations du client
        Client client = clientRepository.findById(request.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client", request.getIdClient()));

        Facture facture = factureMapper.toEntity(request);

        // Remplir les informations client dans la facture
        populateClientInfo(facture, client);

        // Calculer les montants
        calculateFactureMontants(facture);

        facture.setCreatedAt(LocalDateTime.now());
        facture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(facture);
        log.info("Facture créée avec succès: {}", savedFacture.getNumeroFacture());

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.cacheFacture(savedFacture.getIdFacture(), response);

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse updateFacture(UUID id, FactureUpdateRequest request) {
        log.info("Mise à jour de la facture: {}", id);

        Facture existingFacture = findFactureById(id);
        validateFactureUpdateRequest(request, existingFacture);

        factureMapper.updateEntityFromRequest(request, existingFacture);

        // Recalculer les montants si les lignes ont changé
        if (request.getLignesFacture() != null) {
            calculateFactureMontants(existingFacture);
        }

        existingFacture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(existingFacture);
        log.info("Facture mise à jour avec succès: {}", id);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.invalidateFactureRelatedCaches(id, savedFacture.getIdClient());

        return response;
    }

    @Override
    @Cacheable(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse getFactureById(UUID id) {
        log.debug("Récupération de la facture par ID: {}", id);

        Facture facture = findFactureById(id);
        return factureMapper.toResponse(facture);
    }

    @Override
    public FactureDetailsResponse getFactureDetails(UUID id) {
        log.debug("Récupération des détails de la facture: {}", id);

        Facture facture = findFactureById(id);
        Client client = clientRepository.findById(facture.getIdClient()).orElse(null);

        FactureDetailsResponse response = factureMapper.toDetailsResponse(facture);

        if (client != null) {
            // Mapper le client - vous pouvez créer un ClientMapper si nécessaire
            // response.setClient(clientMapper.toResponse(client));
        }

        return response;
    }

    @Override
    public FactureResponse getFactureByNumero(String numeroFacture) {
        log.debug("Récupération de la facture par numéro: {}", numeroFacture);

        if (ValidationUtil.isBlank(numeroFacture)) {
            throw new ValidationException("Le numéro de facture est requis");
        }

        Facture facture = factureRepository.findByNumeroFacture(numeroFacture)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", "numeroFacture", numeroFacture));

        return factureMapper.toResponse(facture);
    }

    @Override
    public Page<FactureResponse> getAllFactures(Pageable pageable) {
        log.debug("Récupération de toutes les factures avec pagination");

        Page<Facture> facturesPage = factureRepository.findAll(pageable);
        List<FactureResponse> responses = factureMapper.toResponseList(facturesPage.getContent());

        return new PageImpl<>(responses, pageable, facturesPage.getTotalElements());
    }

    @Override
    public List<FactureResponse> getFacturesByClient(UUID clientId) {
        log.debug("Récupération des factures du client: {}", clientId);

        if (!ValidationUtil.isValidUuid(clientId)) {
            throw new ValidationException("ID client invalide");
        }

        List<Facture> factures = factureRepository.findByIdClient(clientId);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesByEtat(StatutFacture etat) {
        log.debug("Récupération des factures par état: {}", etat);

        if (etat == null) {
            throw new ValidationException("L'état de la facture est requis");
        }

        List<Facture> factures = factureRepository.findByEtat(etat);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesByClientAndEtat(UUID clientId, StatutFacture etat) {
        log.debug("Récupération des factures du client {} avec état: {}", clientId, etat);

        if (!ValidationUtil.isValidUuid(clientId)) {
            throw new ValidationException("ID client invalide");
        }
        if (etat == null) {
            throw new ValidationException("L'état de la facture est requis");
        }

        List<Facture> factures = factureRepository.findByClientAndEtat(clientId, etat);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesByPeriode(LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des factures pour la période {} - {}", startDate, endDate);

        validateDateRange(startDate, endDate);

        List<Facture> factures = factureRepository.findByDateFacturationBetween(startDate, endDate);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesByMontant(BigDecimal minAmount, BigDecimal maxAmount) {
        log.debug("Récupération des factures par montant: {} - {}", minAmount, maxAmount);

        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new ValidationException("Le montant minimum ne peut pas être supérieur au montant maximum");
        }

        List<Facture> factures = factureRepository.findByMontantTotalBetween(minAmount, maxAmount);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesEnRetard() {
        log.debug("Récupération des factures en retard");

        List<Facture> factures = factureRepository.findOverdueFactures(LocalDate.now());
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesImpayes() {
        log.debug("Récupération des factures impayées");

        List<Facture> factures = factureRepository.findUnpaidFactures();
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesByDevise(String devise) {
        log.debug("Récupération des factures par devise: {}", devise);

        if (ValidationUtil.isBlank(devise)) {
            throw new ValidationException("La devise est requise");
        }

        List<Facture> factures = factureRepository.findByDevise(devise);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> getFacturesEnvoyeesParEmail(Boolean envoyees) {
        log.debug("Récupération des factures envoyées par email: {}", envoyees);

        List<Facture> factures = factureRepository.findByEnvoyeParEmail(envoyees);
        return factureMapper.toResponseList(factures);
    }

    @Override
    public List<FactureResponse> searchFactures(UUID clientId, StatutFacture etat, LocalDate dateStart,
                                                LocalDate dateEnd, BigDecimal montantMin, BigDecimal montantMax,
                                                String devise) {
        log.debug("Recherche de factures avec filtres multiples");

        List<Facture> factures = customFactureRepository.findFacturesWithFilters(
                clientId, etat, dateStart, dateEnd, montantMin, montantMax, devise);

        return factureMapper.toResponseList(factures);
    }

    @Override
    public Page<FactureResponse> searchFacturesWithPagination(UUID clientId, StatutFacture etat,
                                                              LocalDate dateStart, LocalDate dateEnd,
                                                              BigDecimal montantMin, BigDecimal montantMax,
                                                              String devise, Pageable pageable) {
        log.debug("Recherche paginée de factures avec filtres multiples");

        Page<Facture> facturesPage = customFactureRepository.findFacturesWithFiltersAndPagination(
                clientId, etat, dateStart, dateEnd, montantMin, montantMax, devise, pageable);

        List<FactureResponse> responses = factureMapper.toResponseList(facturesPage.getContent());
        return new PageImpl<>(responses, pageable, facturesPage.getTotalElements());
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse changeStatutFacture(UUID id, StatutFacture nouveauStatut) {
        log.info("Changement du statut de la facture {} vers: {}", id, nouveauStatut);

        if (nouveauStatut == null) {
            throw new ValidationException("Le nouveau statut est requis");
        }

        Facture facture = findFactureById(id);
        validateStatutChange(facture.getEtat(), nouveauStatut);

        facture.setEtat(nouveauStatut);
        facture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(facture);
        log.info("Statut de la facture modifié avec succès: {}", id);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.invalidateFactureRelatedCaches(id, savedFacture.getIdClient());

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse marquerEnvoyeeParEmail(UUID id) {
        log.info("Marquage de la facture {} comme envoyée par email", id);

        Facture facture = findFactureById(id);
        facture.setEnvoyeParEmail(true);
        facture.setDateEnvoiEmail(LocalDateTime.now());
        facture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(facture);
        log.info("Facture marquée comme envoyée par email: {}", id);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.cacheFacture(id, response);

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse calculerMontantsFacture(UUID id) {
        log.info("Calcul des montants de la facture: {}", id);

        Facture facture = findFactureById(id);
        calculateFactureMontants(facture);
        facture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(facture);
        log.info("Montants de la facture calculés: {}", id);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.cacheFacture(id, response);

        return response;
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public FactureResponse updateMontantRestant(UUID id, BigDecimal montantPaye) {
        log.info("Mise à jour du montant restant de la facture {} avec paiement: {}", id, montantPaye);

        if (!NumberUtil.isPositive(montantPaye)) {
            throw new ValidationException("Le montant payé doit être positif");
        }

        Facture facture = findFactureById(id);
        BigDecimal nouveauMontantRestant = NumberUtil.safeSubtract(facture.getMontantRestant(), montantPaye);

        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Le montant payé ne peut pas dépasser le montant restant dû");
        }

        facture.setMontantRestant(nouveauMontantRestant);

        // Mettre à jour le statut selon le montant restant
        if (nouveauMontantRestant.compareTo(BigDecimal.ZERO) == 0) {
            facture.setEtat(StatutFacture.PAYE);
        } else if (nouveauMontantRestant.compareTo(facture.getMontantTotal()) < 0) {
            facture.setEtat(StatutFacture.PARTIELLEMENT_PAYE);
        }

        facture.setUpdatedAt(LocalDateTime.now());

        Facture savedFacture = factureRepository.save(facture);
        log.info("Montant restant mis à jour: {}", id);

        FactureResponse response = factureMapper.toResponse(savedFacture);
        cacheUtil.invalidateFactureRelatedCaches(id, savedFacture.getIdClient());

        return response;
    }

    @Override
    public String genererPdfFacture(UUID id) {
        log.info("Génération du PDF pour la facture: {}", id);

        Facture facture = findFactureById(id);
        String pdfPath = pdfService.generateAndSaveFacturePdf(id);

        // Mettre à jour le chemin du PDF dans la facture
        facture.setPdfPath(pdfPath);
        facture.setUpdatedAt(LocalDateTime.now());
        factureRepository.save(facture);

        log.info("PDF généré pour la facture {}: {}", id, pdfPath);
        return pdfPath;
    }

    @Override
    public void envoyerFactureParEmail(UUID id) {
        log.info("Envoi de la facture {} par email", id);

        Facture facture = findFactureById(id);

        if (ValidationUtil.isBlank(facture.getEmailClient())) {
            throw new BusinessException("L'adresse email du client est requise pour envoyer la facture");
        }

        // Générer le PDF s'il n'existe pas
        String pdfPath = facture.getPdfPath();
        if (ValidationUtil.isBlank(pdfPath) || !pdfService.pdfFileExists(pdfPath)) {
            pdfPath = genererPdfFacture(id);
        }

        // Envoyer l'email avec le PDF
        emailService.sendFacturePdfEmail(id, facture.getEmailClient(), pdfPath);

        log.info("Facture {} envoyée par email avec succès", id);
    }

    @Override
    @CacheEvict(value = CacheUtil.FACTURE_CACHE, key = "#id")
    public void deleteFacture(UUID id) {
        log.info("Suppression de la facture: {}", id);

        Facture facture = findFactureById(id);

        // Vérifier si la facture peut être supprimée
        if (facture.getEtat() == StatutFacture.PAYE || facture.getEtat() == StatutFacture.PARTIELLEMENT_PAYE) {
            throw new BusinessException("Impossible de supprimer une facture payée ou partiellement payée");
        }

        factureRepository.delete(facture);
        log.info("Facture supprimée avec succès: {}", id);

        cacheUtil.invalidateFactureRelatedCaches(id, facture.getIdClient());
    }

    @Override
    public FactureResponse dupliquerFacture(UUID id) {
        log.info("Duplication de la facture: {}", id);

        Facture originalFacture = findFactureById(id);

        Facture nouvelleFacture = Facture.builder()
                .idClient(originalFacture.getIdClient())
                .nomClient(originalFacture.getNomClient())
                .adresseClient(originalFacture.getAdresseClient())
                .emailClient(originalFacture.getEmailClient())
                .telephoneClient(originalFacture.getTelephoneClient())
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .type(originalFacture.getType())
                .etat(StatutFacture.BROUILLON)
                .lignesFacture(originalFacture.getLignesFacture())
                .devise(originalFacture.getDevise())
                .tauxChange(originalFacture.getTauxChange())
                .conditionsPaiement(originalFacture.getConditionsPaiement())
                .notes(originalFacture.getNotes())
                .envoyeParEmail(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Générer un nouveau numéro de facture
        nouvelleFacture.setNumeroFacture(factureMapper.generateNumeroFacture());

        // Calculer les montants
        calculateFactureMontants(nouvelleFacture);

        Facture savedFacture = factureRepository.save(nouvelleFacture);
        log.info("Facture dupliquée avec succès: {} -> {}", id, savedFacture.getIdFacture());

        return factureMapper.toResponse(savedFacture);
    }

    @Override
    public boolean existsByNumero(String numeroFacture) {
        return ValidationUtil.isNotBlank(numeroFacture) && factureRepository.existsByNumeroFacture(numeroFacture);
    }

    @Override
    public Long countFacturesByEtat(StatutFacture etat) {
        if (etat == null) {
            return 0L;
        }
        return factureRepository.countByEtat(etat);
    }

    @Override
    public Long countFacturesByClient(UUID clientId) {
        if (!ValidationUtil.isValidUuid(clientId)) {
            return 0L;
        }
        return factureRepository.countByIdClient(clientId);
    }

    @Override
    public Map<String, Object> getFactureStatistics(LocalDate startDate, LocalDate endDate) {
        log.debug("Génération des statistiques de factures pour la période {} - {}", startDate, endDate);
        return customFactureRepository.getFactureStatisticsByPeriod(startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> getChiffreAffairesByMonth(int year) {
        log.debug("Calcul du chiffre d'affaires par mois pour l'année {}", year);
        return customFactureRepository.getChiffreAffairesByMonth(year);
    }

    @Override
    public List<Map<String, Object>> getTopClientsByChiffreAffaires(int limit) {
        log.debug("Récupération du top {} clients par chiffre d'affaires", limit);
        return customFactureRepository.getTopClientsByChiffreAffaires(limit);
    }

    @Override
    public List<FactureResponse> getFacturesApprochantEcheance(int nombreJours) {
        log.debug("Récupération des factures approchant de l'échéance dans {} jours", nombreJours);

        List<Facture> factures = customFactureRepository.getFacturesApprochantEcheance(nombreJours);
        return factureMapper.toResponseList(factures);
    }

    // Méthodes privées utilitaires

    private Facture findFactureById(UUID id) {
        if (!ValidationUtil.isValidUuid(id)) {
            throw new ValidationException("ID facture invalide");
        }

        return factureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture", id));
    }

    private void validateFactureCreateRequest(FactureCreateRequest request) {
        if (!ValidationUtil.isValidUuid(request.getIdClient())) {
            throw new ValidationException("ID client invalide");
        }

        if (request.getDateFacturation() == null) {
            throw new ValidationException("La date de facturation est obligatoire");
        }

        if (request.getDateEcheance() == null) {
            throw new ValidationException("La date d'échéance est obligatoire");
        }

        if (!ValidationUtil.isValidDueDate(request.getDateFacturation(), request.getDateEcheance())) {
            throw new ValidationException("La date d'échéance doit être postérieure à la date de facturation");
        }

        if (ValidationUtil.isEmpty(request.getLignesFacture())) {
            throw new ValidationException("Au moins une ligne de facture est requise");
        }

        // Valider chaque ligne de facture
        request.getLignesFacture().forEach(ligne -> {
            if (!ValidationUtil.isPositiveInteger(ligne.getQuantite())) {
                throw new ValidationException("La quantité doit être positive");
            }
            if (ligne.getPrixUnitaire() == null || ligne.getPrixUnitaire().compareTo(BigDecimal.ZERO) < 0) {
                throw new ValidationException("Le prix unitaire doit être positif ou nul");
            }
        });
    }

    private void validateFactureUpdateRequest(FactureUpdateRequest request, Facture existingFacture) {
        if (request.getDateFacturation() != null && request.getDateEcheance() != null) {
            if (!ValidationUtil.isValidDueDate(request.getDateFacturation(), request.getDateEcheance())) {
                throw new ValidationException("La date d'échéance doit être postérieure à la date de facturation");
            }
        }

        // Ne pas permettre la modification d'une facture payée
        if (existingFacture.getEtat() == StatutFacture.PAYE) {
            throw new BusinessException("Impossible de modifier une facture payée");
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Les dates de début et de fin sont requises");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("La date de début ne peut pas être postérieure à la date de fin");
        }
    }

    private void validateStatutChange(StatutFacture currentStatut, StatutFacture newStatut) {
        // Logique métier pour les transitions de statut autorisées
        if (currentStatut == StatutFacture.PAYE && newStatut != StatutFacture.PAYE) {
            throw new BusinessException("Impossible de changer le statut d'une facture payée");
        }

        if (currentStatut == StatutFacture.ANNULE) {
            throw new BusinessException("Impossible de modifier une facture annulée");
        }
    }

    private void populateClientInfo(Facture facture, Client client) {
        facture.setNomClient(client.getUsername());
        facture.setAdresseClient(client.getAdresse());
        facture.setEmailClient(client.getEmail());
        facture.setTelephoneClient(client.getTelephone());
    }

    private void calculateFactureMontants(Facture facture) {
        if (ValidationUtil.isEmpty(facture.getLignesFacture())) {
            facture.setMontantHT(BigDecimal.ZERO);
            facture.setMontantTVA(BigDecimal.ZERO);
            facture.setMontantTTC(BigDecimal.ZERO);
            facture.setMontantTotal(BigDecimal.ZERO);
            facture.setMontantRestant(BigDecimal.ZERO);
            return;
        }

        BigDecimal montantHT = facture.getLignesFacture().stream()
                .filter(ligne -> !ligne.getIsTaxLine())
                .map(ligne -> NumberUtil.safeMultiply(
                        ligne.getPrixUnitaire(),
                        BigDecimal.valueOf(ligne.getQuantite())
                ))
                .reduce(BigDecimal.ZERO, NumberUtil::safeAdd);

        BigDecimal montantTVA = facture.getLignesFacture().stream()
                .filter(ligne -> ligne.getIsTaxLine())
                .map(ligne -> NumberUtil.safeMultiply(
                        ligne.getPrixUnitaire(),
                        BigDecimal.valueOf(ligne.getQuantite())
                ))
                .reduce(BigDecimal.ZERO, NumberUtil::safeAdd);

        BigDecimal montantTTC = NumberUtil.safeAdd(montantHT, montantTVA);

        facture.setMontantHT(NumberUtil.round(montantHT));
        facture.setMontantTVA(NumberUtil.round(montantTVA));
        facture.setMontantTTC(NumberUtil.round(montantTTC));
        facture.setMontantTotal(NumberUtil.round(montantTTC));

        // Si c'est une nouvelle facture, le montant restant = montant total
        if (facture.getMontantRestant() == null) {
            facture.setMontantRestant(facture.getMontantTotal());
        }
    }
}