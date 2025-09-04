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
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mesures")
public class Mesure {

    @PrimaryKey
    @Column("id_mesure")
    private UUID idMesure;

    @NotBlank(message = "Le nom de la mesure est obligatoire")
    @Column("nom_mesure")
    private String nomMesure;

    @NotNull(message = "Le facteur de conversion est obligatoire")
    @Positive(message = "Le facteur de conversion doit être positif")
    @Column("facteur_conversion")
    private BigDecimal facteurConversion;

    @Column("unite_base")
    private String uniteBase;

    @Column("symbole")
    private String symbole;

    @Column("type_mesure")
    private String typeMesure;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}