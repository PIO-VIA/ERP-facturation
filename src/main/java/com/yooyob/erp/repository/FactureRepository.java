package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.enums.StatutFacture;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FactureRepository extends CassandraRepository<Facture, UUID> {

    Optional<Facture> findByNumeroFacture(String numeroFacture);

    List<Facture> findByIdClient(UUID idClient);

    List<Facture> findByEtat(StatutFacture etat);

    List<Facture> findByType(String type);

    @Query("SELECT * FROM factures WHERE id_client = ?0 AND etat = ?1 ALLOW FILTERING")
    List<Facture> findByClientAndEtat(UUID idClient, StatutFacture etat);

    @Query("SELECT * FROM factures WHERE date_facturation >= ?0 AND date_facturation <= ?1 ALLOW FILTERING")
    List<Facture> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM factures WHERE date_echeance >= ?0 AND date_echeance <= ?1 ALLOW FILTERING")
    List<Facture> findByDateEcheanceBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM factures WHERE date_echeance < ?0 AND etat IN ('ENVOYE', 'PARTIELLEMENT_PAYE') ALLOW FILTERING")
    List<Facture> findOverdueFactures(LocalDate currentDate);

    @Query("SELECT * FROM factures WHERE montant_total >= ?0 AND montant_total <= ?1 ALLOW FILTERING")
    List<Facture> findByMontantTotalBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT * FROM factures WHERE montant_restant > 0 ALLOW FILTERING")
    List<Facture> findUnpaidFactures();

    @Query("SELECT * FROM factures WHERE devise = ?0 ALLOW FILTERING")
    List<Facture> findByDevise(String devise);

    @Query("SELECT * FROM factures WHERE envoye_par_email = ?0 ALLOW FILTERING")
    List<Facture> findByEnvoyeParEmail(Boolean envoyeParEmail);

    // Requêtes pour les statistiques
    @Query("SELECT COUNT(*) FROM factures WHERE etat = ?0")
    Long countByEtat(StatutFacture etat);

    @Query("SELECT COUNT(*) FROM factures WHERE id_client = ?0")
    Long countByIdClient(UUID idClient);

    @Query("SELECT COUNT(*) FROM factures WHERE date_facturation >= ?0 AND date_facturation <= ?1")
    Long countByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    // Requêtes avec pagination
    Slice<Facture> findByIdClient(UUID idClient, Pageable pageable);

    Slice<Facture> findByEtat(StatutFacture etat, Pageable pageable);

    @Query("SELECT * FROM factures WHERE date_facturation >= ?0 AND date_facturation <= ?1 ALLOW FILTERING")
    Slice<Facture> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    boolean existsByNumeroFacture(String numeroFacture);
}