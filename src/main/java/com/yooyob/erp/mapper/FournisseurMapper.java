package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.FournisseurCreateRequest;
import com.yooyob.erp.dto.request.FournisseurUpdateRequest;
import com.yooyob.erp.dto.response.FournisseurResponse;
import com.yooyob.erp.model.entity.Fournisseur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface FournisseurMapper extends BaseMapper<Fournisseur, FournisseurCreateRequest, FournisseurUpdateRequest, FournisseurResponse> {

    @Mapping(target = "idFournisseur", expression = "java(generateId())")
    @Mapping(target = "soldeCourant", constant = "0.0")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Fournisseur toEntity(FournisseurCreateRequest createRequest);

    @Mapping(target = "idFournisseur", ignore = true)
    @Mapping(target = "soldeCourant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(FournisseurUpdateRequest updateRequest, @MappingTarget Fournisseur fournisseur);

    FournisseurResponse toResponse(Fournisseur fournisseur);

    List<FournisseurResponse> toResponseList(List<Fournisseur> fournisseurs);
}