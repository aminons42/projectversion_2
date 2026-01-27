package net.doha.microservice_audit.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReponseDTO {
    private Long id;
    private Long questionId;
    private String questionLibelle;
    private String valeur;
    private Boolean conforme;
    private String commentaire;
    private Boolean actionRequise;
    private LocalDateTime dateReponse;
    private Long utilisateurId;
    private String utilisateurNom;
}


