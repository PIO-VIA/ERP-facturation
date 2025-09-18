package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueFacturationRecurrenteResponse {

    private UUID idHistorique;
    private UUID idAbonnement;
    private String nomAbonnement;
    private UUID idFactureGeneree;
    private String numeroFactureGeneree;
    private LocalDateTime dateExecution;
    private LocalDateTime dateFacture;
    private BigDecimal montantFacture;
    private Boolean succes;
    private String messageErreur;
    private String detailsExecution;
    private Boolean emailEnvoye;
    private Boolean pdfGenere;
    private Long tempsExecutionMs;
    private LocalDateTime createdAt;
}