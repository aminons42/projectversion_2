package net.doha.microservice_planaction.DTO;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.Entities.MotifEscalade;
import net.doha.microservice_planaction.Entities.NiveauEscalade;

@Data
public class CreateEscaladeRequeste {
    private MotifEscalade motif;
    private NiveauEscalade niveau;
    private String description;
    private Long actionId;
    private Long planActionId;

}
