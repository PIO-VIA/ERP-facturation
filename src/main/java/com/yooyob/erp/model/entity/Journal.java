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
@Table("journals")
public class Journal {

    @PrimaryKey
    @Column("id_journal")
    private UUID idJournal;

    @NotBlank(message = "Le nom du journal est obligatoire")
    @Column("nom_journal")
    private String nomJournal;

    @NotBlank(message = "Le type est obligatoire")
    @Column("type")
    private String type;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}