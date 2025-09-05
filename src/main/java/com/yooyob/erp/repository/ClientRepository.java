package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Client;
import com.yooyob.erp.model.enums.TypeClient;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientRepository extends CassandraRepository<Client, UUID> {

    Page<Client> findAll(Pageable pageable);

    Optional<Client> findByUsername(String username);

    Optional<Client> findByEmail(String email);

    Optional<Client> findByCodeClient(String codeClient);

    List<Client> findByTypeClient(TypeClient typeClient);

    List<Client> findByActif(Boolean actif);

    List<Client> findByCategorie(String categorie);

    @Query("SELECT * FROM clients WHERE actif = true ALLOW FILTERING")
    List<Client> findAllActiveClients();

    @Query("SELECT * FROM clients WHERE type_client = ?0 AND actif = true ALLOW FILTERING")
    List<Client> findActiveClientsByType(TypeClient typeClient);

    @Query("SELECT * FROM clients WHERE username LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Client> findByUsernameContaining(String username);

    @Query("SELECT * FROM clients WHERE email LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Client> findByEmailContaining(String email);

    @Query("SELECT * FROM clients WHERE solde_courant > ?0 ALLOW FILTERING")
    List<Client> findClientsWithSoldeGreaterThan(Double solde);

    @Query("SELECT * FROM clients WHERE solde_courant < 0 ALLOW FILTERING")
    List<Client> findClientsWithNegativeBalance();

    @Query("SELECT COUNT(*) FROM clients WHERE actif = true")
    Long countActiveClients();

    @Query("SELECT COUNT(*) FROM clients WHERE type_client = ?0")
    Long countClientsByType(TypeClient typeClient);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByCodeClient(String codeClient);
}