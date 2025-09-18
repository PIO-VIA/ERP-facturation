package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Devis;
import com.yooyob.erp.model.enums.StatutDevis;
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
public interface DevisRepository extends CassandraRepository<Devis, UUID> {

    Optional<Devis> findByIdDevis(UUID idDevis);

    Optional<Devis> findByNumeroDevis(String numeroDevis);

    @Query("SELECT * FROM devis WHERE id_client = ?0 ALLOW FILTERING")
    List<Devis> findByIdClient(UUID idClient);

    @Query("SELECT * FROM devis WHERE statut = ?0 ALLOW FILTERING")
    List<Devis> findByStatut(StatutDevis statut);

    @Query("SELECT * FROM devis WHERE date_validite < ?0 AND statut IN ('ENVOYE', 'BROUILLON') ALLOW FILTERING")
    List<Devis> findExpiredDevis(LocalDate currentDate);

    @Query("SELECT * FROM devis WHERE date_creation >= ?0 AND date_creation <= ?1 ALLOW FILTERING")
    List<Devis> findByDateCreationBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM devis WHERE statut = ?0 AND date_creation >= ?1 AND date_creation <= ?2 ALLOW FILTERING")
    List<Devis> findByStatutAndDateCreationBetween(StatutDevis statut, LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM devis WHERE id_client = ?0 AND statut = ?1 ALLOW FILTERING")
    List<Devis> findByIdClientAndStatut(UUID idClient, StatutDevis statut);

    @Query("SELECT * FROM devis WHERE envoye_par_email = true ALLOW FILTERING")
    List<Devis> findSentByEmail();

    @Query("SELECT * FROM devis WHERE id_facture_convertie IS NOT NULL ALLOW FILTERING")
    List<Devis> findConvertedToInvoice();

    Slice<Devis> findAll(Pageable pageable);
}