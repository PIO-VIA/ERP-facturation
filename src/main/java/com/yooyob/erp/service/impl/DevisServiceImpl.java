package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.DevisCreateRequest;
import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.response.DevisResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.DevisMapper;
import com.yooyob.erp.model.entity.Devis;
import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.entity.LigneDevis;
import com.yooyob.erp.model.enums.StatutDevis;
import com.yooyob.erp.model.enums.StatutFacture;
import com.yooyob.erp.repository.DevisRepository;
import com.yooyob.erp.repository.ClientRepository;
import com.yooyob.erp.service.DevisService;
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
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DevisServiceImpl implements DevisService {

    private final DevisRepository devisRepository;
    private final ClientRepository clientRepository;
    private final DevisMapper devisMapper;
    private final EmailService emailService;
    private final PdfService pdfService;
    @Lazy
    private final FactureService factureService;

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse createDevis(DevisCreateRequest request) {
        log.info("Création d'un nouveau devis pour le client: {}", request.getIdClient());

        ValidationUtil.validateNotNull(request, "La demande de création de devis ne peut pas être nulle");
        ValidationUtil.validateNotNull(request.getIdClient(), "L'ID client est obligatoire");

        Client client = clientRepository.findByIdClient(request.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + request.getIdClient()));

        Devis devis = devisMapper.toEntity(request);
        devis.setIdDevis(UUID.randomUUID());
        devis.setNumeroDevis(genererNumeroDevis());
        devis.setStatut(StatutDevis.BROUILLON);
        devis.setCreatedAt(LocalDateTime.now());
        devis.setUpdatedAt(LocalDateTime.now());

        populateClientInfo(devis, client);
        calculerTotauxDevis(devis);

        devis = devisRepository.save(devis);
        log.info("Devis créé avec succès: {}", devis.getNumeroDevis());

        return devisMapper.toResponse(devis);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse updateDevis(UUID idDevis, DevisCreateRequest request) {
        log.info("Mise à jour du devis: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        if (!isDevisModifiable(idDevis)) {
            throw new BusinessException("Le devis ne peut pas être modifié dans son état actuel: " + devis.getStatut());
        }

        devisMapper.updateEntityFromRequest(request, devis);
        devis.setUpdatedAt(LocalDateTime.now());
        calculerTotauxDevis(devis);

        devis = devisRepository.save(devis);
        log.info("Devis mis à jour avec succès: {}", devis.getNumeroDevis());

        return devisMapper.toResponse(devis);
    }

    @Override
    @Cacheable(value = "devis", key = "#idDevis")
    public DevisResponse getDevis(UUID idDevis) {
        log.debug("Récupération du devis: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        return devisMapper.toResponse(devis);
    }

    @Override
    public DevisResponse getDevisByNumero(String numeroDevis) {
        log.debug("Récupération du devis par numéro: {}", numeroDevis);

        Devis devis = devisRepository.findByNumeroDevis(numeroDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec le numéro: " + numeroDevis));

        return devisMapper.toResponse(devis);
    }

    @Override
    public List<DevisResponse> getAllDevis() {
        log.debug("Récupération de tous les devis");
        List<Devis> devisList = devisRepository.findAll();
        return devisMapper.toResponseList(devisList);
    }

    @Override
    public Page<DevisResponse> getDevisPaginated(Pageable pageable) {
        log.debug("Récupération des devis paginés");
        var slice = devisRepository.findAll(pageable);
        List<DevisResponse> responses = devisMapper.toResponseList(slice.getContent());
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public List<DevisResponse> getDevisByClient(UUID idClient) {
        log.debug("Récupération des devis pour le client: {}", idClient);
        List<Devis> devisList = devisRepository.findByIdClient(idClient);
        return devisMapper.toResponseList(devisList);
    }

    @Override
    public List<DevisResponse> getDevisByStatut(StatutDevis statut) {
        log.debug("Récupération des devis avec le statut: {}", statut);
        List<Devis> devisList = devisRepository.findByStatut(statut);
        return devisMapper.toResponseList(devisList);
    }

    @Override
    public List<DevisResponse> getDevisByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des devis entre {} et {}", startDate, endDate);
        List<Devis> devisList = devisRepository.findByDateCreationBetween(startDate, endDate);
        return devisMapper.toResponseList(devisList);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public void deleteDevis(UUID idDevis) {
        log.info("Suppression du devis: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        if (devis.getStatut() == StatutDevis.CONVERTI_EN_FACTURE) {
            throw new BusinessException("Impossible de supprimer un devis déjà converti en facture");
        }

        devisRepository.delete(devis);
        log.info("Devis supprimé avec succès: {}", devis.getNumeroDevis());
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse changerStatut(UUID idDevis, StatutDevis nouveauStatut) {
        return changerStatut(idDevis, nouveauStatut, null);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse changerStatut(UUID idDevis, StatutDevis nouveauStatut, String motif) {
        log.info("Changement de statut du devis {} vers {}", idDevis, nouveauStatut);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        validateStatutTransition(devis.getStatut(), nouveauStatut);

        devis.setStatut(nouveauStatut);
        devis.setUpdatedAt(LocalDateTime.now());

        switch (nouveauStatut) {
            case ACCEPTE:
                devis.setDateAcceptation(LocalDateTime.now());
                break;
            case REFUSE:
                devis.setDateRefus(LocalDateTime.now());
                if (motif != null) {
                    devis.setMotifRefus(motif);
                }
                break;
        }

        devis = devisRepository.save(devis);
        log.info("Statut du devis changé avec succès: {}", devis.getNumeroDevis());

        return devisMapper.toResponse(devis);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse accepterDevis(UUID idDevis) {
        log.info("Acceptation du devis: {}", idDevis);
        return changerStatut(idDevis, StatutDevis.ACCEPTE);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse refuserDevis(UUID idDevis, String motifRefus) {
        log.info("Refus du devis: {} avec motif: {}", idDevis, motifRefus);
        return changerStatut(idDevis, StatutDevis.REFUSE, motifRefus);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public UUID convertirEnFacture(UUID idDevis) {
        log.info("Conversion du devis en facture: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        if (devis.getStatut() != StatutDevis.ACCEPTE) {
            throw new BusinessException("Seuls les devis acceptés peuvent être convertis en facture");
        }

        if (devis.getIdFactureConvertie() != null) {
            throw new BusinessException("Ce devis a déjà été converti en facture");
        }

        FactureCreateRequest factureRequest = convertDevisToFactureRequest(devis);
        var factureResponse = factureService.createFacture(factureRequest);

        devis.setStatut(StatutDevis.CONVERTI_EN_FACTURE);
        devis.setIdFactureConvertie(factureResponse.getIdFacture());
        devis.setUpdatedAt(LocalDateTime.now());
        devisRepository.save(devis);

        log.info("Devis {} converti en facture {}", devis.getNumeroDevis(), factureResponse.getNumeroFacture());
        return factureResponse.getIdFacture();
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse envoyerParEmail(UUID idDevis) {
        log.info("Envoi du devis par email: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        if (devis.getEmailClient() == null || devis.getEmailClient().trim().isEmpty()) {
            throw new BusinessException("L'email du client n'est pas renseigné");
        }

        emailService.sendDevisEmail(idDevis, devis.getEmailClient());

        devis.setEnvoyeParEmail(true);
        devis.setDateEnvoiEmail(LocalDateTime.now());
        if (devis.getStatut() == StatutDevis.BROUILLON) {
            devis.setStatut(StatutDevis.ENVOYE);
        }
        devis.setUpdatedAt(LocalDateTime.now());

        devis = devisRepository.save(devis);
        log.info("Devis envoyé par email avec succès: {}", devis.getNumeroDevis());

        return devisMapper.toResponse(devis);
    }

    @Override
    public String genererPdf(UUID idDevis) {
        log.info("Génération du PDF pour le devis: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        String pdfPath = pdfService.generateDevisPdf(devis);
        
        devis.setPdfPath(pdfPath);
        devis.setUpdatedAt(LocalDateTime.now());
        devisRepository.save(devis);

        log.info("PDF généré avec succès pour le devis: {}", devis.getNumeroDevis());
        return pdfPath;
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse dupliquerDevis(UUID idDevis) {
        log.info("Duplication du devis: {}", idDevis);

        Devis devisOriginal = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        Devis nouveauDevis = Devis.builder()
                .idDevis(UUID.randomUUID())
                .numeroDevis(genererNumeroDevis())
                .dateCreation(LocalDate.now())
                .dateValidite(LocalDate.now().plusDays(devisOriginal.getValiditeOffreJours()))
                .type(devisOriginal.getType())
                .statut(StatutDevis.BROUILLON)
                .idClient(devisOriginal.getIdClient())
                .nomClient(devisOriginal.getNomClient())
                .adresseClient(devisOriginal.getAdresseClient())
                .emailClient(devisOriginal.getEmailClient())
                .telephoneClient(devisOriginal.getTelephoneClient())
                .lignesDevis(devisOriginal.getLignesDevis())
                .devise(devisOriginal.getDevise())
                .tauxChange(devisOriginal.getTauxChange())
                .conditionsPaiement(devisOriginal.getConditionsPaiement())
                .notes(devisOriginal.getNotes())
                .remiseGlobalePourcentage(devisOriginal.getRemiseGlobalePourcentage())
                .remiseGlobaleMontant(devisOriginal.getRemiseGlobaleMontant())
                .validiteOffreJours(devisOriginal.getValiditeOffreJours())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        calculerTotauxDevis(nouveauDevis);
        nouveauDevis = devisRepository.save(nouveauDevis);

        log.info("Devis dupliqué avec succès: {} -> {}", devisOriginal.getNumeroDevis(), nouveauDevis.getNumeroDevis());
        return devisMapper.toResponse(nouveauDevis);
    }

    @Override
    public List<DevisResponse> getDevisExpires() {
        log.debug("Récupération des devis expirés");
        List<Devis> devisExpires = devisRepository.findExpiredDevis(LocalDate.now());
        return devisMapper.toResponseList(devisExpires);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public void marquerDevisCommeExpires() {
        log.info("Marquage des devis expirés");

        List<Devis> devisExpires = devisRepository.findExpiredDevis(LocalDate.now());
        
        for (Devis devis : devisExpires) {
            devis.setStatut(StatutDevis.EXPIRE);
            devis.setUpdatedAt(LocalDateTime.now());
            devisRepository.save(devis);
        }

        log.info("{} devis marqués comme expirés", devisExpires.size());
    }

    @Override
    public List<DevisResponse> getDevisConverties() {
        log.debug("Récupération des devis convertis en factures");
        List<Devis> devisConvertis = devisRepository.findConvertedToInvoice();
        return devisMapper.toResponseList(devisConvertis);
    }

    @Override
    @Transactional
    @CacheEvict(value = "devis", allEntries = true)
    public DevisResponse calculerTotaux(UUID idDevis) {
        log.debug("Recalcul des totaux pour le devis: {}", idDevis);

        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        calculerTotauxDevis(devis);
        devis.setUpdatedAt(LocalDateTime.now());
        devis = devisRepository.save(devis);

        return devisMapper.toResponse(devis);
    }

    @Override
    public boolean isDevisModifiable(UUID idDevis) {
        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        return devis.getStatut() == StatutDevis.BROUILLON || devis.getStatut() == StatutDevis.ENVOYE;
    }

    @Override
    public boolean isDevisExpire(UUID idDevis) {
        Devis devis = devisRepository.findByIdDevis(idDevis)
                .orElseThrow(() -> new ResourceNotFoundException("Devis non trouvé avec l'ID: " + idDevis));

        return devis.getDateValidite().isBefore(LocalDate.now()) || devis.getStatut() == StatutDevis.EXPIRE;
    }

    private String genererNumeroDevis() {
        String prefix = "DEV";
        String year = String.valueOf(LocalDate.now().getYear());
        String sequence = NumberUtil.generateSequence("DEVIS", year);
        return String.format("%s-%s-%s", prefix, year, sequence);
    }

    private void populateClientInfo(Devis devis, Client client) {
        devis.setNomClient(client.getUsername());
        devis.setAdresseClient(client.getAdresse());
        devis.setEmailClient(client.getEmail());
        devis.setTelephoneClient(client.getTelephone());
    }

    private void calculerTotauxDevis(Devis devis) {
        if (devis.getLignesDevis() == null || devis.getLignesDevis().isEmpty()) {
            devis.setMontantHT(BigDecimal.ZERO);
            devis.setMontantTVA(BigDecimal.ZERO);
            devis.setMontantTTC(BigDecimal.ZERO);
            devis.setMontantTotal(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneDevis ligne : devis.getLignesDevis()) {
            if (ligne.getIsTaxLine()) {
                totalTVA = totalTVA.add(ligne.getCredit());
            } else {
                totalHT = totalHT.add(ligne.getCredit());
            }
        }

        if (devis.getRemiseGlobalePourcentage() != null && devis.getRemiseGlobalePourcentage().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal remise = totalHT.multiply(devis.getRemiseGlobalePourcentage()).divide(BigDecimal.valueOf(100));
            totalHT = totalHT.subtract(remise);
        }

        if (devis.getRemiseGlobaleMontant() != null && devis.getRemiseGlobaleMontant().compareTo(BigDecimal.ZERO) > 0) {
            totalHT = totalHT.subtract(devis.getRemiseGlobaleMontant());
        }

        BigDecimal totalTTC = totalHT.add(totalTVA);

        devis.setMontantHT(totalHT);
        devis.setMontantTVA(totalTVA);
        devis.setMontantTTC(totalTTC);
        devis.setMontantTotal(totalTTC);
    }

    private void validateStatutTransition(StatutDevis statutActuel, StatutDevis nouveauStatut) {
        // Définir les transitions autorisées
        switch (statutActuel) {
            case BROUILLON:
                if (!(nouveauStatut == StatutDevis.ENVOYE || nouveauStatut == StatutDevis.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case ENVOYE:
                if (!(nouveauStatut == StatutDevis.ACCEPTE || nouveauStatut == StatutDevis.REFUSE || 
                      nouveauStatut == StatutDevis.EXPIRE || nouveauStatut == StatutDevis.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case ACCEPTE:
                if (nouveauStatut != StatutDevis.CONVERTI_EN_FACTURE) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case REFUSE:
            case EXPIRE:
            case ANNULE:
            case CONVERTI_EN_FACTURE:
                throw new BusinessException("Aucune transition autorisée depuis le statut " + statutActuel);
        }
    }

    private FactureCreateRequest convertDevisToFactureRequest(Devis devis) {
        return FactureCreateRequest.builder()
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30))
                .type(devis.getType())
                .idClient(devis.getIdClient())
                .devise(devis.getDevise())
                .tauxChange(devis.getTauxChange())
                .conditionsPaiement(devis.getConditionsPaiement())
                .notes("Converti du devis " + devis.getNumeroDevis())
                .referenceCommande(devis.getNumeroDevis())
                .build();
    }
}