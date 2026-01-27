package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;

@Data
public class ActionDTO {
    private Long id;
    private String description;
    private TypeAction typeAction;
    private Long responsableId;
    private String responsableNom;
    private LocalDateTime dateDebut;
    private LocalDateTime dateEcheance;
    private Integer dureeEstimee;
    private Integer dureeReelle;
    private StatutAction statut;
    private Priorite priorite;
    private Integer progression;
    private String ressourcesNecessaires;
    private String indicateurEfficacite;
    private Long planActionId;

}
