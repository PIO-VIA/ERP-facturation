package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.AbonnementFacturationCreateRequest;
import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.response.AbonnementFacturationResponse;
import com.yooyob.erp.dto.response.HistoriqueFacturationRecurrenteResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.AbonnementFacturationMapper;
import com.yooyob.erp.model.entity.AbonnementFacturation;
import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.entity.HistoriqueFacturationRecurrente;
import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
import com.yooyob.erp.repository.AbonnementFacturationRepository;
import com.yooyob.erp.repository.ClientRepository;
import com.yooyob.erp.repository.HistoriqueFacturationRecurrenteRepository;
import com.yooyob.erp.service.AbonnementFacturationService;
import com.yooyob.erp.service.FactureService;
import com.yooyob.erp.service.EmailService;
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
import org.springframework.context.annotation.Lazy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AbonnementFacturationServiceImpl implements AbonnementFacturationService {

    private final AbonnementFacturationRepository abonnementRepository;
    private final HistoriqueFacturationRecurrenteRepository historiqueRepository;
    private final ClientRepository clientRepository;
    private final AbonnementFacturationMapper abonnementMapper;
    @Lazy
    private final FactureService factureService;
    private final EmailService emailService;

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse createAbonnement(AbonnementFacturationCreateRequest request) {
        log.info("Création d'un nouvel abonnement pour le client: {}", request.getIdClient());

        ValidationUtil.validateNotNull(request, "La demande de création d'abonnement ne peut pas être nulle");
        ValidationUtil.validateNotNull(request.getIdClient(), "L'ID client est obligatoire");

        Client client = clientRepository.findByIdClient(request.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + request.getIdClient()));

        AbonnementFacturation abonnement = abonnementMapper.toEntity(request);
        abonnement.setIdAbonnement(UUID.randomUUID());
        abonnement.setStatut(request.getStatut() != null ? request.getStatut() : StatutAbonnement.BROUILLON);
        abonnement.setCreatedAt(LocalDateTime.now());
        abonnement.setUpdatedAt(LocalDateTime.now());

        populateClientInfo(abonnement, client);
        calculerProchaineFacturation(abonnement);

        abonnement = abonnementRepository.save(abonnement);
        log.info("Abonnement créé avec succès: {}", abonnement.getNomAbonnement());

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse updateAbonnement(UUID idAbonnement, AbonnementFacturationCreateRequest request) {
        log.info("Mise à jour de l'abonnement: {}", idAbonnement);

        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        if (!isAbonnementModifiable(idAbonnement)) {
            throw new BusinessException("L'abonnement ne peut pas être modifié dans son état actuel: " + abonnement.getStatut());
        }

        abonnementMapper.updateEntityFromRequest(request, abonnement);
        abonnement.setUpdatedAt(LocalDateTime.now());
        calculerProchaineFacturation(abonnement);

        abonnement = abonnementRepository.save(abonnement);
        log.info("Abonnement mis à jour avec succès: {}", abonnement.getNomAbonnement());

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    @Cacheable(value = "abonnements", key = "#idAbonnement")
    public AbonnementFacturationResponse getAbonnement(UUID idAbonnement) {
        log.debug("Récupération de l'abonnement: {}", idAbonnement);

        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    public List<AbonnementFacturationResponse> getAllAbonnements() {
        log.debug("Récupération de tous les abonnements");
        List<AbonnementFacturation> abonnements = abonnementRepository.findAll();
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public Page<AbonnementFacturationResponse> getAbonnementsPaginated(Pageable pageable) {
        log.debug("Récupération des abonnements paginés");
        var slice = abonnementRepository.findAll(pageable);
        List<AbonnementFacturationResponse> responses = abonnementMapper.toResponseList(slice.getContent());
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsByClient(UUID idClient) {
        log.debug("Récupération des abonnements pour le client: {}", idClient);
        List<AbonnementFacturation> abonnements = abonnementRepository.findByIdClient(idClient);
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsByStatut(StatutAbonnement statut) {
        log.debug("Récupération des abonnements avec le statut: {}", statut);
        List<AbonnementFacturation> abonnements = abonnementRepository.findByStatut(statut);
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsByFrequence(FrequenceRecurrence frequence) {
        log.debug("Récupération des abonnements avec la fréquence: {}", frequence);
        List<AbonnementFacturation> abonnements = abonnementRepository.findByFrequenceRecurrence(frequence);
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public void deleteAbonnement(UUID idAbonnement) {
        log.info("Suppression de l'abonnement: {}", idAbonnement);

        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        if (abonnement.getStatut() == StatutAbonnement.ACTIF) {
            throw new BusinessException("Impossible de supprimer un abonnement actif. Veuillez d'abord l'annuler.");
        }

        abonnementRepository.delete(abonnement);
        log.info("Abonnement supprimé avec succès: {}", abonnement.getNomAbonnement());
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse changerStatut(UUID idAbonnement, StatutAbonnement nouveauStatut) {
        log.info("Changement de statut de l'abonnement {} vers {}", idAbonnement, nouveauStatut);

        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        validateStatutTransition(abonnement.getStatut(), nouveauStatut);

        abonnement.setStatut(nouveauStatut);
        abonnement.setUpdatedAt(LocalDateTime.now());

        if (nouveauStatut == StatutAbonnement.ACTIF) {
            calculerProchaineFacturation(abonnement);
        }

        abonnement = abonnementRepository.save(abonnement);
        log.info("Statut de l'abonnement changé avec succès: {}", abonnement.getNomAbonnement());

        return abonnementMapper.toResponse(abonnement);
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse activerAbonnement(UUID idAbonnement) {
        log.info("Activation de l'abonnement: {}", idAbonnement);
        return changerStatut(idAbonnement, StatutAbonnement.ACTIF);
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse suspendreAbonnement(UUID idAbonnement) {
        log.info("Suspension de l'abonnement: {}", idAbonnement);
        return changerStatut(idAbonnement, StatutAbonnement.SUSPENDU);
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse annulerAbonnement(UUID idAbonnement) {
        log.info("Annulation de l'abonnement: {}", idAbonnement);
        return changerStatut(idAbonnement, StatutAbonnement.ANNULE);
    }

    @Override
    @Transactional
    public void executerFacturationRecurrente() {
        log.info("Exécution de la facturation récurrente");

        List<AbonnementFacturation> abonnementsAFacturer = abonnementRepository.findAbonnementsAFacturer(LocalDate.now());
        
        for (AbonnementFacturation abonnement : abonnementsAFacturer) {
            try {
                executerFacturationPourAbonnement(abonnement.getIdAbonnement());
            } catch (Exception e) {
                log.error("Erreur lors de la facturation de l'abonnement {}: {}", abonnement.getIdAbonnement(), e.getMessage());
                enregistrerErreurAbonnement(abonnement, e.getMessage());
            }
        }

        log.info("Facturation récurrente terminée. {} abonnements traités", abonnementsAFacturer.size());
    }

    @Override
    @Transactional
    public void executerFacturationPourAbonnement(UUID idAbonnement) {
        log.info("Exécution de la facturation pour l'abonnement: {}", idAbonnement);

        long startTime = System.currentTimeMillis();
        HistoriqueFacturationRecurrente historique = HistoriqueFacturationRecurrente.builder()
                .idHistorique(UUID.randomUUID())
                .idAbonnement(idAbonnement)
                .dateExecution(LocalDateTime.now())
                .succes(false)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                    .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

            if (!isAbonnementFacturable(abonnement)) {
                throw new BusinessException("L'abonnement n'est pas facturable dans son état actuel");
            }

            // Créer la facture
            FactureCreateRequest factureRequest = creerFactureDepuisAbonnement(abonnement);
            var factureResponse = factureService.createFacture(factureRequest);

            // Mettre à jour l'abonnement
            abonnement.setDateDerniereFacturation(LocalDate.now());
            abonnement.setNombreFacturesGenerees(abonnement.getNombreFacturesGenerees() + 1);
            abonnement.setMontantTotalFacture(abonnement.getMontantTotalFacture().add(abonnement.getMontantRecurrent()));
            abonnement.setDateProchaineFacturation(calculerProchaineFacturation(abonnement));
            abonnement.setUpdatedAt(LocalDateTime.now());

            // Vérifier si l'abonnement a atteint sa limite
            if (abonnement.getNombreMaxFactures() != null && 
                abonnement.getNombreFacturesGenerees() >= abonnement.getNombreMaxFactures()) {
                abonnement.setStatut(StatutAbonnement.EXPIRE);
            }

            abonnementRepository.save(abonnement);

            // Envoyer email si configuré
            if (abonnement.getAutoEnvoyerEmail() && abonnement.getEmailClient() != null) {
                emailService.sendFactureCreationEmail(factureResponse.getIdFacture(), abonnement.getEmailClient());
                historique.setEmailEnvoye(true);
            }

            // Marquer comme succès
            historique.setSucces(true);
            historique.setIdFactureGeneree(factureResponse.getIdFacture());
            historique.setNumeroFactureGeneree(factureResponse.getNumeroFacture());
            historique.setMontantFacture(abonnement.getMontantRecurrent());
            historique.setDateFacture(LocalDateTime.now());
            historique.setPdfGenere(abonnement.getAutoGenererPdf());

            log.info("Facturation réussie pour l'abonnement {}: facture {}", idAbonnement, factureResponse.getNumeroFacture());

        } catch (Exception e) {
            log.error("Erreur lors de la facturation de l'abonnement {}: {}", idAbonnement, e.getMessage());
            historique.setMessageErreur(e.getMessage());
            enregistrerErreurAbonnement(abonnementRepository.findByIdAbonnement(idAbonnement).orElse(null), e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            historique.setTempsExecutionMs(endTime - startTime);
            historiqueRepository.save(historique);
        }
    }

    @Override
    public LocalDate calculerProchaineFacturation(UUID idAbonnement) {
        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        return calculerProchaineFacturation(abonnement);
    }

    @Override
    @Transactional
    public void marquerAbonnementsCommeExpires() {
        log.info("Marquage des abonnements expirés");

        List<AbonnementFacturation> abonnementsExpires = abonnementRepository.findAbonnementsExpires(LocalDate.now());
        
        for (AbonnementFacturation abonnement : abonnementsExpires) {
            abonnement.setStatut(StatutAbonnement.EXPIRE);
            abonnement.setUpdatedAt(LocalDateTime.now());
            abonnementRepository.save(abonnement);
        }

        log.info("{} abonnements marqués comme expirés", abonnementsExpires.size());
    }

    @Override
    public void envoyerRappelsFacturation() {
        log.info("Envoi des rappels de facturation");

        List<AbonnementFacturation> abonnementsActifs = abonnementRepository.findActiveAbonnements();
        
        for (AbonnementFacturation abonnement : abonnementsActifs) {
            if (abonnement.getJoursAvantRappel() != null && abonnement.getEmailClient() != null) {
                LocalDate dateRappel = abonnement.getDateProchaineFacturation().minusDays(abonnement.getJoursAvantRappel());
                
                if (LocalDate.now().equals(dateRappel)) {
                    try {
                        // Envoyer rappel (implementation dépend du service email)
                        emailService.sendSimpleEmail(
                            abonnement.getEmailClient(),
                            "Rappel - Prochaine facturation",
                            "Votre prochaine facturation aura lieu le " + abonnement.getDateProchaineFacturation()
                        );
                        log.info("Rappel envoyé pour l'abonnement: {}", abonnement.getIdAbonnement());
                    } catch (Exception e) {
                        log.error("Erreur lors de l'envoi du rappel pour l'abonnement {}: {}", abonnement.getIdAbonnement(), e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsAFacturer(LocalDate date) {
        log.debug("Récupération des abonnements à facturer pour le: {}", date);
        List<AbonnementFacturation> abonnements = abonnementRepository.findAbonnementsAFacturer(date);
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsExpires() {
        log.debug("Récupération des abonnements expirés");
        List<AbonnementFacturation> abonnements = abonnementRepository.findAbonnementsExpires(LocalDate.now());
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public List<AbonnementFacturationResponse> getAbonnementsAvecErreurs() {
        log.debug("Récupération des abonnements avec erreurs");
        List<AbonnementFacturation> abonnements = abonnementRepository.findAbonnementsAvecErreurs();
        return abonnementMapper.toResponseList(abonnements);
    }

    @Override
    public List<HistoriqueFacturationRecurrenteResponse> getHistoriqueAbonnement(UUID idAbonnement) {
        log.debug("Récupération de l'historique pour l'abonnement: {}", idAbonnement);
        List<HistoriqueFacturationRecurrente> historiques = historiqueRepository.findByIdAbonnement(idAbonnement);
        return historiques.stream()
                .map(this::mapToHistoriqueResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<HistoriqueFacturationRecurrenteResponse> getHistoriqueParPeriode(LocalDate startDate, LocalDate endDate) {
        log.debug("Récupération de l'historique entre {} et {}", startDate, endDate);
        List<HistoriqueFacturationRecurrente> historiques = historiqueRepository.findByDateExecutionBetween(
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        return historiques.stream()
                .map(this::mapToHistoriqueResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void nettoyerHistoriqueAncien(int joursConservation) {
        log.info("Nettoyage de l'historique ancien (>{} jours)", joursConservation);
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(joursConservation);
        
        List<HistoriqueFacturationRecurrente> historiquesAnciens = historiqueRepository.findByDateExecutionBetween(
                LocalDateTime.of(2000, 1, 1, 0, 0), dateLimit);
        
        historiqueRepository.deleteAll(historiquesAnciens);
        log.info("{} entrées d'historique supprimées", historiquesAnciens.size());
    }

    @Override
    public boolean isAbonnementActif(UUID idAbonnement) {
        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        return abonnement.getStatut() == StatutAbonnement.ACTIF && abonnement.getActif();
    }

    @Override
    public boolean isAbonnementModifiable(UUID idAbonnement) {
        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        return abonnement.getStatut() == StatutAbonnement.BROUILLON || 
               abonnement.getStatut() == StatutAbonnement.SUSPENDU;
    }

    @Override
    public int getNombreFacturesRestantes(UUID idAbonnement) {
        AbonnementFacturation abonnement = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        if (abonnement.getNombreMaxFactures() == null) {
            return -1; // Illimité
        }

        return Math.max(0, abonnement.getNombreMaxFactures() - abonnement.getNombreFacturesGenerees());
    }

    @Override
    @Transactional
    @CacheEvict(value = "abonnements", allEntries = true)
    public AbonnementFacturationResponse dupliquerAbonnement(UUID idAbonnement, UUID nouveauClientId) {
        log.info("Duplication de l'abonnement {} pour le client {}", idAbonnement, nouveauClientId);

        AbonnementFacturation abonnementOriginal = abonnementRepository.findByIdAbonnement(idAbonnement)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement non trouvé avec l'ID: " + idAbonnement));

        Client nouveauClient = clientRepository.findByIdClient(nouveauClientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client non trouvé avec l'ID: " + nouveauClientId));

        AbonnementFacturation nouvelAbonnement = AbonnementFacturation.builder()
                .idAbonnement(UUID.randomUUID())
                .nomAbonnement(abonnementOriginal.getNomAbonnement() + " (Copie)")
                .description(abonnementOriginal.getDescription())
                .idClient(nouveauClientId)
                .statut(StatutAbonnement.BROUILLON)
                .frequenceRecurrence(abonnementOriginal.getFrequenceRecurrence())
                .jourFacturation(abonnementOriginal.getJourFacturation())
                .dateDebut(LocalDate.now())
                .montantRecurrent(abonnementOriginal.getMontantRecurrent())
                .lignesTemplate(abonnementOriginal.getLignesTemplate())
                .devise(abonnementOriginal.getDevise())
                .tauxChange(abonnementOriginal.getTauxChange())
                .conditionsPaiement(abonnementOriginal.getConditionsPaiement())
                .notesTemplate(abonnementOriginal.getNotesTemplate())
                .nombreMaxFactures(abonnementOriginal.getNombreMaxFactures())
                .autoEnvoyerEmail(abonnementOriginal.getAutoEnvoyerEmail())
                .autoGenererPdf(abonnementOriginal.getAutoGenererPdf())
                .joursAvantRappel(abonnementOriginal.getJoursAvantRappel())
                .templateEmailPersonnalise(abonnementOriginal.getTemplateEmailPersonnalise())
                .actif(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        populateClientInfo(nouvelAbonnement, nouveauClient);
        nouvelAbonnement = abonnementRepository.save(nouvelAbonnement);

        log.info("Abonnement dupliqué avec succès: {}", nouvelAbonnement.getIdAbonnement());
        return abonnementMapper.toResponse(nouvelAbonnement);
    }

    // Méthodes utilitaires privées

    private void populateClientInfo(AbonnementFacturation abonnement, Client client) {
        abonnement.setNomClient(client.getUsername());
        abonnement.setEmailClient(client.getEmail());
    }

    private LocalDate calculerProchaineFacturation(AbonnementFacturation abonnement) {
        LocalDate dateBase = abonnement.getDateDerniereFacturation() != null ? 
                             abonnement.getDateDerniereFacturation() : 
                             abonnement.getDateDebut();

        LocalDate prochaineDate;
        
        switch (abonnement.getFrequenceRecurrence()) {
            case QUOTIDIENNE:
                prochaineDate = dateBase.plusDays(1);
                break;
            case HEBDOMADAIRE:
                prochaineDate = dateBase.plusWeeks(1);
                break;
            case MENSUELLE:
                prochaineDate = dateBase.plusMonths(1);
                if (abonnement.getJourFacturation() != null) {
                    prochaineDate = prochaineDate.withDayOfMonth(
                        Math.min(abonnement.getJourFacturation(), prochaineDate.lengthOfMonth()));
                }
                break;
            case TRIMESTRIELLE:
                prochaineDate = dateBase.plusMonths(3);
                break;
            case SEMESTRIELLE:
                prochaineDate = dateBase.plusMonths(6);
                break;
            case ANNUELLE:
                prochaineDate = dateBase.plusYears(1);
                break;
            default:
                prochaineDate = dateBase.plusMonths(1);
        }

        abonnement.setDateProchaineFacturation(prochaineDate);
        return prochaineDate;
    }

    private boolean isAbonnementFacturable(AbonnementFacturation abonnement) {
        return abonnement.getStatut() == StatutAbonnement.ACTIF &&
               abonnement.getActif() &&
               (abonnement.getDateFin() == null || abonnement.getDateFin().isAfter(LocalDate.now())) &&
               (abonnement.getNombreMaxFactures() == null || 
                abonnement.getNombreFacturesGenerees() < abonnement.getNombreMaxFactures());
    }

    private FactureCreateRequest creerFactureDepuisAbonnement(AbonnementFacturation abonnement) {
        return FactureCreateRequest.builder()
                .dateFacturation(LocalDate.now())
                .dateEcheance(LocalDate.now().plusDays(30)) // Par défaut 30 jours
                .type("ABONNEMENT")
                .idClient(abonnement.getIdClient())
                .lignesFacture(abonnement.getLignesTemplate().stream()
                        .map(this::convertLigneToCreateRequest)
                        .collect(Collectors.toList()))
                .devise(abonnement.getDevise())
                .tauxChange(abonnement.getTauxChange())
                .conditionsPaiement(abonnement.getConditionsPaiement())
                .notes(abonnement.getNotesTemplate() + " - Abonnement: " + abonnement.getNomAbonnement())
                .referenceCommande("ABN-" + abonnement.getIdAbonnement().toString().substring(0, 8))
                .build();
    }

    private com.yooyob.erp.dto.request.LigneFactureCreateRequest convertLigneToCreateRequest(com.yooyob.erp.model.entity.LigneFacture ligne) {
        return com.yooyob.erp.dto.request.LigneFactureCreateRequest.builder()
                .quantite(ligne.getQuantite())
                .description(ligne.getDescription())
                .debit(ligne.getDebit())
                .credit(ligne.getCredit())
                .isTaxLine(ligne.getIsTaxLine())
                .idProduit(ligne.getIdProduit())
                .nomProduit(ligne.getNomProduit())
                .prixUnitaire(ligne.getPrixUnitaire())
                .montantTotal(ligne.getMontantTotal())
                .build();
    }

    private void validateStatutTransition(StatutAbonnement statutActuel, StatutAbonnement nouveauStatut) {
        switch (statutActuel) {
            case BROUILLON:
                if (!(nouveauStatut == StatutAbonnement.ACTIF || nouveauStatut == StatutAbonnement.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case ACTIF:
                if (!(nouveauStatut == StatutAbonnement.SUSPENDU || nouveauStatut == StatutAbonnement.EXPIRE || 
                      nouveauStatut == StatutAbonnement.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case SUSPENDU:
                if (!(nouveauStatut == StatutAbonnement.ACTIF || nouveauStatut == StatutAbonnement.ANNULE)) {
                    throw new BusinessException("Transition non autorisée de " + statutActuel + " vers " + nouveauStatut);
                }
                break;
            case EXPIRE:
            case ANNULE:
                throw new BusinessException("Aucune transition autorisée depuis le statut " + statutActuel);
        }
    }

    private void enregistrerErreurAbonnement(AbonnementFacturation abonnement, String erreur) {
        if (abonnement != null) {
            abonnement.setDerniereErreur(erreur);
            abonnement.setDateDerniereErreur(LocalDateTime.now());
            abonnement.setUpdatedAt(LocalDateTime.now());
            abonnementRepository.save(abonnement);
        }
    }

    private HistoriqueFacturationRecurrenteResponse mapToHistoriqueResponse(HistoriqueFacturationRecurrente historique) {
        return HistoriqueFacturationRecurrenteResponse.builder()
                .idHistorique(historique.getIdHistorique())
                .idAbonnement(historique.getIdAbonnement())
                .nomAbonnement(historique.getNomAbonnement())
                .idFactureGeneree(historique.getIdFactureGeneree())
                .numeroFactureGeneree(historique.getNumeroFactureGeneree())
                .dateExecution(historique.getDateExecution())
                .dateFacture(historique.getDateFacture())
                .montantFacture(historique.getMontantFacture())
                .succes(historique.getSucces())
                .messageErreur(historique.getMessageErreur())
                .detailsExecution(historique.getDetailsExecution())
                .emailEnvoye(historique.getEmailEnvoye())
                .pdfGenere(historique.getPdfGenere())
                .tempsExecutionMs(historique.getTempsExecutionMs())
                .createdAt(historique.getCreatedAt())
                .build();
    }
}