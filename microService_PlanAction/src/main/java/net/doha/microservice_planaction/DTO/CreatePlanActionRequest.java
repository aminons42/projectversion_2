package net.doha.microservice_planaction.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.SourcePlan;
import net.doha.microservice_planaction.Entities.TypeAction;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;

@Data
public class CreatePlanActionRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    @NotNull(message = "La source est obligatoire")
    private SourcePlan source;
    private Long sourceId;

    private Priorite priorite;
    private LocalDate dateEcheance;
    
    private String responsableNom ;
    private Double budgetEstime;

   @NotNull(message = "Le type est obligatoire")
    private TypeAction type;
}
