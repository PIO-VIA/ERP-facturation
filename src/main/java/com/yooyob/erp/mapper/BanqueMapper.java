package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.BanqueCreateRequest;
import com.yooyob.erp.dto.request.BanqueUpdateRequest;
import com.yooyob.erp.dto.response.BanqueResponse;
import com.yooyob.erp.model.entity.Banque;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface BanqueMapper extends BaseMapper<Banque, BanqueCreateRequest, BanqueUpdateRequest, BanqueResponse> {

    @Mapping(target = "idBanque", expression = "java(generateId())")
    Banque toEntity(BanqueCreateRequest createRequest);

    @Mapping(target = "idBanque", ignore = true)
    void updateEntityFromRequest(BanqueUpdateRequest updateRequest, @MappingTarget Banque banque);

    BanqueResponse toResponse(Banque banque);

    List<BanqueResponse> toResponseList(List<Banque> banques);
}