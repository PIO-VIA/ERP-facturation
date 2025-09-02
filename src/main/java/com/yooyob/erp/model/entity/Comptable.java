package com.yooyob.erp.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("comptables")
public class Comptable {

    @PrimaryKey
    @Column("id_comptable")
    private UUID idComptable;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column("password")
    private String password;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column("username")
    private String username;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
