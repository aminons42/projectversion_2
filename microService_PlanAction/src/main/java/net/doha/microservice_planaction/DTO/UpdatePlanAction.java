package net.doha.microservice_planaction.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
@Data
public class UpdatePlanAction {
    private String titre;
    private String description;
    private Double budgetEstime;

}
