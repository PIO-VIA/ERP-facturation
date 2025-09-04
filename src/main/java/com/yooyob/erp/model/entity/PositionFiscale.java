package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("positions_fiscales")
public class PositionFiscale {

    @PrimaryKey
    @Column("id_position_fiscale")
    private UUID idPositionFiscale;

    @NotNull(message = "Le statut par défaut est obligatoire")
    @Column("defaut_auto")
    @Builder.Default
    private Boolean defautAuto = false;

    @NotBlank(message = "Le pays est obligatoire")
    @Column("pays")
    private String pays;

    @Column("n_identification_etranger")
    private String nIdentificationEtranger;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("description")
    private String description;

    @Column("code_position")
    private String codePosition;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}