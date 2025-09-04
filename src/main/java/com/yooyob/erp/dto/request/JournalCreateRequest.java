package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalCreateRequest {

    @NotBlank(message = "Le nom du journal est obligatoire")
    private String nomJournal;

    @NotBlank(message = "Le type est obligatoire")
    private String type;
}