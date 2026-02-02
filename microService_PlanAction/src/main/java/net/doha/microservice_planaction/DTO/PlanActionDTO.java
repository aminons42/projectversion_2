package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.SourcePlan;
import net.doha.microservice_planaction.Entities.StatutPlan;
import net.doha.microservice_planaction.Entities.TypeAction;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;
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
    private LocalDate dateCreation;
    private LocalDate dateEcheance;
    private LocalDate dateCloture;
    private Double budgetEstime;
    private Double coutReel;
    private Long valideurId;
    private String valideurNom;
    private LocalDate dateValidation;
    private Integer progression;
    private List<ActionDTO> actions;
}
