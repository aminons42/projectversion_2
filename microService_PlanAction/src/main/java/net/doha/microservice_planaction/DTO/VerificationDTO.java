package net.doha.microservice_planaction.DTO;

import lombok.Data;
import net.doha.microservice_planaction.Entities.ResultatVerification;

import java.time.LocalDateTime;

@Data
public class VerificationDTO {
    private Long id;
    private LocalDateTime dateVerification;
    private Long verificateurId;
    private String verificateurNom;
    private ResultatVerification resultat;
    private String commentaire;
    private Boolean efficace;
    private LocalDateTime dateProchaineSurveillance;
    private Long actionId;
}
