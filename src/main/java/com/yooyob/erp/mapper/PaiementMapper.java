package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.PaiementCreateRequest;
import com.yooyob.erp.dto.request.PaiementUpdateRequest;
import com.yooyob.erp.dto.response.PaiementResponse;
import com.yooyob.erp.model.entity.Paiement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PaiementMapper extends BaseMapper<Paiement, PaiementCreateRequest, PaiementUpdateRequest, PaiementResponse> {

    @Mapping(target = "idPaiement", expression = "java(generateId())")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Paiement toEntity(PaiementCreateRequest createRequest);

    @Mapping(target = "idPaiement", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(PaiementUpdateRequest updateRequest, @MappingTarget Paiement paiement);

    PaiementResponse toResponse(Paiement paiement);

    List<PaiementResponse> toResponseList(List<Paiement> paiements);
}