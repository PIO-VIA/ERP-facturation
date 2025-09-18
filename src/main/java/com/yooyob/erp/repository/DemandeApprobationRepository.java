package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.DemandeApprobation;
import com.yooyob.erp.model.enums.StatutApprobation;
import com.yooyob.erp.model.enums.TypeWorkflow;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DemandeApprobationRepository extends CassandraRepository<DemandeApprobation, UUID> {

    Optional<DemandeApprobation> findByIdDemande(UUID idDemande);

    @Query("SELECT * FROM demandes_approbation WHERE id_objet = ?0 AND type_objet = ?1 ALLOW FILTERING")
    List<DemandeApprobation> findByIdObjetAndTypeObjet(UUID idObjet, String typeObjet);

    @Query("SELECT * FROM demandes_approbation WHERE demandeur = ?0 ALLOW FILTERING")
    List<DemandeApprobation> findByDemandeur(UUID demandeur);

    @Query("SELECT * FROM demandes_approbation WHERE statut = ?0 ALLOW FILTERING")
    List<DemandeApprobation> findByStatut(StatutApprobation statut);

    @Query("SELECT * FROM demandes_approbation WHERE type_workflow = ?0 ALLOW FILTERING")
    List<DemandeApprobation> findByTypeWorkflow(TypeWorkflow typeWorkflow);

    @Query("SELECT * FROM demandes_approbation WHERE ?0 IN approbateurs_en_attente ALLOW FILTERING")
    List<DemandeApprobation> findByApprobateurEnAttente(UUID approbateur);

    @Query("SELECT * FROM demandes_approbation WHERE date_expiration < ?0 AND statut = 'EN_ATTENTE' ALLOW FILTERING")
    List<DemandeApprobation> findExpiredDemandes(LocalDateTime currentDateTime);

    @Query("SELECT * FROM demandes_approbation WHERE date_creation >= ?0 AND date_creation <= ?1 ALLOW FILTERING")
    List<DemandeApprobation> findByDateCreationBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT * FROM demandes_approbation WHERE statut IN ('EN_ATTENTE', 'EN_COURS') ALLOW FILTERING")
    List<DemandeApprobation> findPendingDemandes();

    @Query("SELECT * FROM demandes_approbation WHERE escalades_effectuees > 0 ALLOW FILTERING")
    List<DemandeApprobation> findEscalatedDemandes();

    @Query("SELECT * FROM demandes_approbation WHERE priorite = ?0 ALLOW FILTERING")
    List<DemandeApprobation> findByPriorite(Integer priorite);

    Slice<DemandeApprobation> findAll(Pageable pageable);
}