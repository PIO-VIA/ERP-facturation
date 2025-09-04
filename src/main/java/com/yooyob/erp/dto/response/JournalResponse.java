package com.yooyob.erp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalResponse {

    private UUID idJournal;
    private String nomJournal;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}