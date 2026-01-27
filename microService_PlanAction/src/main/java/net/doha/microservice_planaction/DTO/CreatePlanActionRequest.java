package net.doha.microservice_planaction.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.SourcePlan;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;

@Data
public class CreatePlanActionRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "La source est obligatoire")
    private SourcePlan source;
    private Long sourceId;

    private Priorite priorite;
    private LocalDateTime dateEcheance;

    private Double budgetEstime;

}
