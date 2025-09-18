package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.WorkflowApprobationCreateRequest;
import com.yooyob.erp.dto.request.DemandeApprobationCreateRequest;
import com.yooyob.erp.dto.response.WorkflowApprobationResponse;
import com.yooyob.erp.dto.response.DemandeApprobationResponse;
import com.yooyob.erp.model.enums.TypeWorkflow;
import com.yooyob.erp.model.enums.StatutApprobation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface WorkflowApprobationService {

    // Gestion des workflows
    WorkflowApprobationResponse createWorkflow(WorkflowApprobationCreateRequest request);
    
    WorkflowApprobationResponse updateWorkflow(UUID idWorkflow, WorkflowApprobationCreateRequest request);
    
    WorkflowApprobationResponse getWorkflow(UUID idWorkflow);
    
    List<WorkflowApprobationResponse> getAllWorkflows();
    
    List<WorkflowApprobationResponse> getWorkflowsByType(TypeWorkflow typeWorkflow);
    
    void deleteWorkflow(UUID idWorkflow);
    
    WorkflowApprobationResponse activerWorkflow(UUID idWorkflow);
    
    WorkflowApprobationResponse desactiverWorkflow(UUID idWorkflow);

    // Gestion des demandes d'approbation
    DemandeApprobationResponse creerDemandeApprobation(DemandeApprobationCreateRequest request);
    
    DemandeApprobationResponse creerDemandeAutomatique(TypeWorkflow typeWorkflow, UUID idObjet, String typeObjet, BigDecimal montant, UUID demandeur);
    
    DemandeApprobationResponse getDemandeApprobation(UUID idDemande);
    
    List<DemandeApprobationResponse> getDemandesByDemandeur(UUID demandeur);
    
    List<DemandeApprobationResponse> getDemandesByApprobateur(UUID approbateur);
    
    List<DemandeApprobationResponse> getDemandesEnAttente();
    
    List<DemandeApprobationResponse> getDemandesByStatut(StatutApprobation statut);
    
    Page<DemandeApprobationResponse> getDemandesPaginated(Pageable pageable);

    // Actions d'approbation
    DemandeApprobationResponse approuverDemande(UUID idDemande, UUID approbateur, String commentaires);
    
    DemandeApprobationResponse rejeterDemande(UUID idDemande, UUID approbateur, String motifRejet, String commentaires);
    
    DemandeApprobationResponse deleguerdDemande(UUID idDemande, UUID approbateurOriginal, UUID nouvelApprobateur, String commentaires);
    
    DemandeApprobationResponse annulerDemande(UUID idDemande, UUID utilisateur, String motif);

    // Automatisation et monitoring
    void processerEscaladesAutomatiques();
    
    void processerExpirations();
    
    void envoyerNotificationsRappel();
    
    void executerActionsAutomatiques();

    // Validation et vérifications
    boolean isWorkflowApplicable(TypeWorkflow typeWorkflow, UUID idObjet, BigDecimal montant);
    
    boolean isApprobationRequise(TypeWorkflow typeWorkflow, UUID idObjet, BigDecimal montant);
    
    boolean isUtilisateurApprobateur(UUID idDemande, UUID utilisateur);
    
    boolean isDemandeModifiable(UUID idDemande);

    // Utilitaires
    List<UUID> getApprobateursRequis(UUID idDemande);
    
    List<DemandeApprobationResponse> getHistoriqueObjet(UUID idObjet, String typeObjet);
    
    int getNombreApprobationsEnAttente(UUID approbateur);
    
    WorkflowApprobationResponse findBestWorkflow(TypeWorkflow typeWorkflow, BigDecimal montant);
    
    void nettoyerDemandesAnciennes(int joursConservation);
}