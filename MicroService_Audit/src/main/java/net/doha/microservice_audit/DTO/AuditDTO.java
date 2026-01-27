package net.doha.microservice_audit.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class AuditDTO {

        private Long id;
        private String nom;
        private String description;
        private String titre;
        private String typeAudit;
        private LocalDateTime dateDebut;
        private LocalDateTime dateFin;
        private LocalDateTime datePlanifie;
        private String statut;
        private Long auditeurId;
        private  String auditeurNom;
        private  String auditeurPrenom;
        private  String auditeurEmail;
        private String departement;
        private String zone;
        private Double scoreGlobal;
        private Double tauxConformite;
        private String observations;
        private String recommandations;
        private LocalDateTime dateCreation;
        private LocalDateTime dateModification;

        // Relations représentées par ID
        private Long TemplateId;
        private String templateNom;
}
