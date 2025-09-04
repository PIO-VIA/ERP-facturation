package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Fournisseur;
import com.yooyob.erp.model.enums.TypeClient;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FournisseurRepository extends CassandraRepository<Fournisseur, UUID> {

    Optional<Fournisseur> findByUsername(String username);

    Optional<Fournisseur> findByEmail(String email);

    Optional<Fournisseur> findByCodeFournisseur(String codeFournisseur);

    List<Fournisseur> findByTypeFournisseur(TypeClient typeFournisseur);

    List<Fournisseur> findByActif(Boolean actif);

    List<Fournisseur> findByCategorie(String categorie);

    @Query("SELECT * FROM fournisseurs WHERE actif = true ALLOW FILTERING")
    List<Fournisseur> findAllActiveFournisseurs();

    @Query("SELECT * FROM fournisseurs WHERE type_fournisseur = ?0 AND actif = true ALLOW FILTERING")
    List<Fournisseur> findActiveFournisseursByType(TypeClient typeFournisseur);

    @Query("SELECT * FROM fournisseurs WHERE username LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Fournisseur> findByUsernameContaining(String username);

    @Query("SELECT * FROM fournisseurs WHERE solde_courant > ?0 ALLOW FILTERING")
    List<Fournisseur> findFournisseursWithSoldeGreaterThan(Double solde);

    @Query("SELECT COUNT(*) FROM fournisseurs WHERE actif = true")
    Long countActiveFournisseurs();

    @Query("SELECT COUNT(*) FROM fournisseurs WHERE type_fournisseur = ?0")
    Long countFournisseursByType(TypeClient typeFournisseur);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCodeFournisseur(String codeFournisseur);
}