package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("banques")
public class Banque {

    @PrimaryKey
    @Column("id_banque")
    private UUID idBanque;

    @NotBlank(message = "Le numéro de compte est obligatoire")
    @Column("numero_compte")
    private String numeroCompte;

    @NotBlank(message = "Le nom de la banque est obligatoire")
    @Column("banque")
    private String banque;
}
