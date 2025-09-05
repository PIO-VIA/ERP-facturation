package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.ProduitVente;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProduitVenteRepository extends CassandraRepository<ProduitVente, UUID> {

    Page<ProduitVente> findAll(Pageable pageable);

    Optional<ProduitVente> findByReference(String reference);

    Optional<ProduitVente> findByCodeBarre(String codeBarre);

    List<ProduitVente> findByNomProduit(String nomProduit);

    List<ProduitVente> findByTypeProduit(String typeProduit);

    List<ProduitVente> findByCategorie(String categorie);

    List<ProduitVente> findByActive(Boolean active);

    @Query("SELECT * FROM produits_vente WHERE active = true ALLOW FILTERING")
    List<ProduitVente> findAllActiveProducts();

    @Query("SELECT * FROM produits_vente WHERE nom_produit LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<ProduitVente> findByNomProduitContaining(String nomProduit);

    @Query("SELECT * FROM produits_vente WHERE prix_vente >= ?0 AND prix_vente <= ?1 ALLOW FILTERING")
    List<ProduitVente> findByPrixVenteBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT * FROM produits_vente WHERE cout >= ?0 AND cout <= ?1 ALLOW FILTERING")
    List<ProduitVente> findByCoutBetween(BigDecimal minCost, BigDecimal maxCost);

    @Query("SELECT * FROM produits_vente WHERE categorie = ?0 AND active = true ALLOW FILTERING")
    List<ProduitVente> findActiveByCategorieAndActive(String categorie);

    @Query("SELECT * FROM produits_vente WHERE type_produit = ?0 AND active = true ALLOW FILTERING")
    List<ProduitVente> findActiveByTypeProduit(String typeProduit);

    @Query("SELECT COUNT(*) FROM produits_vente WHERE active = true")
    Long countActiveProducts();

    @Query("SELECT COUNT(*) FROM produits_vente WHERE categorie = ?0")
    Long countByCategorie(String categorie);

    @Query("SELECT COUNT(*) FROM produits_vente WHERE type_produit = ?0")
    Long countByTypeProduit(String typeProduit);

    boolean existsByReference(String reference);

    boolean existsByCodeBarre(String codeBarre);
}