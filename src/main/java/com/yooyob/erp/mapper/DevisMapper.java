package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.DevisCreateRequest;
import com.yooyob.erp.dto.response.DevisResponse;
import com.yooyob.erp.model.entity.Devis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", 
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {LigneDevisMapper.class})
public interface DevisMapper {

    @Mapping(target = "idDevis", ignore = true)
    @Mapping(target = "numeroDevis", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "montantHT", ignore = true)
    @Mapping(target = "montantTVA", ignore = true)
    @Mapping(target = "montantTTC", ignore = true)
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", ignore = true)
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "dateAcceptation", ignore = true)
    @Mapping(target = "dateRefus", ignore = true)
    @Mapping(target = "motifRefus", ignore = true)
    @Mapping(target = "idFactureConvertie", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Devis toEntity(DevisCreateRequest request);

    DevisResponse toResponse(Devis devis);

    List<DevisResponse> toResponseList(List<Devis> devisList);

    void updateEntityFromRequest(DevisCreateRequest request, @MappingTarget Devis devis);
}