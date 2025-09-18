package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.WorkflowApprobationCreateRequest;
import com.yooyob.erp.dto.response.WorkflowApprobationResponse;
import com.yooyob.erp.model.entity.WorkflowApprobation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {EtapeApprobationMapper.class})
public interface WorkflowApprobationMapper {

    @Mapping(target = "idWorkflow", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    WorkflowApprobation toEntity(WorkflowApprobationCreateRequest request);

    WorkflowApprobationResponse toResponse(WorkflowApprobation workflow);

    List<WorkflowApprobationResponse> toResponseList(List<WorkflowApprobation> workflows);

    void updateEntityFromRequest(WorkflowApprobationCreateRequest request, @MappingTarget WorkflowApprobation workflow);
}