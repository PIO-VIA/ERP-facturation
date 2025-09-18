package com.yooyob.erp.service;

import com.yooyob.erp.dto.request.AbonnementFacturationCreateRequest;
import com.yooyob.erp.dto.response.AbonnementFacturationResponse;
import com.yooyob.erp.dto.response.HistoriqueFacturationRecurrenteResponse;
import com.yooyob.erp.model.enums.FrequenceRecurrence;
import com.yooyob.erp.model.enums.StatutAbonnement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AbonnementFacturationService {

    AbonnementFacturationResponse createAbonnement(AbonnementFacturationCreateRequest request);

    AbonnementFacturationResponse updateAbonnement(UUID idAbonnement, AbonnementFacturationCreateRequest request);

    AbonnementFacturationResponse getAbonnement(UUID idAbonnement);

    List<AbonnementFacturationResponse> getAllAbonnements();

    Page<AbonnementFacturationResponse> getAbonnementsPaginated(Pageable pageable);

    List<AbonnementFacturationResponse> getAbonnementsByClient(UUID idClient);

    List<AbonnementFacturationResponse> getAbonnementsByStatut(StatutAbonnement statut);

    List<AbonnementFacturationResponse> getAbonnementsByFrequence(FrequenceRecurrence frequence);

    void deleteAbonnement(UUID idAbonnement);

    AbonnementFacturationResponse changerStatut(UUID idAbonnement, StatutAbonnement nouveauStatut);

    AbonnementFacturationResponse activerAbonnement(UUID idAbonnement);

    AbonnementFacturationResponse suspendreAbonnement(UUID idAbonnement);

    AbonnementFacturationResponse annulerAbonnement(UUID idAbonnement);

    void executerFacturationRecurrente();

    void executerFacturationPourAbonnement(UUID idAbonnement);

    LocalDate calculerProchaineFacturation(UUID idAbonnement);

    void marquerAbonnementsCommeExpires();

    void envoyerRappelsFacturation();

    List<AbonnementFacturationResponse> getAbonnementsAFacturer(LocalDate date);

    List<AbonnementFacturationResponse> getAbonnementsExpires();

    List<AbonnementFacturationResponse> getAbonnementsAvecErreurs();

    List<HistoriqueFacturationRecurrenteResponse> getHistoriqueAbonnement(UUID idAbonnement);

    List<HistoriqueFacturationRecurrenteResponse> getHistoriqueParPeriode(LocalDate startDate, LocalDate endDate);

    void nettoyerHistoriqueAncien(int joursConservation);

    boolean isAbonnementActif(UUID idAbonnement);

    boolean isAbonnementModifiable(UUID idAbonnement);

    int getNombreFacturesRestantes(UUID idAbonnement);

    AbonnementFacturationResponse dupliquerAbonnement(UUID idAbonnement, UUID nouveauClientId);
}