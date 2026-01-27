package net.doha.microservice_audit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import lombok.Data;
import net.doha.microservice_audit.entities.TypeAudit;

import java.time.LocalDateTime;

@Data
public class CreateAuditRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "Le type et obligatoir")
    private TypeAudit typeAudit;

    @NotNull(message = "Le template est obligatoire")
    private Long checklistTemplateId;

    private LocalDateTime datePlanifiee;
    private String siteId;
    private String departement;
    private String zone;


}
