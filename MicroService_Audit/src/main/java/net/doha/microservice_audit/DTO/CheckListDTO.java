package net.doha.microservice_audit.DTO;

import lombok.Data;
import net.doha.microservice_audit.entities.TypeAudit;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CheckListDTO {
    private Long id;
    private String nom;
    private String description;
    private TypeAudit typeAudit;
    private Boolean actif;
    private Integer version;
    private LocalDateTime dateCreation;
    private Long createurId;
    private String createurNom;
    private List<QuestionAuditDTO> questions;
}
