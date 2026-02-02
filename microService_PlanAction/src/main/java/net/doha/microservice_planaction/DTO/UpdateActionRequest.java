package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.TypeAction;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;

@Data
public class UpdateActionRequest {
    private String description;
    private TypeAction typeAction;
    private Long responsableId;
    private LocalDate dateEcheance;
    private Integer dureeEstimee;
    private Priorite priorite;
    private String ressourcesNecessaires;
    private String indicateurEfficacite;

}
