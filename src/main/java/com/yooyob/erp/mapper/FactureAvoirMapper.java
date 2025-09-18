package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.FactureAvoirCreateRequest;
import com.yooyob.erp.dto.response.FactureAvoirResponse;
import com.yooyob.erp.model.entity.FactureAvoir;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LigneAvoirMapper.class})
public interface FactureAvoirMapper {

    @Mapping(target = "idAvoir", ignore = true)
    @Mapping(target = "numeroAvoir", ignore = true)
    @Mapping(target = "dateValidation", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "numeroFactureOrigine", ignore = true)
    @Mapping(target = "idClient", ignore = true)
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "montantHT", ignore = true)
    @Mapping(target = "montantTVA", ignore = true)
    @Mapping(target = "montantTTC", ignore = true)
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", ignore = true)
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "dateApplication", ignore = true)
    @Mapping(target = "montantApplique", ignore = true)
    @Mapping(target = "montantRembourse", ignore = true)
    @Mapping(target = "dateRemboursement", ignore = true)
    @Mapping(target = "approuvePar", ignore = true)
    @Mapping(target = "dateApprobation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    FactureAvoir toEntity(FactureAvoirCreateRequest request);

    FactureAvoirResponse toResponse(FactureAvoir factureAvoir);

    List<FactureAvoirResponse> toResponseList(List<FactureAvoir> facturesAvoir);

    void updateEntityFromRequest(FactureAvoirCreateRequest request, @MappingTarget FactureAvoir factureAvoir);
}