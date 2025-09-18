package com.yooyob.erp.dto.request;

import com.yooyob.erp.model.enums.TypeRelance;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanificationRelanceCreateRequest {
    @NotNull
    private UUID idConfiguration;
    @NotNull
    private TypeRelance typeRelance;
    @NotNull
    private UUID idFacture;
    private LocalDateTime dateEnvoiPrevue;
    private String contenuPersonnalise;
    private String objetPersonnalise;
    private List<String> destinatairesSupplementaires;
    private List<String> canalEnvoi;
    private Integer priorite;
    private List<String> tags;
}