package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.SourcePlan;
import net.doha.microservice_planaction.Entities.StatutPlan;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PlanActionDTO {
    private Long id;
    private String titre;
    private String description;
    private SourcePlan source;
    private Long sourceId;
    private Priorite priorite;
    private StatutPlan statut;
    private Long responsablePlanId;
    private String responsableNom;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEcheance;
    private LocalDateTime dateCloture;
    private Double budgetEstime;
    private Double coutReel;
    private Long valideurId;
    private String valideurNom;
    private LocalDateTime dateValidation;
    private Integer progression;
    private List<ActionDTO> actions;
}
