package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.DevisCreateRequest;
import com.yooyob.erp.dto.response.DevisResponse;
import com.yooyob.erp.model.enums.StatutDevis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DevisService {

    DevisResponse createDevis(DevisCreateRequest request);

    DevisResponse updateDevis(UUID idDevis, DevisCreateRequest request);

    DevisResponse getDevis(UUID idDevis);

    DevisResponse getDevisByNumero(String numeroDevis);

    List<DevisResponse> getAllDevis();

    Page<DevisResponse> getDevisPaginated(Pageable pageable);

    List<DevisResponse> getDevisByClient(UUID idClient);

    List<DevisResponse> getDevisByStatut(StatutDevis statut);

    List<DevisResponse> getDevisByDateRange(LocalDate startDate, LocalDate endDate);

    void deleteDevis(UUID idDevis);

    DevisResponse changerStatut(UUID idDevis, StatutDevis nouveauStatut);

    DevisResponse changerStatut(UUID idDevis, StatutDevis nouveauStatut, String motif);

    DevisResponse accepterDevis(UUID idDevis);

    DevisResponse refuserDevis(UUID idDevis, String motifRefus);

    UUID convertirEnFacture(UUID idDevis);

    DevisResponse envoyerParEmail(UUID idDevis);

    String genererPdf(UUID idDevis);

    DevisResponse dupliquerDevis(UUID idDevis);

    List<DevisResponse> getDevisExpires();

    void marquerDevisCommeExpires();

    List<DevisResponse> getDevisConverties();

    DevisResponse calculerTotaux(UUID idDevis);

    boolean isDevisModifiable(UUID idDevis);

    boolean isDevisExpire(UUID idDevis);
}