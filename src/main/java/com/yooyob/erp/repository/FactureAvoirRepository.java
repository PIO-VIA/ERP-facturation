package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.FactureAvoir;
import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
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
public interface FactureAvoirRepository extends CassandraRepository<FactureAvoir, UUID> {

    Optional<FactureAvoir> findByIdAvoir(UUID idAvoir);

    Optional<FactureAvoir> findByNumeroAvoir(String numeroAvoir);

    @Query("SELECT * FROM factures_avoir WHERE id_client = ?0 ALLOW FILTERING")
    List<FactureAvoir> findByIdClient(UUID idClient);

    @Query("SELECT * FROM factures_avoir WHERE id_facture_origine = ?0 ALLOW FILTERING")
    List<FactureAvoir> findByIdFactureOrigine(UUID idFactureOrigine);

    @Query("SELECT * FROM factures_avoir WHERE statut = ?0 ALLOW FILTERING")
    List<FactureAvoir> findByStatut(StatutAvoir statut);

    @Query("SELECT * FROM factures_avoir WHERE type_avoir = ?0 ALLOW FILTERING")
    List<FactureAvoir> findByTypeAvoir(TypeAvoir typeAvoir);

    @Query("SELECT * FROM factures_avoir WHERE date_creation >= ?0 AND date_creation <= ?1 ALLOW FILTERING")
    List<FactureAvoir> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM factures_avoir WHERE statut = ?0 AND date_creation >= ?1 AND date_creation <= ?2 ALLOW FILTERING")
    List<FactureAvoir> findByStatutAndDateCreationBetween(StatutAvoir statut, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM factures_avoir WHERE id_client = ?0 AND statut = ?1 ALLOW FILTERING")
    List<FactureAvoir> findByIdClientAndStatut(UUID idClient, StatutAvoir statut);

    @Query("SELECT * FROM factures_avoir WHERE envoye_par_email = true ALLOW FILTERING")
    List<FactureAvoir> findSentByEmail();

    @Query("SELECT * FROM factures_avoir WHERE statut = 'VALIDE' AND montant_applique < montant_total ALLOW FILTERING")
    List<FactureAvoir> findAvoirsNonTotalementAppliques();

    @Query("SELECT * FROM factures_avoir WHERE approuve_par IS NOT NULL ALLOW FILTERING")
    List<FactureAvoir> findAvoirsApprouves();

    Slice<FactureAvoir> findAll(Pageable pageable);
}