package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.MotifEscalade;
import net.doha.microservice_planaction.Entities.NiveauEscalade;
import net.doha.microservice_planaction.Entities.StatutEscalade;

import java.time.LocalDateTime;

@Data
public class EscaladeDTO {
    private Long id;
    private MotifEscalade motif;        // enum
    private NiveauEscalade niveau;      // enum
    private StatutEscalade statut;      // enum

    private LocalDateTime date;
    private String description;
    private String resolution;

    private Long traitePar;

    private Long planActionId;
    private Long actionId;
}
