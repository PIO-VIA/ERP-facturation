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
public class BanqueCreateRequest {

    @NotBlank(message = "Le numéro de compte est obligatoire")
    private String numeroCompte;

    @NotBlank(message = "Le nom de la banque est obligatoire")
    private String banque;
}