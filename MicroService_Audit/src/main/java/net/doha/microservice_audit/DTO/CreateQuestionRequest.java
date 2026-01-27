package net.doha.microservice_audit.DTO;

import lombok.Data;
import net.doha.microservice_audit.entities.NiveauCriticite;
import net.doha.microservice_audit.entities.TypeReponse;

@Data
public class CreateQuestionRequest {
    private String description;
    private TypeReponse typeReponse=TypeReponse.OUI_NON;
    private NiveauCriticite niveauCriticite=NiveauCriticite.MOYENNE ;
    private String categorie;
    private String libelle;


}
