package net.doha.microservice_audit.DTO;

import lombok.Data;
import net.doha.microservice_audit.entities.TypeAudit;

@Data
public class CreateTemplateRequest {
    private String Nom;
    private String Description;
    private TypeAudit TypeAudit;
}
