package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.FactureCreateRequest;
import com.yooyob.erp.dto.request.FactureUpdateRequest;
import com.yooyob.erp.dto.response.FactureResponse;
import com.yooyob.erp.dto.response.FactureDetailsResponse;
import com.yooyob.erp.model.entity.Facture;
import com.yooyob.erp.model.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.Context;

import java.math.BigDecimal;
import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {LigneFactureMapper.class, ClientMapper.class, PaiementMapper.class}
)
public interface FactureMapper extends BaseMapper<Facture, FactureCreateRequest, FactureUpdateRequest, FactureResponse> {

    @Mapping(target = "idFacture", expression = "java(generateId())")
    @Mapping(target = "numeroFacture", expression = "java(generateNumeroFacture())")
    @Mapping(target = "montantTotal", constant = "0")
    @Mapping(target = "montantRestant", constant = "0")
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "montantHT", constant = "0")
    @Mapping(target = "montantTVA", constant = "0")
    @Mapping(target = "montantTTC", constant = "0")
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", constant = "false")
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Facture toEntity(FactureCreateRequest createRequest);

    @Mapping(target = "idFacture", ignore = true)
    @Mapping(target = "numeroFacture", ignore = true)
    @Mapping(target = "montantTotal", ignore = true)
    @Mapping(target = "montantRestant", ignore = true)
    @Mapping(target = "nomClient", ignore = true)
    @Mapping(target = "adresseClient", ignore = true)
    @Mapping(target = "emailClient", ignore = true)
    @Mapping(target = "telephoneClient", ignore = true)
    @Mapping(target = "montantHT", ignore = true)
    @Mapping(target = "montantTVA", ignore = true)
    @Mapping(target = "montantTTC", ignore = true)
    @Mapping(target = "pdfPath", ignore = true)
    @Mapping(target = "envoyeParEmail", ignore = true)
    @Mapping(target = "dateEnvoiEmail", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(FactureUpdateRequest updateRequest, @MappingTarget Facture facture);

    FactureResponse toResponse(Facture facture);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "paiements", ignore = true)
    @Mapping(target = "totalPaiements", ignore = true)
    FactureDetailsResponse toDetailsResponse(Facture facture);

    List<FactureResponse> toResponseList(List<Facture> factures);

    // Méthode utilitaire pour générer le numéro de facture
    default String generateNumeroFacture() {
        return "FAC-" + System.currentTimeMillis();
    }

    // Mapping avec client pour FactureDetailsResponse
    @Mapping(target = "client", source = "client")
    @Mapping(target = "paiements", ignore = true)
    @Mapping(target = "totalPaiements", ignore = true)
    FactureDetailsResponse toDetailsResponseWithClient(Facture facture, @Context Client client);
}