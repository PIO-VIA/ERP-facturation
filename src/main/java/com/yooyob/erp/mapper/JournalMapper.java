package com.yooyob.erp.mapper;

import com.yooyob.erp.dto.request.JournalCreateRequest;
import com.yooyob.erp.dto.request.JournalUpdateRequest;
import com.yooyob.erp.dto.response.JournalResponse;
import com.yooyob.erp.model.entity.Journal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface JournalMapper extends BaseMapper<Journal, JournalCreateRequest, JournalUpdateRequest, JournalResponse> {

    @Mapping(target = "idJournal", expression = "java(generateId())")
    @Mapping(target = "createdAt", expression = "java(getCurrentTime())")
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    Journal toEntity(JournalCreateRequest createRequest);

    @Mapping(target = "idJournal", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(getCurrentTime())")
    void updateEntityFromRequest(JournalUpdateRequest updateRequest, @MappingTarget Journal journal);

    JournalResponse toResponse(Journal journal);

    List<JournalResponse> toResponseList(List<Journal> journals);
}