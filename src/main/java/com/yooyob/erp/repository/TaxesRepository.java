package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Taxes;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaxesRepository extends CassandraRepository<Taxes, UUID> {

    Optional<Taxes> findByNomTaxe(String nomTaxe);

    List<Taxes> findByTypeTaxe(String typeTaxe);

    List<Taxes> findByActif(Boolean actif);

    List<Taxes> findByPorteTaxe(String porteTaxe);

    List<Taxes> findByPositionFiscale(String positionFiscale);

    @Query("SELECT * FROM taxes WHERE actif = true ALLOW FILTERING")
    List<Taxes> findAllActiveTaxes();

    @Query("SELECT * FROM taxes WHERE type_taxe = ?0 AND actif = true ALLOW FILTERING")
    List<Taxes> findActiveByTypeTaxe(String typeTaxe);

    @Query("SELECT * FROM taxes WHERE calcul_taxe >= ?0 AND calcul_taxe <= ?1 ALLOW FILTERING")
    List<Taxes> findByCalculTaxeBetween(BigDecimal minTaux, BigDecimal maxTaux);

    @Query("SELECT * FROM taxes WHERE montant >= ?0 AND montant <= ?1 ALLOW FILTERING")
    List<Taxes> findByMontantBetween(BigDecimal minMontant, BigDecimal maxMontant);

    @Query("SELECT COUNT(*) FROM taxes WHERE actif = true")
    Long countActiveTaxes();

    @Query("SELECT COUNT(*) FROM taxes WHERE type_taxe = ?0")
    Long countByTypeTaxe(String typeTaxe);

    boolean existsByNomTaxe(String nomTaxe);
}