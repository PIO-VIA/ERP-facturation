package com.yooyob.erp.service.impl;

import com.yooyob.erp.dto.request.WorkflowApprobationCreateRequest;
import com.yooyob.erp.dto.request.DemandeApprobationCreateRequest;
import com.yooyob.erp.dto.response.WorkflowApprobationResponse;
import com.yooyob.erp.dto.response.DemandeApprobationResponse;
import com.yooyob.erp.exception.ResourceNotFoundException;
import com.yooyob.erp.exception.BusinessException;
import com.yooyob.erp.mapper.WorkflowApprobationMapper;
import com.yooyob.erp.model.entity.WorkflowApprobation;
import com.yooyob.erp.model.entity.DemandeApprobation;
import com.yooyob.erp.model.entity.HistoriqueApprobation;
import com.yooyob.erp.model.enums.TypeWorkflow;
import com.yooyob.erp.model.enums.StatutApprobation;
import com.yooyob.erp.repository.WorkflowApprobationRepository;
import com.yooyob.erp.repository.DemandeApprobationRepository;
import com.yooyob.erp.service.WorkflowApprobationService;
import com.yooyob.erp.service.EmailService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class WorkflowApprobationServiceImpl implements WorkflowApprobationService {

    private final WorkflowApprobationRepository workflowRepository;
    private final DemandeApprobationRepository demandeRepository;
    private final WorkflowApprobationMapper workflowMapper;
    private final EmailService emailService;

    @Override
    @Transactional
    @CacheEvict(value = "workflows", allEntries = true)
    public WorkflowApprobationResponse createWorkflow(WorkflowApprobationCreateRequest request) {
        log.info("Création d'un nouveau workflow: {}", request.getNomWorkflow());

        WorkflowApprobation workflow = workflowMapper.toEntity(request);
        workflow.setIdWorkflow(UUID.randomUUID());
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setUpdatedAt(LocalDateTime.now());

        workflow = workflowRepository.save(workflow);
        log.info("Workflow créé avec succès: {}", workflow.getNomWorkflow());

        return workflowMapper.toResponse(workflow);
    }

    @Override
    @Transactional
    @CacheEvict(value = "workflows", allEntries = true)
    public WorkflowApprobationResponse updateWorkflow(UUID idWorkflow, WorkflowApprobationCreateRequest request) {
        log.info("Mise à jour du workflow: {}", idWorkflow);

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(idWorkflow)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + idWorkflow));

        workflowMapper.updateEntityFromRequest(request, workflow);
        workflow.setUpdatedAt(LocalDateTime.now());

        workflow = workflowRepository.save(workflow);
        log.info("Workflow mis à jour avec succès: {}", workflow.getNomWorkflow());

        return workflowMapper.toResponse(workflow);
    }

    @Override
    @Cacheable(value = "workflows", key = "#idWorkflow")
    public WorkflowApprobationResponse getWorkflow(UUID idWorkflow) {
        log.debug("Récupération du workflow: {}", idWorkflow);

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(idWorkflow)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + idWorkflow));

        return workflowMapper.toResponse(workflow);
    }

    @Override
    public List<WorkflowApprobationResponse> getAllWorkflows() {
        log.debug("Récupération de tous les workflows");
        List<WorkflowApprobation> workflows = workflowRepository.findAll();
        return workflowMapper.toResponseList(workflows);
    }

    @Override
    public List<WorkflowApprobationResponse> getWorkflowsByType(TypeWorkflow typeWorkflow) {
        log.debug("Récupération des workflows par type: {}", typeWorkflow);
        List<WorkflowApprobation> workflows = workflowRepository.findActiveByType(typeWorkflow);
        return workflowMapper.toResponseList(workflows);
    }

    @Override
    @Transactional
    @CacheEvict(value = "workflows", allEntries = true)
    public void deleteWorkflow(UUID idWorkflow) {
        log.info("Suppression du workflow: {}", idWorkflow);

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(idWorkflow)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + idWorkflow));

        // Vérifier qu'il n'y a pas de demandes en cours
        List<DemandeApprobation> demandesEnCours = demandeRepository.findByWorkflowAndStatutPending(idWorkflow);
        if (!demandesEnCours.isEmpty()) {
            throw new BusinessException("Impossible de supprimer le workflow car il y a des demandes en cours");
        }

        workflowRepository.delete(workflow);
        log.info("Workflow supprimé avec succès: {}", workflow.getNomWorkflow());
    }

    @Override
    @Transactional
    @CacheEvict(value = "workflows", allEntries = true)
    public WorkflowApprobationResponse activerWorkflow(UUID idWorkflow) {
        log.info("Activation du workflow: {}", idWorkflow);

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(idWorkflow)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + idWorkflow));

        workflow.setActif(true);
        workflow.setUpdatedAt(LocalDateTime.now());

        workflow = workflowRepository.save(workflow);
        log.info("Workflow activé avec succès: {}", workflow.getNomWorkflow());

        return workflowMapper.toResponse(workflow);
    }

    @Override
    @Transactional
    @CacheEvict(value = "workflows", allEntries = true)
    public WorkflowApprobationResponse desactiverWorkflow(UUID idWorkflow) {
        log.info("Désactivation du workflow: {}", idWorkflow);

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(idWorkflow)
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + idWorkflow));

        workflow.setActif(false);
        workflow.setUpdatedAt(LocalDateTime.now());

        workflow = workflowRepository.save(workflow);
        log.info("Workflow désactivé avec succès: {}", workflow.getNomWorkflow());

        return workflowMapper.toResponse(workflow);
    }

    @Override
    @Transactional
    public DemandeApprobationResponse creerDemandeApprobation(DemandeApprobationCreateRequest request) {
        log.info("Création d'une demande d'approbation pour l'objet: {}", request.getIdObjet());

        WorkflowApprobation workflow = workflowRepository.findByIdWorkflow(request.getIdWorkflow())
                .orElseThrow(() -> new ResourceNotFoundException("Workflow non trouvé avec l'ID: " + request.getIdWorkflow()));

        if (!workflow.getActif()) {
            throw new BusinessException("Le workflow n'est pas actif");
        }

        DemandeApprobation demande = DemandeApprobation.builder()
                .idDemande(UUID.randomUUID())
                .idWorkflow(request.getIdWorkflow())
                .typeWorkflow(request.getTypeWorkflow())
                .titreDemande(request.getTitreDemande())
                .description(request.getDescription())
                .idObjet(request.getIdObjet())
                .typeObjet(request.getTypeObjet())
                .donneesObjet(request.getDonneesObjet())
                .montantConcerne(request.getMontantConcerne())
                .demandeur(request.getDemandeur())
                .statut(StatutApprobation.EN_ATTENTE)
                .etapeCourante(1)
                .historiqueApprobations(new ArrayList<>())
                .approbateursEnAttente(new ArrayList<>())
                .dateCreation(LocalDateTime.now())
                .dateExpiration(LocalDateTime.now().plusHours(workflow.getDelaiExpirationHeures()))
                .dateDerniereAction(LocalDateTime.now())
                .commentairesDemandeur(request.getCommentairesDemandeur())
                .escaladeesEffectuees(0)
                .priorite(request.getPriorite())
                .tags(request.getTags())
                .metadata(request.getMetadata())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Initialiser la première étape
        initialiserPremiereEtape(demande, workflow);

        demande = demandeRepository.save(demande);
        log.info("Demande d'approbation créée avec succès: {}", demande.getIdDemande());

        // Envoyer notifications
        envoyerNotificationsInitiales(demande);

        return mapToDemandeResponse(demande);
    }

    @Override
    @Transactional
    public DemandeApprobationResponse creerDemandeAutomatique(TypeWorkflow typeWorkflow, UUID idObjet, String typeObjet, BigDecimal montant, UUID demandeur) {
        log.info("Création automatique d'une demande d'approbation pour {} ({})", typeWorkflow, montant);

        // Trouver le meilleur workflow
        WorkflowApprobation workflow = trouverMeilleurWorkflow(typeWorkflow, montant);
        
        if (workflow == null) {
            throw new BusinessException("Aucun workflow applicable trouvé pour " + typeWorkflow + " avec montant " + montant);
        }

        DemandeApprobationCreateRequest request = DemandeApprobationCreateRequest.builder()
                .idWorkflow(workflow.getIdWorkflow())
                .typeWorkflow(typeWorkflow)
                .titreDemande("Approbation automatique - " + typeWorkflow.getLibelle())
                .idObjet(idObjet)
                .typeObjet(typeObjet)
                .montantConcerne(montant)
                .demandeur(demandeur)
                .build();

        return creerDemandeApprobation(request);
    }

    @Override
    public DemandeApprobationResponse getDemandeApprobation(UUID idDemande) {
        log.debug("Récupération de la demande d'approbation: {}", idDemande);

        DemandeApprobation demande = demandeRepository.findByIdDemande(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'approbation non trouvée avec l'ID: " + idDemande));

        return mapToDemandeResponse(demande);
    }

    @Override
    @Transactional
    public DemandeApprobationResponse approuverDemande(UUID idDemande, UUID approbateur, String commentaires) {
        log.info("Approbation de la demande {} par {}", idDemande, approbateur);

        DemandeApprobation demande = demandeRepository.findByIdDemande(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'approbation non trouvée avec l'ID: " + idDemande));

        if (!isUtilisateurApprobateur(idDemande, approbateur)) {
            throw new BusinessException("L'utilisateur n'est pas autorisé à approuver cette demande");
        }

        if (demande.getStatut() != StatutApprobation.EN_ATTENTE && demande.getStatut() != StatutApprobation.EN_COURS) {
            throw new BusinessException("La demande ne peut pas être approuvée dans son état actuel: " + demande.getStatut());
        }

        // Ajouter l'approbation à l'historique
        HistoriqueApprobation historique = HistoriqueApprobation.builder()
                .ordreAction(demande.getHistoriqueApprobations().size() + 1)
                .etape(demande.getEtapeCourante())
                .approbateur(approbateur)
                .action(StatutApprobation.APPROUVE)
                .commentaires(commentaires)
                .dateAction(LocalDateTime.now())
                .build();

        demande.getHistoriqueApprobations().add(historique);
        demande.setDateDerniereAction(LocalDateTime.now());

        // Vérifier si l'étape est complète et passer à la suivante
        if (etapeEstComplete(demande)) {
            passerEtapeSuivante(demande);
        }

        demande.setUpdatedAt(LocalDateTime.now());
        demande = demandeRepository.save(demande);

        log.info("Demande approuvée avec succès: {}", demande.getIdDemande());
        return mapToDemandeResponse(demande);
    }

    @Override
    @Transactional
    public DemandeApprobationResponse rejeterDemande(UUID idDemande, UUID approbateur, String motifRejet, String commentaires) {
        log.info("Rejet de la demande {} par {}", idDemande, approbateur);

        DemandeApprobation demande = demandeRepository.findByIdDemande(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'approbation non trouvée avec l'ID: " + idDemande));

        if (!isUtilisateurApprobateur(idDemande, approbateur)) {
            throw new BusinessException("L'utilisateur n'est pas autorisé à rejeter cette demande");
        }

        // Ajouter le rejet à l'historique
        HistoriqueApprobation historique = HistoriqueApprobation.builder()
                .ordreAction(demande.getHistoriqueApprobations().size() + 1)
                .etape(demande.getEtapeCourante())
                .approbateur(approbateur)
                .action(StatutApprobation.REJETE)
                .commentaires(commentaires)
                .dateAction(LocalDateTime.now())
                .build();

        demande.getHistoriqueApprobations().add(historique);
        demande.setStatut(StatutApprobation.REJETE);
        demande.setMotifRejet(motifRejet);
        demande.setDateDerniereAction(LocalDateTime.now());
        demande.setDateFinalisation(LocalDateTime.now());
        demande.setUpdatedAt(LocalDateTime.now());

        demande = demandeRepository.save(demande);

        log.info("Demande rejetée avec succès: {}", demande.getIdDemande());
        return mapToDemandeResponse(demande);
    }

    // Implémentation simplifiée des autres méthodes...

    @Override
    public List<DemandeApprobationResponse> getDemandesByDemandeur(UUID demandeur) {
        List<DemandeApprobation> demandes = demandeRepository.findByDemandeur(demandeur);
        return demandes.stream().map(this::mapToDemandeResponse).collect(Collectors.toList());
    }

    @Override
    public List<DemandeApprobationResponse> getDemandesByApprobateur(UUID approbateur) {
        List<DemandeApprobation> demandes = demandeRepository.findByApprobateurEnAttente(approbateur);
        return demandes.stream().map(this::mapToDemandeResponse).collect(Collectors.toList());
    }

    @Override
    public List<DemandeApprobationResponse> getDemandesEnAttente() {
        List<DemandeApprobation> demandes = demandeRepository.findPendingDemandes();
        return demandes.stream().map(this::mapToDemandeResponse).collect(Collectors.toList());
    }

    @Override
    public List<DemandeApprobationResponse> getDemandesByStatut(StatutApprobation statut) {
        List<DemandeApprobation> demandes = demandeRepository.findByStatut(statut);
        return demandes.stream().map(this::mapToDemandeResponse).collect(Collectors.toList());
    }

    @Override
    public Page<DemandeApprobationResponse> getDemandesPaginated(Pageable pageable) {
        var slice = demandeRepository.findAll(pageable);
        List<DemandeApprobationResponse> responses = slice.getContent().stream()
                .map(this::mapToDemandeResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public boolean isWorkflowApplicable(TypeWorkflow typeWorkflow, UUID idObjet, BigDecimal montant) {
        List<WorkflowApprobation> workflows = workflowRepository.findApplicableWorkflows(typeWorkflow, montant);
        return !workflows.isEmpty();
    }

    @Override
    public boolean isApprobationRequise(TypeWorkflow typeWorkflow, UUID idObjet, BigDecimal montant) {
        return isWorkflowApplicable(typeWorkflow, idObjet, montant);
    }

    @Override
    public boolean isUtilisateurApprobateur(UUID idDemande, UUID utilisateur) {
        DemandeApprobation demande = demandeRepository.findByIdDemande(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'approbation non trouvée avec l'ID: " + idDemande));

        return demande.getApprobateursEnAttente().contains(utilisateur);
    }

    @Override
    public boolean isDemandeModifiable(UUID idDemande) {
        DemandeApprobation demande = demandeRepository.findByIdDemande(idDemande)
                .orElseThrow(() -> new ResourceNotFoundException("Demande d'approbation non trouvée avec l'ID: " + idDemande));

        return demande.getStatut() == StatutApprobation.EN_ATTENTE || 
               demande.getStatut() == StatutApprobation.EN_COURS;
    }

    // Méthodes utilitaires privées

    private WorkflowApprobation trouverMeilleurWorkflow(TypeWorkflow typeWorkflow, BigDecimal montant) {
        List<WorkflowApprobation> workflows = workflowRepository.findApplicableWorkflows(typeWorkflow, montant);
        return workflows.isEmpty() ? null : workflows.get(0);
    }

    private void initialiserPremiereEtape(DemandeApprobation demande, WorkflowApprobation workflow) {
        if (workflow.getEtapesApprobation() != null && !workflow.getEtapesApprobation().isEmpty()) {
            var premiereEtape = workflow.getEtapesApprobation().get(0);
            demande.setApprobateursEnAttente(new ArrayList<>(premiereEtape.getApproubateursRequis()));
        }
    }

    private void envoyerNotificationsInitiales(DemandeApprobation demande) {
        // Implementation simplifiée
        log.info("Envoi des notifications pour la demande: {}", demande.getIdDemande());
    }

    private boolean etapeEstComplete(DemandeApprobation demande) {
        // Logic simplifiée - en réalité il faudrait vérifier le nombre d'approbations requises
        return true;
    }

    private void passerEtapeSuivante(DemandeApprobation demande) {
        // Logic pour passer à l'étape suivante ou finaliser
        demande.setStatut(StatutApprobation.APPROUVE);
        demande.setDateFinalisation(LocalDateTime.now());
    }

    private DemandeApprobationResponse mapToDemandeResponse(DemandeApprobation demande) {
        return DemandeApprobationResponse.builder()
                .idDemande(demande.getIdDemande())
                .idWorkflow(demande.getIdWorkflow())
                .typeWorkflow(demande.getTypeWorkflow())
                .titreDemande(demande.getTitreDemande())
                .description(demande.getDescription())
                .idObjet(demande.getIdObjet())
                .typeObjet(demande.getTypeObjet())
                .donneesObjet(demande.getDonneesObjet())
                .montantConcerne(demande.getMontantConcerne())
                .demandeur(demande.getDemandeur())
                .nomDemandeur(demande.getNomDemandeur())
                .statut(demande.getStatut())
                .etapeCourante(demande.getEtapeCourante())
                .approbateursEnAttente(demande.getApprobateursEnAttente())
                .dateCreation(demande.getDateCreation())
                .dateExpiration(demande.getDateExpiration())
                .dateDerniereAction(demande.getDateDerniereAction())
                .dateFinalisation(demande.getDateFinalisation())
                .commentairesDemandeur(demande.getCommentairesDemandeur())
                .commentairesFinaux(demande.getCommentairesFinaux())
                .motifRejet(demande.getMotifRejet())
                .escaladeesEffectuees(demande.getEscaladeesEffectuees())
                .priorite(demande.getPriorite())
                .tags(demande.getTags())
                .metadata(demande.getMetadata())
                .createdAt(demande.getCreatedAt())
                .updatedAt(demande.getUpdatedAt())
                .build();
    }

    // Implémentation stub pour les méthodes restantes
    @Override public DemandeApprobationResponse deleguerdDemande(UUID idDemande, UUID approbateurOriginal, UUID nouvelApprobateur, String commentaires) { return null; }
    @Override public DemandeApprobationResponse annulerDemande(UUID idDemande, UUID utilisateur, String motif) { return null; }
    @Override public void processerEscaladesAutomatiques() {}
    @Override public void processerExpirations() {}
    @Override public void envoyerNotificationsRappel() {}
    @Override public void executerActionsAutomatiques() {}
    @Override public List<UUID> getApprobateursRequis(UUID idDemande) { return new ArrayList<>(); }
    @Override public List<DemandeApprobationResponse> getHistoriqueObjet(UUID idObjet, String typeObjet) { return new ArrayList<>(); }
    @Override public int getNombreApprobationsEnAttente(UUID approbateur) { return 0; }
    @Override public WorkflowApprobationResponse findBestWorkflow(TypeWorkflow typeWorkflow, BigDecimal montant) { return null; }
    @Override public void nettoyerDemandesAnciennes(int joursConservation) {}
}