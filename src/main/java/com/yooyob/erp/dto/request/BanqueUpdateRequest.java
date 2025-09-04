package com.yooyob.erp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanqueUpdateRequest {

    private String numeroCompte;
    private String banque;
}