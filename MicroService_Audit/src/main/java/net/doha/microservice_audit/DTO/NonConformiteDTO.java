package net.doha.microservice_audit.DTO;

import lombok.Data;
import net.doha.microservice_audit.entities.Gravite;
import net.doha.microservice_audit.entities.StatutNC;

import java.time.LocalDateTime;

@Data
public class NonConformiteDTO {
    private Long id;
    private String description;
    private Gravite gravite;
    private String causeRacine;
    private String actionImmediateRealisee;
    private Long planActionId;
    private StatutNC statut;
    private LocalDateTime dateDetection;
    private LocalDateTime dateCloture;
    private Long auditId;
    private String auditTitre;
    private Long responsableId;
    private String responsableNom;
}
