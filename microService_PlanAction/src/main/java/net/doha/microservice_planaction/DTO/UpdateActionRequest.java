package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;

@Data
public class UpdateActionRequest {
    private String description;
    private TypeAction typeAction;
    private Long responsableId;
    private LocalDateTime dateEcheance;
    private Integer dureeEstimee;
    private Priorite priorite;
    private String ressourcesNecessaires;
    private String indicateurEfficacite;

}
