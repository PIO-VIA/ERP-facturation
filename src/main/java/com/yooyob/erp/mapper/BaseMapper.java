package com.yooyob.erp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BaseMapper<E, CreateRequest, UpdateRequest, Response> {

    E toEntity(CreateRequest createRequest);


    Response toResponse(E entity);

    void updateEntityFromRequest(UpdateRequest updateRequest, @MappingTarget E entity);

    // Méthodes utilitaires par défaut
    default UUID generateId() {
        return UUID.randomUUID();
    }

    default LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }
}