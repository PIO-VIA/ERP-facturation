package com.yooyob.erp.dto.response;

import com.yooyob.erp.model.enums.StatutApprobation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistoriqueApprobationResponse {

    private Integer ordreAction;
    private Integer etape;
    private String nomEtape;
    private UUID approbateur;
    private String nomApprobateur;
    private StatutApprobation action;
    private String commentaires;
    private LocalDateTime dateAction;
    private Long delaiReponseHeures;
    private Boolean escalade;
    private UUID escaladeDepuis;
    private String ipAdresse;
    private String metadata;
}