package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.PaiementCreateRequest;
import com.yooyob.erp.dto.request.PaiementUpdateRequest;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.PaiementMapper;
import com.yooyob.erp.model.entity.Paiement;
import com.yooyob.erp.model.enums.TypePaiement;
import com.yooyob.erp.repository.PaiementRepository;
import com.yooyob.erp.service.PaiementService;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.EmailService;
import com.yooyob.erp.util.ValidationUtil;
import com.yooyob.erp.util.NumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
public class PaiementServiceImpl implements PaiementService {

    private final PaiementRepository paiementRepository;
    private final PaiementMapper paiementMapper;
    private final FactureService factureService;
    private final EmailService emailService;

    public PaiementServiceImpl(PaiementRepository paiementRepository, PaiementMapper paiementMapper,
                              @Lazy FactureService factureService, @Lazy EmailService emailService) {
        this.paiementRepository = paiementRepository;
        this.paiementMapper = paiementMapper;
        this.factureService = factureService;
        this.emailService = emailService;
    }

    @Override
    public PaiementResponse createPaiement(PaiementCreateRequest request) {
        log.info("Création d'un nouveau paiement pour le client: {}", request.getIdClient());

        validatePaiementCreateRequest(request);

        Paiement paiement = paiementMapper.toEntity(request);
        paiement.setCreatedAt(LocalDateTime.now());
        paiement.setUpdatedAt(LocalDateTime.now());

        Paiement savedPaiement = paiementRepository.save(paiement);
        log.info("Paiement créé avec succès: {}", savedPaiement.getIdPaiement());

        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    public PaiementResponse updatePaiement(UUID id, PaiementUpdateRequest request) {
        log.info("Mise à jour du paiement: {}", id);

        Paiement existingPaiement = findPaiementById(id);
        validatePaiementUpdateRequest(request);

        paiementMapper.updateEntityFromRequest(request, existingPaiement);
        existingPaiement.setUpdatedAt(LocalDateTime.now());

        Paiement savedPaiement = paiementRepository.save(existingPaiement);
        log.info("Paiement mis à jour avec succès: {}", id);

        return paiementMapper.toResponse(savedPaiement);
    }

    @Override
    public PaiementResponse getPaiementById(UUID id) {
        log.debug("Récupération du paiement par ID: {}", id);

        Paiement paiement = findPaiementById(id);
        return paiementMapper.toResponse(paiement);
    }

    @Override
    public Page<PaiementResponse> getAllPaiements(Pageable pageable) {
        log.debug("Récupération de tous les paiements avec pagination");

        Page<Paiement> paiementsPage = paiementRepository.findAll(pageable);
        List<PaiementResponse> responses = paiementMapper.toResponseList(paiementsPage.getContent());

        return new PageImpl<>(responses, pageable, paiementsPage.getTotalElements());
    }

    @Override
    public List<PaiementResponse> getPaiementsByClient(UUID clientId) {
        log.debug("Récupération des paiements du client: {}", clientId);

        if (!ValidationUtil.isValidUuid(clientId)) {
            throw new ValidationException("ID client invalide");
        }

        List<Paiement> paiements = paiementRepository.findByIdClient(clientId);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByFacture(UUID factureId) {
        log.debug("Récupération des paiements de la facture: {}", factureId);

        if (!ValidationUtil.isValidUuid(factureId)) {
            throw new ValidationException("ID facture invalide");
        }

        List<Paiement> paiements = paiementRepository.findByIdFacture(factureId);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByModePaiement(TypePaiement modePaiement) {
        log.debug("Récupération des paiements par mode: {}", modePaiement);

        if (modePaiement == null) {
            throw new ValidationException("Le mode de paiement est requis");
        }

        List<Paiement> paiements = paiementRepository.findByModePaiement(modePaiement);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByJournal(String journal) {
        log.debug("Récupération des paiements par journal: {}", journal);

        if (ValidationUtil.isBlank(journal)) {
            throw new ValidationException("Le journal est requis");
        }

        List<Paiement> paiements = paiementRepository.findByJournal(journal);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByPeriode(LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des paiements pour la période {} - {}", startDate, endDate);

        validateDateRange(startDate, endDate);

        List<Paiement> paiements = paiementRepository.findByDateBetween(startDate, endDate);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByMontant(BigDecimal minAmount, BigDecimal maxAmount) {
        log.debug("Récupération des paiements par montant: {} - {}", minAmount, maxAmount);

        if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
            throw new ValidationException("Le montant minimum ne peut pas être supérieur au montant maximum");
        }

        List<Paiement> paiements = paiementRepository.findByMontantBetween(minAmount, maxAmount);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByClientAndPeriode(UUID clientId, LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des paiements du client {} pour la période {} - {}", clientId, startDate, endDate);

        if (!ValidationUtil.isValidUuid(clientId)) {
            throw new ValidationException("ID client invalide");
        }
        validateDateRange(startDate, endDate);

        List<Paiement> paiements = paiementRepository.findByClientAndDateBetween(clientId, startDate, endDate);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByFactureOrderByDate(UUID factureId) {
        log.debug("Récupération des paiements de la facture {} triés par date", factureId);

        if (!ValidationUtil.isValidUuid(factureId)) {
            throw new ValidationException("ID facture invalide");
        }

        List<Paiement> paiements = paiementRepository.findByFactureOrderByDateDesc(factureId);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public List<PaiementResponse> getPaiementsByModePaiementAndPeriode(TypePaiement modePaiement, LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des paiements par mode {} pour la période {} - {}", modePaiement, startDate, endDate);

        if (modePaiement == null) {
            throw new ValidationException("Le mode de paiement est requis");
        }
        validateDateRange(startDate, endDate);

        List<Paiement> paiements = paiementRepository.findByModePaiementAndDateBetween(modePaiement, startDate, endDate);
        return paiementMapper.toResponseList(paiements);
    }

    @Override
    public void deletePaiement(UUID id) {
        log.info("Suppression du paiement: {}", id);

        Paiement paiement = findPaiementById(id);

        // Vérifier si le paiement peut être supprimé (règles métier)
        // Par exemple, ne pas permettre la suppression si la facture est clôturée

        paiementRepository.delete(paiement);
        log.info("Paiement supprimé avec succès: {}", id);
    }

    @Override
    public BigDecimal getTotalPaiementsByClient(UUID clientId) {
        log.debug("Calcul du total des paiements pour le client: {}", clientId);

        if (!ValidationUtil.isValidUuid(clientId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = paiementRepository.sumMontantByClient(clientId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalPaiementsByFacture(UUID factureId) {
        log.debug("Calcul du total des paiements pour la facture: {}", factureId);

        if (!ValidationUtil.isValidUuid(factureId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = paiementRepository.sumMontantByFacture(factureId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getTotalPaiementsByPeriode(LocalDate startDate, LocalDate endDate) {
        log.debug("Calcul du total des paiements pour la période {} - {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = paiementRepository.sumMontantByDateBetween(startDate, endDate);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    public Long countPaiementsByClient(UUID clientId) {
        if (!ValidationUtil.isValidUuid(clientId)) {
            return 0L;
        }
        return paiementRepository.countByIdClient(clientId);
    }

    @Override
    public Long countPaiementsByModePaiement(TypePaiement modePaiement) {
        if (modePaiement == null) {
            return 0L;
        }
        return paiementRepository.countByModePaiement(modePaiement);
    }

    @Override
    public Long countPaiementsByPeriode(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0L;
        }
        return paiementRepository.countByDateBetween(startDate, endDate);
    }

    @Override
    public boolean canPayFacture(UUID factureId, BigDecimal montantPaiement) {
        log.debug("Vérification si le paiement de {} est possible pour la facture {}", montantPaiement, factureId);

        if (!ValidationUtil.isValidUuid(factureId) || !NumberUtil.isPositive(montantPaiement)) {
            return false;
        }

        try {
            var facture = factureService.getFactureById(factureId);
            return ValidationUtil.isValidPaymentAmount(montantPaiement, facture.getMontantRestant());
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification de paiement pour la facture {}: {}", factureId, e.getMessage());
            return false;
        }
    }

    @Override
    public PaiementResponse traiterPaiementFacture(UUID factureId, PaiementCreateRequest request) {
        log.info("Traitement du paiement pour la facture: {}", factureId);

        if (!ValidationUtil.isValidUuid(factureId)) {
            throw new ValidationException("ID facture invalide");
        }

        // Vérifier que le paiement est valide
        if (!canPayFacture(factureId, request.getMontant())) {
            throw new BusinessException("Le montant du paiement dépasse le montant restant dû");
        }

        // Créer le paiement
        request.setIdFacture(factureId);
        PaiementResponse paiement = createPaiement(request);

        // Mettre à jour le montant restant de la facture
        factureService.updateMontantRestant(factureId, request.getMontant());

        // Envoyer une confirmation par email si l'email du client est disponible
        try {
            var facture = factureService.getFactureById(factureId);
            if (ValidationUtil.isValidEmail(facture.getEmailClient())) {
                emailService.sendPaiementConfirmationEmail(paiement.getIdPaiement(), facture.getEmailClient());
            }
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'email de confirmation de paiement: {}", e.getMessage());
        }

        log.info("Paiement traité avec succès pour la facture: {}", factureId);
        return paiement;
    }

    // Méthodes privées utilitaires

    private Paiement findPaiementById(UUID id) {
        if (!ValidationUtil.isValidUuid(id)) {
            throw new ValidationException("ID paiement invalide");
        }

        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }

    private void validatePaiementCreateRequest(PaiementCreateRequest request) {
        if (!ValidationUtil.isValidUuid(request.getIdClient())) {
            throw new ValidationException("ID client invalide");
        }

        if (!NumberUtil.isPositive(request.getMontant())) {
            throw new ValidationException("Le montant doit être positif");
        }

        if (request.getDate() == null) {
            throw new ValidationException("La date du paiement est obligatoire");
        }

        if (request.getDate().isAfter(LocalDate.now())) {
            throw new ValidationException("La date du paiement ne peut pas être dans le futur");
        }

        if (request.getModePaiement() == null) {
            throw new ValidationException("Le mode de paiement est obligatoire");
        }

        if (ValidationUtil.isBlank(request.getJournal())) {
            throw new ValidationException("Le journal est obligatoire");
        }

        // Vérifier que le client existe
        // clientService.getClientById(request.getIdClient());

        // Vérifier que la facture existe si spécifiée
        if (request.getIdFacture() != null) {
            factureService.getFactureById(request.getIdFacture());
        }
    }

    private void validatePaiementUpdateRequest(PaiementUpdateRequest request) {
        if (request.getMontant() != null && !NumberUtil.isPositive(request.getMontant())) {
            throw new ValidationException("Le montant doit être positif");
        }

        if (request.getDate() != null && request.getDate().isAfter(LocalDate.now())) {
            throw new ValidationException("La date du paiement ne peut pas être dans le futur");
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
}