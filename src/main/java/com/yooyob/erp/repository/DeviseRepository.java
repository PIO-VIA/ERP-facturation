package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Devise;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviseRepository extends CassandraRepository<Devise, UUID> {

    Page<Devise> findAll(Pageable pageable);

    Optional<Devise> findByNomDevise(String nomDevise);

    Optional<Devise> findBySymbole(String symbole);

    List<Devise> findByActif(Boolean actif);

    @Query("SELECT * FROM devises WHERE actif = true ALLOW FILTERING")
    List<Devise> findAllActiveDevises();

    @Query("SELECT * FROM devises WHERE nom_devise LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Devise> findByNomDeviseContaining(String nomDevise);

    @Query("SELECT COUNT(*) FROM devises WHERE actif = true")
    Long countActiveDevises();

    boolean existsByNomDevise(String nomDevise);

    boolean existsBySymbole(String symbole);
}