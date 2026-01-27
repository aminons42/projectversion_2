package net.doha.microservice_audit.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReponseRequeste {

        @NotNull
        private Long questionId;

        @NotNull
        private String valeur;

        @NotNull
        private Boolean conforme;

        private String commentaire;

}
