package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.WorkflowApprobation;
import com.yooyob.erp.model.enums.TypeWorkflow;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkflowApprobationRepository extends CassandraRepository<WorkflowApprobation, UUID> {

    Optional<WorkflowApprobation> findByIdWorkflow(UUID idWorkflow);

    @Query("SELECT * FROM workflows_approbation WHERE type_workflow = ?0 AND actif = true ALLOW FILTERING")
    List<WorkflowApprobation> findActiveByType(TypeWorkflow typeWorkflow);

    @Query("SELECT * FROM workflows_approbation WHERE type_workflow = ?0 AND actif = true AND (montant_seuil_min IS NULL OR montant_seuil_min <= ?1) AND (montant_seuil_max IS NULL OR montant_seuil_max >= ?1) ORDER BY ordre_priorite ASC ALLOW FILTERING")
    List<WorkflowApprobation> findApplicableWorkflows(TypeWorkflow typeWorkflow, BigDecimal montant);

    @Query("SELECT * FROM workflows_approbation WHERE actif = true ALLOW FILTERING")
    List<WorkflowApprobation> findActiveWorkflows();

    @Query("SELECT * FROM workflows_approbation WHERE actif = false ALLOW FILTERING")
    List<WorkflowApprobation> findInactiveWorkflows();

    @Query("SELECT * FROM workflows_approbation WHERE escalade_automatique = true ALLOW FILTERING")
    List<WorkflowApprobation> findWorkflowsWithAutoEscalation();

    @Query("SELECT * FROM workflows_approbation WHERE notification_email = true ALLOW FILTERING")
    List<WorkflowApprobation> findWorkflowsWithEmailNotification();
}