package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Comptable;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ComptableRepository extends CassandraRepository<Comptable, UUID> {

    Page<Comptable> findAll(Pageable pageable);

    Optional<Comptable> findByUsername(String username);

    boolean existsByUsername(String username);
}