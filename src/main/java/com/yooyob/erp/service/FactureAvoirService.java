package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.FactureAvoirCreateRequest;
import com.yooyob.erp.dto.response.FactureAvoirResponse;
import com.yooyob.erp.model.enums.StatutAvoir;
import com.yooyob.erp.model.enums.TypeAvoir;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface FactureAvoirService {

    FactureAvoirResponse createFactureAvoir(FactureAvoirCreateRequest request);

    FactureAvoirResponse updateFactureAvoir(UUID idAvoir, FactureAvoirCreateRequest request);

    FactureAvoirResponse getFactureAvoir(UUID idAvoir);

    FactureAvoirResponse getFactureAvoirByNumero(String numeroAvoir);

    List<FactureAvoirResponse> getAllFacturesAvoir();

    Page<FactureAvoirResponse> getFacturesAvoirPaginated(Pageable pageable);

    List<FactureAvoirResponse> getFacturesAvoirByClient(UUID idClient);

    List<FactureAvoirResponse> getFacturesAvoirByFactureOrigine(UUID idFactureOrigine);

    List<FactureAvoirResponse> getFacturesAvoirByStatut(StatutAvoir statut);

    List<FactureAvoirResponse> getFacturesAvoirByType(TypeAvoir typeAvoir);

    List<FactureAvoirResponse> getFacturesAvoirByDateRange(LocalDate startDate, LocalDate endDate);

    void deleteFactureAvoir(UUID idAvoir);

    FactureAvoirResponse changerStatut(UUID idAvoir, StatutAvoir nouveauStatut);

    FactureAvoirResponse validerFactureAvoir(UUID idAvoir, UUID approuvePar);

    FactureAvoirResponse appliquerAvoir(UUID idAvoir, BigDecimal montantApplique);

    FactureAvoirResponse rembourserAvoir(UUID idAvoir, BigDecimal montantRembourse, String modeRemboursement, String referenceRemboursement);

    FactureAvoirResponse envoyerParEmail(UUID idAvoir);

    String genererPdf(UUID idAvoir);

    FactureAvoirResponse calculerTotaux(UUID idAvoir);

    FactureAvoirResponse creerAvoirDepuisFacture(UUID idFacture, List<UUID> lignesFacture, String motif, TypeAvoir typeAvoir);

    List<FactureAvoirResponse> getAvoirsNonTotalementAppliques();

    List<FactureAvoirResponse> getAvoirsApprouves();

    BigDecimal getMontantRestantAAppliquer(UUID idAvoir);

    boolean isAvoirModifiable(UUID idAvoir);

    boolean isAvoirApplicable(UUID idAvoir);
}