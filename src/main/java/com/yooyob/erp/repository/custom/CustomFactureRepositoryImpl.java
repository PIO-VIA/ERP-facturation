package com.yooyob.erp.repository.custom;

import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.enums.StatutFacture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomFactureRepositoryImpl implements CustomFactureRepository {

    private final CassandraTemplate cassandraTemplate;

    @Override
    public List<Facture> findFacturesWithFilters(UUID clientId, StatutFacture etat,
                                                 LocalDate dateStart, LocalDate dateEnd, BigDecimal montantMin,
                                                 BigDecimal montantMax, String devise) {

        Query query = Query.empty();

        if (clientId != null) {
            query = query.and(Criteria.where("id_client").is(clientId));
        }

        if (etat != null) {
            query = query.and(Criteria.where("etat").is(etat));
        }

        if (dateStart != null && dateEnd != null) {
            query = query.and(Criteria.where("date_facturation").gte(dateStart).lte(dateEnd));
        }

        if (montantMin != null) {
            query = query.and(Criteria.where("montant_total").gte(montantMin));
        }

        if (montantMax != null) {
            query = query.and(Criteria.where("montant_total").lte(montantMax));
        }

        if (devise != null && !devise.isEmpty()) {
            query = query.and(Criteria.where("devise").is(devise));
        }

        return cassandraTemplate.select(query, Facture.class);
    }

    @Override
    public Page<Facture> findFacturesWithFiltersAndPagination(UUID clientId, StatutFacture etat,
                                                              LocalDate dateStart, LocalDate dateEnd, BigDecimal montantMin,
                                                              BigDecimal montantMax, String devise, Pageable pageable) {

        List<Facture> factures = findFacturesWithFilters(clientId, etat, dateStart,
                dateEnd, montantMin, montantMax, devise);

        // Implémentation manuelle de la pagination pour Cassandra
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), factures.size());

        List<Facture> pageContent = factures.subList(start, end);

        return new PageImpl<>(pageContent, pageable, factures.size());
    }

    @Override
    public Map<String, Object> getFactureStatisticsByPeriod(LocalDate startDate, LocalDate endDate) {
        List<Facture> factures = findFacturesWithFilters(null, null, startDate, endDate, null, null, null);

        Map<String, Object> stats = new HashMap<>();

        stats.put("nombreFactures", (long) factures.size());
        stats.put("montantTotal", factures.stream()
                .map(Facture::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        stats.put("montantMoyen", factures.isEmpty() ? BigDecimal.ZERO :
                factures.stream()
                        .map(Facture::getMontantTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(factures.size()), 2, RoundingMode.HALF_UP));

        Map<StatutFacture, Long> repartitionStatut = factures.stream()
                .collect(Collectors.groupingBy(Facture::getEtat, Collectors.counting()));
        stats.put("repartitionStatut", repartitionStatut);

        return stats;
    }

    @Override
    public Map<String, BigDecimal> getChiffreAffairesByMonth(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Facture> factures = findFacturesWithFilters(null, null, startDate, endDate, null, null, null);

        return factures.stream()
                .filter(f -> f.getEtat() == StatutFacture.PAYE || f.getEtat() == StatutFacture.PARTIELLEMENT_PAYE)
                .collect(Collectors.groupingBy(
                        f -> f.getDateFacturation().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.reducing(BigDecimal.ZERO, Facture::getMontantTotal, BigDecimal::add)
                ));
    }

    @Override
    public List<Map<String, Object>> getTopClientsByChiffreAffaires(int limit) {
        List<Facture> factures = cassandraTemplate.select(Query.empty(), Facture.class);

        return factures.stream()
                .filter(f -> f.getEtat() == StatutFacture.PAYE || f.getEtat() == StatutFacture.PARTIELLEMENT_PAYE)
                .collect(Collectors.groupingBy(
                        f -> Map.of("idClient", f.getIdClient(), "nomClient", f.getNomClient()),
                        Collectors.reducing(BigDecimal.ZERO, Facture::getMontantTotal, BigDecimal::add)
                ))
                .entrySet()
                .stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> clientData = new HashMap<>(entry.getKey());
                    clientData.put("chiffreAffaires", entry.getValue());
                    return clientData;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Facture> getOverdueFactures() {
        LocalDate today = LocalDate.now();

        return cassandraTemplate.select(Query.empty(), Facture.class)
                .stream()
                .filter(f -> f.getDateEcheance().isBefore(today) &&
                        (f.getEtat() == StatutFacture.ENVOYE || f.getEtat() == StatutFacture.PARTIELLEMENT_PAYE))
                .collect(Collectors.toList());
    }

    @Override
    public Map<UUID, BigDecimal> getMontantImpayeByClient() {
        List<Facture> facturesImpayes = cassandraTemplate.select(Query.empty(), Facture.class)
                .stream()
                .filter(f -> f.getMontantRestant().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        return facturesImpayes.stream()
                .collect(Collectors.groupingBy(
                        Facture::getIdClient,
                        Collectors.reducing(BigDecimal.ZERO, Facture::getMontantRestant, BigDecimal::add)
                ));
    }

    @Override
    public Map<String, BigDecimal> getChiffreAffairesByQuarter(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Facture> factures = findFacturesWithFilters(null, null, startDate, endDate, null, null, null);

        Map<String, BigDecimal> quarterlyCA = new LinkedHashMap<>();
        quarterlyCA.put("Q1", BigDecimal.ZERO);
        quarterlyCA.put("Q2", BigDecimal.ZERO);
        quarterlyCA.put("Q3", BigDecimal.ZERO);
        quarterlyCA.put("Q4", BigDecimal.ZERO);

        factures.stream()
                .filter(f -> f.getEtat() == StatutFacture.PAYE || f.getEtat() == StatutFacture.PARTIELLEMENT_PAYE)
                .forEach(facture -> {
                    int month = facture.getDateFacturation().getMonthValue();
                    String quarter = "Q" + ((month - 1) / 3 + 1);
                    quarterlyCA.merge(quarter, facture.getMontantTotal(), BigDecimal::add);
                });

        return quarterlyCA;
    }

    @Override
    public List<Facture> getFacturesApprochantEcheance(int nombreJours) {
        LocalDate dateLimit = LocalDate.now().plusDays(nombreJours);

        return cassandraTemplate.select(Query.empty(), Facture.class)
                .stream()
                .filter(f -> f.getDateEcheance().isBefore(dateLimit) &&
                        f.getDateEcheance().isAfter(LocalDate.now()) &&
                        (f.getEtat() == StatutFacture.ENVOYE || f.getEtat() == StatutFacture.PARTIELLEMENT_PAYE))
                .sorted(Comparator.comparing(Facture::getDateEcheance))
                .collect(Collectors.toList());
    }

    @Override
    public Map<StatutFacture, Long> getFactureCountByStatut() {
        List<Facture> factures = cassandraTemplate.select(Query.empty(), Facture.class);

        return factures.stream()
                .collect(Collectors.groupingBy(Facture::getEtat, Collectors.counting()));
    }

    @Override
    public BigDecimal getMontantMoyenFactures(LocalDate startDate, LocalDate endDate) {
        List<Facture> factures = findFacturesWithFilters(null, null, startDate, endDate, null, null, null);

        if (factures.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = factures.stream()
                .map(Facture::getMontantTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(factures.size()), 2, RoundingMode.HALF_UP);
    }
}