package com.yooyob.erp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtapeApprobationCreateRequest {

    private Integer ordreEtape;

    @NotBlank(message = "Le nom de l'étape est obligatoire")
    private String nomEtape;

    private String description;

    private List<UUID> approubateursRequis;

    private List<String> rolesApprobateurs;

    @Builder.Default
    private Integer nombreApprobationsRequises = 1;

    @Builder.Default
    private Boolean obligatoire = true;

    @Builder.Default
    private Boolean parallele = false;

    private String conditionsPassage;

    private Integer delaiMaxHeures;

    private List<UUID> escaladeVers;

    @Builder.Default
    private Boolean actif = true;
}