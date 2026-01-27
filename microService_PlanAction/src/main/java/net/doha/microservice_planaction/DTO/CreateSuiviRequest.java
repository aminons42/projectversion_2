package net.doha.microservice_planaction.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateSuiviRequest {
    @NotBlank
    private String commentaire;

    @NotNull
    @Min(0) @Max(100)
    private Integer avancement;

    private String difficultes;
    private String solutions;
    private Long ActionId;
    private Long utilisateurId;
}
