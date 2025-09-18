package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.AbonnementFacturationCreateRequest;
import com.yooyob.erp.dto.response.AbonnementFacturationResponse;
import com.yooyob.erp.model.entity.AbonnementFacturation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LigneFactureMapper.class})
public interface AbonnementFacturationMapper {

    @Mapping(target = "idAbonnement", ignore = true)
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "dateProchaineFacturation", ignore = true)
    @Mapping(target = "dateDerniereFacturation", ignore = true)
    @Mapping(target = "nombreFacturesGenerees", ignore = true)
    @Mapping(target = "montantTotalFacture", ignore = true)
    @Mapping(target = "derniereErreur", ignore = true)
    @Mapping(target = "dateDerniereErreur", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AbonnementFacturation toEntity(AbonnementFacturationCreateRequest request);

    AbonnementFacturationResponse toResponse(AbonnementFacturation abonnement);

    List<AbonnementFacturationResponse> toResponseList(List<AbonnementFacturation> abonnements);

    void updateEntityFromRequest(AbonnementFacturationCreateRequest request, @MappingTarget AbonnementFacturation abonnement);
}