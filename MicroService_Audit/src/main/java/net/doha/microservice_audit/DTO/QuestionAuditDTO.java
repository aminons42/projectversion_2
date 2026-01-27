package net.doha.microservice_audit.DTO;

import lombok.Data;
import net.doha.microservice_audit.entities.NiveauCriticite;
import net.doha.microservice_audit.entities.TypeReponse;

@Data
public class QuestionAuditDTO {
    private Long id;
    private String libelle;
    private String description;
    private TypeReponse typeReponse;
    private String exigenceReglementaire;
    private Integer ponderation;
    private NiveauCriticite criticite;
    private Integer ordre;
    private Boolean obligatoire;
    private String categorie;
}
