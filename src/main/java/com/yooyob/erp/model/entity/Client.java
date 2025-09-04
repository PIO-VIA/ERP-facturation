package com.yooyob.erp.model.entity;

import com.yooyob.erp.model.enums.TypeClient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("clients")
public class Client {

    @PrimaryKey
    @Column("id_client")
    private UUID idClient;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Column("username")
    private String username;

    @NotBlank(message = "La catégorie est obligatoire")
    @Column("categorie")
    private String categorie;

    @Column("site_web")
    private String siteWeb;

    @Column("n_tva")
    @Builder.Default
    private Boolean nTva = false;

    @NotBlank(message = "L'adresse est obligatoire")
    @Column("adresse")
    private String adresse;

    @Column("telephone")
    private String telephone;

    @Email(message = "Format d'email invalide")
    @Column("email")
    private String email;

    @NotNull(message = "Le type de client est obligatoire")
    @Column("type_client")
    private TypeClient typeClient;

    @Column("raison_sociale")
    private String raisonSociale;

    @Column("numero_tva")
    private String numeroTva;

    @Column("code_client")
    private String codeClient;

    @Column("limite_credit")
    private Double limiteCredit;

    @Column("solde_courant")
    @Builder.Default
    private Double soldeCourant = 0.0;

    @Column("actif")
    @Builder.Default
    private Boolean actif = true;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}