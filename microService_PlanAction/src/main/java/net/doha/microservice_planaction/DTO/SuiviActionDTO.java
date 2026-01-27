package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;

@Data
public class SuiviActionDTO {
    private Long id;
    private LocalDateTime date;
    private String commentaire;
    private Integer avancement;
    private String difficultes;
    private String solutions;
    private Long utilisateurId;
    private String utilisateurNom;
    private Long actionId;
}
