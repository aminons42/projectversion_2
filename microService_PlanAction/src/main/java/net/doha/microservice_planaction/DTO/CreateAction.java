package net.doha.microservice_planaction.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;

import java.time.LocalDateTime;

@Data
public class CreateAction {
    @NotBlank
    private String description;
    @NotNull
    private TypeAction typeAction;

    @NotNull
    private Long responsableId;

    private LocalDateTime dateEcheance;
    private Integer dureeEstimee;
    private Priorite priorite = Priorite.MOYENNE;
    private String ressourcesNecessaires;
    private StatutAction statut = StatutAction.A_FAIRE;

}
