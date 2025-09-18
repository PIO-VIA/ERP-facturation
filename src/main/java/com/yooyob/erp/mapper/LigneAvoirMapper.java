package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.LigneAvoirCreateRequest;
import com.yooyob.erp.dto.response.LigneAvoirResponse;
import com.yooyob.erp.model.entity.LigneAvoir;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LigneAvoirMapper {

    @Mapping(target = "idLigne", ignore = true)
    LigneAvoir toEntity(LigneAvoirCreateRequest request);

    LigneAvoirResponse toResponse(LigneAvoir ligneAvoir);

    List<LigneAvoir> toEntityList(List<LigneAvoirCreateRequest> requests);

    List<LigneAvoirResponse> toResponseList(List<LigneAvoir> lignesAvoir);
}