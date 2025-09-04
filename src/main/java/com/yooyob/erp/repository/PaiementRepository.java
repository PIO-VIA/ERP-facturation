package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Paiement;
import com.yooyob.erp.model.enums.TypePaiement;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PaiementRepository extends CassandraRepository<Paiement, UUID> {

    List<Paiement> findByIdClient(UUID idClient);

    List<Paiement> findByIdFacture(UUID idFacture);

    List<Paiement> findByModePaiement(TypePaiement modePaiement);

    List<Paiement> findByJournal(String journal);

    @Query("SELECT * FROM paiements WHERE date >= ?0 AND date <= ?1 ALLOW FILTERING")
    List<Paiement> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM paiements WHERE montant >= ?0 AND montant <= ?1 ALLOW FILTERING")
    List<Paiement> findByMontantBetween(BigDecimal minAmount, BigDecimal maxAmount);

    @Query("SELECT * FROM paiements WHERE id_client = ?0 AND date >= ?1 AND date <= ?2 ALLOW FILTERING")
    List<Paiement> findByClientAndDateBetween(UUID idClient, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM paiements WHERE id_facture = ?0 ORDER BY date DESC ALLOW FILTERING")
    List<Paiement> findByFactureOrderByDateDesc(UUID idFacture);

    @Query("SELECT * FROM paiements WHERE mode_paiement = ?0 AND date >= ?1 AND date <= ?2 ALLOW FILTERING")
    List<Paiement> findByModePaiementAndDateBetween(TypePaiement modePaiement, LocalDate startDate, LocalDate endDate);

    // Requêtes d'agrégation
    @Query("SELECT SUM(montant) FROM paiements WHERE id_client = ?0")
    BigDecimal sumMontantByClient(UUID idClient);

    @Query("SELECT SUM(montant) FROM paiements WHERE id_facture = ?0")
    BigDecimal sumMontantByFacture(UUID idFacture);

    @Query("SELECT SUM(montant) FROM paiements WHERE date >= ?0 AND date <= ?1")
    BigDecimal sumMontantByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(*) FROM paiements WHERE id_client = ?0")
    Long countByIdClient(UUID idClient);

    @Query("SELECT COUNT(*) FROM paiements WHERE mode_paiement = ?0")
    Long countByModePaiement(TypePaiement modePaiement);

    @Query("SELECT COUNT(*) FROM paiements WHERE date >= ?0 AND date <= ?1")
    Long countByDateBetween(LocalDate startDate, LocalDate endDate);
}