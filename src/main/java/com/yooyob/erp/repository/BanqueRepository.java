package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Banque;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BanqueRepository extends CassandraRepository<Banque, UUID> {

    Optional<Banque> findByNumeroCompte(String numeroCompte);

    List<Banque> findByBanque(String banque);

    @Query("SELECT * FROM banques WHERE banque LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Banque> findByBanqueContaining(String banque);

    @Query("SELECT * FROM banques WHERE numero_compte LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Banque> findByNumeroCompteContaining(String numeroCompte);

    boolean existsByNumeroCompte(String numeroCompte);
}