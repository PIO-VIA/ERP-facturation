package com.yooyob.erp.repository;

import com.yooyob.erp.model.entity.Journal;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JournalRepository extends CassandraRepository<Journal, UUID> {

    Optional<Journal> findByNomJournal(String nomJournal);

    List<Journal> findByType(String type);

    @Query("SELECT * FROM journals WHERE nom_journal LIKE '%' + ?0 + '%' ALLOW FILTERING")
    List<Journal> findByNomJournalContaining(String nomJournal);

    @Query("SELECT COUNT(*) FROM journals WHERE type = ?0")
    Long countByType(String type);

    boolean existsByNomJournal(String nomJournal);
}