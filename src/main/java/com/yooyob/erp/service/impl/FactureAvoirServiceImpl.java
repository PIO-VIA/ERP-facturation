package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.FactureAvoirCreateRequest;
import com.yooyob.erp.dto.response.FactureAvoirResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.ValidationException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.FactureAvoirMapper;
import com.yooyob.erp.model.entity.FactureAvoir;
import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.entity.LigneAvoir;
import com.yooyob.erp.model.entity.LigneFacture;
import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
import com.yooyob.erp.repository.FactureAvoirRepository;
import com.yooyob.erp.repository.FactureRepository;
import com.yooyob.erp.repository.ClientRepository;
import com.yooyob.erp.service.FactureAvoirService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FactureAvoirServiceImpl implements FactureAvoirService {

    private final FactureAvoirRepository factureAvoirRepository;
    private final FactureRepository factureRepository;
    private final ClientRepository clientRepository;
    private final FactureAvoirMapper factureAvoirMapper;
    private final EmailService emailService;
    private final PdfService pdfService;

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse createFactureAvoir(FactureAvoirCreateRequest request) {
        log.info("Création d'une nouvelle facture d'avoir pour la facture: {}", request.getIdFactureOrigine());

        ValidationUtil.validateNotNull(request, "La demande de création de facture d'avoir ne peut pas être nulle");
        ValidationUtil.validateNotNull(request.getIdFactureOrigine(), "L'ID de la facture d'origine est obligatoire");

        Facture factureOrigine = factureRepository.findByIdFacture(request.getIdFactureOrigine())
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'origine non trouvée avec l'ID: " + request.getIdFactureOrigine()));

        Client client = clientRepository.findByIdClient(factureOrigine.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + factureOrigine.getIdClient()));

        FactureAvoir factureAvoir = factureAvoirMapper.toEntity(request);
        factureAvoir.setIdAvoir(UUID.randomUUID());
        factureAvoir.setNumeroAvoir(genererNumeroAvoir());
        factureAvoir.setStatut(StatutAvoir.BROUILLON);
        factureAvoir.setCreatedAt(LocalDateTime.now());
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        populateInfosFromFactureOrigine(factureAvoir, factureOrigine);
        populateClientInfo(factureAvoir, client);
        calculerTotauxAvoir(factureAvoir);

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Facture d'avoir créée avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse updateFactureAvoir(UUID idAvoir, FactureAvoirCreateRequest request) {
        log.info("Mise à jour de la facture d'avoir: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (!isAvoirModifiable(idAvoir)) {
            throw new BusinessException("La facture d'avoir ne peut pas être modifiée dans son état actuel: " + factureAvoir.getStatut());
        }

        factureAvoirMapper.updateEntityFromRequest(request, factureAvoir);
        factureAvoir.setUpdatedAt(LocalDateTime.now());
        calculerTotauxAvoir(factureAvoir);

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Facture d'avoir mise à jour avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Cacheable(value = "facturesAvoir", key = "#idAvoir")
    public FactureAvoirResponse getFactureAvoir(UUID idAvoir) {
        log.debug("Récupération de la facture d'avoir: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    public FactureAvoirResponse getFactureAvoirByNumero(String numeroAvoir) {
        log.debug("Récupération de la facture d'avoir par numéro: {}", numeroAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByNumeroAvoir(numeroAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec le numéro: " + numeroAvoir));

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getAllFacturesAvoir() {
        log.debug("Récupération de toutes les factures d'avoir");
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findAll();
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public Page<FactureAvoirResponse> getFacturesAvoirPaginated(Pageable pageable) {
        log.debug("Récupération des factures d'avoir paginées");
        var slice = factureAvoirRepository.findAll(pageable);
        List<FactureAvoirResponse> responses = factureAvoirMapper.toResponseList(slice.getContent());
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public List<FactureAvoirResponse> getFacturesAvoirByClient(UUID idClient) {
        log.debug("Récupération des factures d'avoir pour le client: {}", idClient);
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findByIdClient(idClient);
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getFacturesAvoirByFactureOrigine(UUID idFactureOrigine) {
        log.debug("Récupération des factures d'avoir pour la facture d'origine: {}", idFactureOrigine);
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findByIdFactureOrigine(idFactureOrigine);
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getFacturesAvoirByStatut(StatutAvoir statut) {
        log.debug("Récupération des factures d'avoir avec le statut: {}", statut);
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findByStatut(statut);
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getFacturesAvoirByType(TypeAvoir typeAvoir) {
        log.debug("Récupération des factures d'avoir avec le type: {}", typeAvoir);
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findByTypeAvoir(typeAvoir);
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getFacturesAvoirByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération des factures d'avoir entre {} et {}", startDate, endDate);
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findByDateCreationBetween(startDate, endDate);
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public void deleteFactureAvoir(UUID idAvoir) {
        log.info("Suppression de la facture d'avoir: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (factureAvoir.getStatut() == StatutAvoir.APPLIQUE || factureAvoir.getStatut() == StatutAvoir.REMBOURSE) {
            throw new BusinessException("Impossible de supprimer une facture d'avoir déjà appliquée ou remboursée");
        }

        factureAvoirRepository.delete(factureAvoir);
        log.info("Facture d'avoir supprimée avec succès: {}", factureAvoir.getNumeroAvoir());
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse changerStatut(UUID idAvoir, StatutAvoir nouveauStatut) {
        log.info("Changement de statut de la facture d'avoir {} vers {}", idAvoir, nouveauStatut);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        validateStatutTransition(factureAvoir.getStatut(), nouveauStatut);

        factureAvoir.setStatut(nouveauStatut);
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Statut de la facture d'avoir changé avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse validerFactureAvoir(UUID idAvoir, UUID approuvePar) {
        log.info("Validation de la facture d'avoir: {} par {}", idAvoir, approuvePar);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (factureAvoir.getStatut() != StatutAvoir.BROUILLON) {
            throw new BusinessException("Seules les factures d'avoir en brouillon peuvent être validées");
        }

        factureAvoir.setStatut(StatutAvoir.VALIDE);
        factureAvoir.setDateValidation(LocalDate.now());
        factureAvoir.setApprouvePar(approuvePar);
        factureAvoir.setDateApprobation(LocalDateTime.now());
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Facture d'avoir validée avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse appliquerAvoir(UUID idAvoir, BigDecimal montantApplique) {
        log.info("Application de l'avoir {} pour un montant de {}", idAvoir, montantApplique);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (!isAvoirApplicable(idAvoir)) {
            throw new BusinessException("Cette facture d'avoir ne peut pas être appliquée dans son état actuel");
        }

        BigDecimal montantRestant = getMontantRestantAAppliquer(idAvoir);
        if (montantApplique.compareTo(montantRestant) > 0) {
            throw new BusinessException("Le montant à appliquer dépasse le montant restant disponible");
        }

        factureAvoir.setMontantApplique(factureAvoir.getMontantApplique().add(montantApplique));
        factureAvoir.setDateApplication(LocalDateTime.now());
        
        if (factureAvoir.getMontantApplique().compareTo(factureAvoir.getMontantTotal()) == 0) {
            factureAvoir.setStatut(StatutAvoir.APPLIQUE);
        }
        
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Avoir appliqué avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse rembourserAvoir(UUID idAvoir, BigDecimal montantRembourse, String modeRemboursement, String referenceRemboursement) {
        log.info("Remboursement de l'avoir {} pour un montant de {}", idAvoir, montantRembourse);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (factureAvoir.getStatut() != StatutAvoir.VALIDE) {
            throw new BusinessException("Seules les factures d'avoir validées peuvent être remboursées");
        }

        BigDecimal montantRestant = factureAvoir.getMontantTotal().subtract(factureAvoir.getMontantRembourse());
        if (montantRembourse.compareTo(montantRestant) > 0) {
            throw new BusinessException("Le montant à rembourser dépasse le montant restant disponible");
        }

        factureAvoir.setMontantRembourse(factureAvoir.getMontantRembourse().add(montantRembourse));
        factureAvoir.setModeRemboursement(modeRemboursement);
        factureAvoir.setReferenceRemboursement(referenceRemboursement);
        factureAvoir.setDateRemboursement(LocalDateTime.now());
        
        if (factureAvoir.getMontantRembourse().compareTo(factureAvoir.getMontantTotal()) == 0) {
            factureAvoir.setStatut(StatutAvoir.REMBOURSE);
        }
        
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Avoir remboursé avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse envoyerParEmail(UUID idAvoir) {
        log.info("Envoi de la facture d'avoir par email: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        if (factureAvoir.getEmailClient() == null || factureAvoir.getEmailClient().trim().isEmpty()) {
            throw new BusinessException("L'email du client n'est pas renseigné");
        }

        emailService.sendFactureAvoirEmail(idAvoir, factureAvoir.getEmailClient());

        factureAvoir.setEnvoyeParEmail(true);
        factureAvoir.setDateEnvoiEmail(LocalDateTime.now());
        factureAvoir.setUpdatedAt(LocalDateTime.now());

        factureAvoir = factureAvoirRepository.save(factureAvoir);
        log.info("Facture d'avoir envoyée par email avec succès: {}", factureAvoir.getNumeroAvoir());

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    public String genererPdf(UUID idAvoir) {
        log.info("Génération du PDF pour la facture d'avoir: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        String pdfPath = pdfService.generateFactureAvoirPdf(factureAvoir);
        
        factureAvoir.setPdfPath(pdfPath);
        factureAvoir.setUpdatedAt(LocalDateTime.now());
        factureAvoirRepository.save(factureAvoir);

        log.info("PDF généré avec succès pour la facture d'avoir: {}", factureAvoir.getNumeroAvoir());
        return pdfPath;
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse calculerTotaux(UUID idAvoir) {
        log.debug("Recalcul des totaux pour la facture d'avoir: {}", idAvoir);

        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        calculerTotauxAvoir(factureAvoir);
        factureAvoir.setUpdatedAt(LocalDateTime.now());
        factureAvoir = factureAvoirRepository.save(factureAvoir);

        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    @Transactional
    @CacheEvict(value = "facturesAvoir", allEntries = true)
    public FactureAvoirResponse creerAvoirDepuisFacture(UUID idFacture, List<UUID> lignesFacture, String motif, TypeAvoir typeAvoir) {
        log.info("Création d'un avoir depuis la facture {} pour les lignes {}", idFacture, lignesFacture);

        Facture facture = factureRepository.findByIdFacture(idFacture)
                .orElseThrow(() -> new ResourceNotFoundException("Facture non trouvée avec l'ID: " + idFacture));

        List<LigneAvoir> lignesAvoir = new ArrayList<>();
        
        for (UUID idLigneFacture : lignesFacture) {
            LigneFacture ligneFacture = facture.getLignesFacture().stream()
                    .filter(ligne -> ligne.getIdLigne().equals(idLigneFacture))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Ligne de facture non trouvée: " + idLigneFacture));

            LigneAvoir ligneAvoir = LigneAvoir.builder()
                    .idLigne(UUID.randomUUID())
                    .quantite(ligneFacture.getQuantite())
                    .description(ligneFacture.getDescription())
                    .debit(ligneFacture.getDebit())
                    .credit(ligneFacture.getCredit())
                    .isTaxLine(ligneFacture.getIsTaxLine())
                    .idProduit(ligneFacture.getIdProduit())
                    .nomProduit(ligneFacture.getNomProduit())
                    .prixUnitaire(ligneFacture.getPrixUnitaire())
                    .montantTotal(ligneFacture.getMontantTotal())
                    .idLigneFactureOrigine(ligneFacture.getIdLigne())
                    .quantiteOrigine(ligneFacture.getQuantite())
                    .motifRetour(motif)
                    .build();

            lignesAvoir.add(ligneAvoir);
        }

        FactureAvoir factureAvoir = FactureAvoir.builder()
                .idAvoir(UUID.randomUUID())
                .numeroAvoir(genererNumeroAvoir())
                .dateCreation(LocalDate.now())
                .typeAvoir(typeAvoir)
                .statut(StatutAvoir.BROUILLON)
                .idFactureOrigine(idFacture)
                .numeroFactureOrigine(facture.getNumeroFacture())
                .idClient(facture.getIdClient())
                .nomClient(facture.getNomClient())
                .adresseClient(facture.getAdresseClient())
                .emailClient(facture.getEmailClient())
                .telephoneClient(facture.getTelephoneClient())
                .lignesAvoir(lignesAvoir)
                .devise(facture.getDevise())
                .tauxChange(facture.getTauxChange())
                .motifAvoir(motif)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        calculerTotauxAvoir(factureAvoir);
        factureAvoir = factureAvoirRepository.save(factureAvoir);

        log.info("Facture d'avoir créée depuis la facture avec succès: {}", factureAvoir.getNumeroAvoir());
        return factureAvoirMapper.toResponse(factureAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getAvoirsNonTotalementAppliques() {
        log.debug("Récupération des avoirs non totalement appliqués");
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findAvoirsNonTotalementAppliques();
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public List<FactureAvoirResponse> getAvoirsApprouves() {
        log.debug("Récupération des avoirs approuvés");
        List<FactureAvoir> facturesAvoir = factureAvoirRepository.findAvoirsApprouves();
        return factureAvoirMapper.toResponseList(facturesAvoir);
    }

    @Override
    public BigDecimal getMontantRestantAAppliquer(UUID idAvoir) {
        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        return factureAvoir.getMontantTotal().subtract(factureAvoir.getMontantApplique());
    }

    @Override
    public boolean isAvoirModifiable(UUID idAvoir) {
        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        return factureAvoir.getStatut() == StatutAvoir.BROUILLON;
    }

    @Override
    public boolean isAvoirApplicable(UUID idAvoir) {
        FactureAvoir factureAvoir = factureAvoirRepository.findByIdAvoir(idAvoir)
                .orElseThrow(() -> new ResourceNotFoundException("Facture d'avoir non trouvée avec l'ID: " + idAvoir));

        return factureAvoir.getStatut() == StatutAvoir.VALIDE && 
               factureAvoir.getMontantApplique().compareTo(factureAvoir.getMontantTotal()) < 0;
    }

    private String genererNumeroAvoir() {
        String prefix = "AV";
        String year = String.valueOf(LocalDate.now().getYear());
        String sequence = NumberUtil.generateSequence("AVOIR", year);
        return String.format("%s-%s-%s", prefix, year, sequence);
    }

    private void populateInfosFromFactureOrigine(FactureAvoir factureAvoir, Facture factureOrigine) {
        factureAvoir.setNumeroFactureOrigine(factureOrigine.getNumeroFacture());
        factureAvoir.setIdClient(factureOrigine.getIdClient());
        factureAvoir.setDevise(factureOrigine.getDevise());
        factureAvoir.setTauxChange(factureOrigine.getTauxChange());
    }

    private void populateClientInfo(FactureAvoir factureAvoir, Client client) {
        factureAvoir.setNomClient(client.getUsername());
        factureAvoir.setAdresseClient(client.getAdresse());
        factureAvoir.setEmailClient(client.getEmail());
        factureAvoir.setTelephoneClient(client.getTelephone());
    }

    private void calculerTotauxAvoir(FactureAvoir factureAvoir) {
        if (factureAvoir.getLignesAvoir() == null || factureAvoir.getLignesAvoir().isEmpty()) {
            factureAvoir.setMontantHT(BigDecimal.ZERO);
            factureAvoir.setMontantTVA(BigDecimal.ZERO);
            factureAvoir.setMontantTTC(BigDecimal.ZERO);
            factureAvoir.setMontantTotal(BigDecimal.ZERO);
            return;
        }

        BigDecimal totalHT = BigDecimal.ZERO;
        BigDecimal totalTVA = BigDecimal.ZERO;

        for (LigneAvoir ligne : factureAvoir.getLignesAvoir()) {
            if (ligne.getIsTaxLine()) {
                totalTVA = totalTVA.add(ligne.getCredit());
            } else {
                totalHT = totalHT.add(ligne.getCredit());
            }
        }

        BigDecimal totalTTC = totalHT.add(totalTVA);

        factureAvoir.setMontantHT(totalHT);
        factureAvoir.setMontantTVA(totalTVA);
        factureAvoir.setMontantTTC(totalTTC);
        factureAvoir.setMontantTotal(totalTTC);
    }

    private void validateStatutTransition(StatutAvoir statutActuel, StatutAvoir nouveauStatut) {
        switch (statutActuel) {
            case BROUILLON:
                if (!(nouveauStatut == StatutAvoir.VALIDE || nouveauStatut == StatutAvoir.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case VALIDE:
                if (!(nouveauStatut == StatutAvoir.APPLIQUE || nouveauStatut == StatutAvoir.REMBOURSE || nouveauStatut == StatutAvoir.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case APPLIQUE:
            case REMBOURSE:
            case ANNULE:
                throw new BusinessException("Aucune transition autorisée depuis le statut " + statutActuel);
        }
    }
}