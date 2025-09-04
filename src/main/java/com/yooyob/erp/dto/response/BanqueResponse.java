package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanqueResponse {

    private UUID idBanque;
    private String numeroCompte;
    private String banque;
}