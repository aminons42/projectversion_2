package net.doha.microservice_planaction.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import net.doha.microservice_planaction.Entities.ResultatVerification;

import java.time.LocalDateTime;

@Data
public class CreateVerificationRequest {
    @NotNull
    private ResultatVerification resultat;
    private  Long verificateurId;

    private LocalDateTime dateProchaineSurveillance;

    private String commentaire;

    @NotNull
    private Boolean efficace;
}
