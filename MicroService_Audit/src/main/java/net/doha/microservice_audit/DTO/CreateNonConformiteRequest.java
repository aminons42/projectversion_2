package net.doha.microservice_audit.DTO;


import lombok.Data;
import net.doha.microservice_audit.entities.Gravite;
import net.doha.microservice_audit.entities.StatutNC;

@Data
public class CreateNonConformiteRequest {
    private Long auditId ;
    private String description;
    private Gravite gravite ;
    private String causeRacine;




}

