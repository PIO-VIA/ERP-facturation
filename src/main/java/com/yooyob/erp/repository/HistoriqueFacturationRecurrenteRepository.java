package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.HistoriqueFacturationRecurrente;
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
public interface HistoriqueFacturationRecurrenteRepository extends CassandraRepository<HistoriqueFacturationRecurrente, UUID> {

    Optional<HistoriqueFacturationRecurrente> findByIdHistorique(UUID idHistorique);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE id_abonnement = ?0 ALLOW FILTERING")
    List<HistoriqueFacturationRecurrente> findByIdAbonnement(UUID idAbonnement);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE id_facture_generee = ?0 ALLOW FILTERING")
    Optional<HistoriqueFacturationRecurrente> findByIdFactureGeneree(UUID idFactureGeneree);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE succes = ?0 ALLOW FILTERING")
    List<HistoriqueFacturationRecurrente> findBySucces(Boolean succes);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE date_execution >= ?0 AND date_execution <= ?1 ALLOW FILTERING")
    List<HistoriqueFacturationRecurrente> findByDateExecutionBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE id_abonnement = ?0 AND succes = true ORDER BY date_execution DESC LIMIT 1 ALLOW FILTERING")
    Optional<HistoriqueFacturationRecurrente> findLastSuccessfulExecution(UUID idAbonnement);

    @Query("SELECT * FROM historique_facturation_recurrente WHERE succes = false ALLOW FILTERING")
    List<HistoriqueFacturationRecurrente> findFailedExecutions();

    @Query("SELECT * FROM historique_facturation_recurrente WHERE email_envoye = false AND succes = true ALLOW FILTERING")
    List<HistoriqueFacturationRecurrente> findSuccessfulWithoutEmail();

    Slice<HistoriqueFacturationRecurrente> findAll(Pageable pageable);
}