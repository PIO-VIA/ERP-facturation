package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.TaxeCreateRequest;
import com.yooyob.erp.dto.request.TaxeUpdateRequest;
import com.yooyob.erp.dto.response.TaxeResponse;
import com.yooyob.erp.model.entity.Taxes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaxeMapper extends BaseMapper<Taxes, TaxeCreateRequest, TaxeUpdateRequest, TaxeResponse> {

    @Mapping(target = "idTaxe", expression = "java(generateId())")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Taxes toEntity(TaxeCreateRequest createRequest);

    @Mapping(target = "idTaxe", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(TaxeUpdateRequest updateRequest, @MappingTarget Taxes taxe);

    TaxeResponse toResponse(Taxes taxe);

    List<TaxeResponse> toResponseList(List<Taxes> taxes);
}