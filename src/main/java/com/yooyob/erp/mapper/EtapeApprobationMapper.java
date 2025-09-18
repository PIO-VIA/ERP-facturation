package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.EtapeApprobationCreateRequest;
import com.yooyob.erp.dto.response.EtapeApprobationResponse;
import com.yooyob.erp.model.entity.EtapeApprobation;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EtapeApprobationMapper {

    EtapeApprobation toEntity(EtapeApprobationCreateRequest request);

    EtapeApprobationResponse toResponse(EtapeApprobation etapeApprobation);

    List<EtapeApprobation> toEntityList(List<EtapeApprobationCreateRequest> requests);

    List<EtapeApprobationResponse> toResponseList(List<EtapeApprobation> etapesApprobation);
}