package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Remboursement;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface RemboursementRepository extends CassandraRepository<Remboursement, UUID> {

    List<Remboursement> findByIdClient(UUID idClient);

    List<Remboursement> findByIdFacture(UUID idFacture);

    List<Remboursement> findByStatut(String statut);

    List<Remboursement> findByDevise(String devise);

    @Query("SELECT * FROM remboursements WHERE date_facturation >= ?0 AND date_facturation <= ?1 ALLOW FILTERING")
    List<Remboursement> findByDateFacturationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM remboursements WHERE date_echeance >= ?0 AND date_echeance <= ?1 ALLOW FILTERING")
    List<Remboursement> findByDateEcheanceBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM remboursements WHERE date_comptable >= ?0 AND date_comptable <= ?1 ALLOW FILTERING")
    List<Remboursement> findByDateComptableBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM remboursements WHERE montant >= ?0 AND montant <= ?1 ALLOW FILTERING")
    List<Remboursement> findByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT * FROM remboursements WHERE statut = ?0 AND date_echeance < ?1 ALLOW FILTERING")
    List<Remboursement> findOverdueRemboursements(String statut, LocalDate currentDate);

    @Query("SELECT * FROM remboursements WHERE id_client = ?0 AND statut = ?1 ALLOW FILTERING")
    List<Remboursement> findByClientAndStatut(UUID idClient, String statut);

    @Query("SELECT SUM(montant) FROM remboursements WHERE id_client = ?0")
    BigDecimal sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(montant) FROM remboursements WHERE statut = ?0")
    BigDecimal sumMontantByStatut(String statut);

    @Query("SELECT COUNT(*) FROM remboursements WHERE statut = ?0")
    Long countByStatut(String statut);

    @Query("SELECT COUNT(*) FROM remboursements WHERE id_client = ?0")
    Long countByIdClient(UUID idClient);
}