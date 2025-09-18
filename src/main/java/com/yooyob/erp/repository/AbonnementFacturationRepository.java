package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.AbonnementFacturation;
import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AbonnementFacturationRepository extends CassandraRepository<AbonnementFacturation, UUID> {

    Optional<AbonnementFacturation> findByIdAbonnement(UUID idAbonnement);

    @Query("SELECT * FROM abonnements_facturation WHERE id_client = ?0 ALLOW FILTERING")
    List<AbonnementFacturation> findByIdClient(UUID idClient);

    @Query("SELECT * FROM abonnements_facturation WHERE statut = ?0 ALLOW FILTERING")
    List<AbonnementFacturation> findByStatut(StatutAbonnement statut);

    @Query("SELECT * FROM abonnements_facturation WHERE frequence_recurrence = ?0 ALLOW FILTERING")
    List<AbonnementFacturation> findByFrequenceRecurrence(FrequenceRecurrence frequence);

    @Query("SELECT * FROM abonnements_facturation WHERE actif = true AND statut = 'ACTIF' ALLOW FILTERING")
    List<AbonnementFacturation> findActiveAbonnements();

    @Query("SELECT * FROM abonnements_facturation WHERE date_prochaine_facturation <= ?0 AND actif = true AND statut = 'ACTIF' ALLOW FILTERING")
    List<AbonnementFacturation> findAbonnementsAFacturer(LocalDate date);

    @Query("SELECT * FROM abonnements_facturation WHERE date_fin IS NOT NULL AND date_fin < ?0 AND statut != 'EXPIRE' ALLOW FILTERING")
    List<AbonnementFacturation> findAbonnementsExpires(LocalDate currentDate);

    @Query("SELECT * FROM abonnements_facturation WHERE nombre_max_factures IS NOT NULL AND nombre_factures_generees >= nombre_max_factures AND statut = 'ACTIF' ALLOW FILTERING")
    List<AbonnementFacturation> findAbonnementsLimiteAtteinte();

    @Query("SELECT * FROM abonnements_facturation WHERE id_client = ?0 AND statut = ?1 ALLOW FILTERING")
    List<AbonnementFacturation> findByIdClientAndStatut(UUID idClient, StatutAbonnement statut);

    @Query("SELECT * FROM abonnements_facturation WHERE date_debut >= ?0 AND date_debut <= ?1 ALLOW FILTERING")
    List<AbonnementFacturation> findByDateDebutBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM abonnements_facturation WHERE derniere_erreur IS NOT NULL ALLOW FILTERING")
    List<AbonnementFacturation> findAbonnementsAvecErreurs();

    Slice<AbonnementFacturation> findAll(Pageable pageable);
}