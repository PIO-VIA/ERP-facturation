package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.LigneDevisCreateRequest;
import com.yooyob.erp.dto.response.LigneDevisResponse;
import com.yooyob.erp.model.entity.LigneDevis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LigneDevisMapper {

    @Mapping(target = "idLigne", ignore = true)
    LigneDevis toEntity(LigneDevisCreateRequest request);

    LigneDevisResponse toResponse(LigneDevis ligneDevis);

    List<LigneDevis> toEntityList(List<LigneDevisCreateRequest> requests);

    List<LigneDevisResponse> toResponseList(List<LigneDevis> lignesDevis);
}