package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;

@Data
public class ActionDTO {
    private Long id;
    private String description;
    private TypeAction typeAction;
    private Long responsableId;
    private String responsableNom;
    private LocalDate dateDebut;
    private LocalDate dateEcheance;
    private Integer dureeEstimee;
    private Integer dureeReelle;
    private StatutAction statut;
    private Priorite priorite;
    private Integer progression;
    private String ressourcesNecessaires;
    private String indicateurEfficacite;
    private Long planActionId;
    private String titre;

}
