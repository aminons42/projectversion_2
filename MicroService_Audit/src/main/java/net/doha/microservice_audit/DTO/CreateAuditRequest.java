package net.doha.microservice_audit.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import lombok.Data;
import net.doha.microservice_audit.entities.TypeAudit;

import java.time.LocalDateTime;

@Data

public class CreateAuditRequest {
    @NotBlank(message = "Le titre est obligatoire")
    private String titre;
    private String auditeurUsername="doha04"; // ← le champ doit être exactement celui-là

    private String description;

    @NotNull(message = "Le type et obligatoir")
    private TypeAudit typeAudit;

   

    private LocalDateTime datePlanifiee;
    private String siteId;
    private String departement;
    private String zone;

// --- GETTERS ---
    public String getTitre() { return titre; }
    public String getDescription() { return description; }
    public String getAuditeurUsername() { return auditeurUsername; }
    public TypeAudit getTypeAudit() { return typeAudit; }
    public LocalDateTime getDatePlanifiee() { return datePlanifiee; }
    public String getDepartement() { return departement; }
    public String getZone() { return zone; }

    // --- SETTERS ---
    public void setTitre(String titre) { this.titre = titre; }
    public void setDescription(String description) { this.description = description; }
    public void setAuditeurUsername(String auditeurUsername) { this.auditeurUsername = auditeurUsername; }
    public void setTypeAudit(TypeAudit typeAudit) { this.typeAudit = typeAudit; }
    public void setDatePlanifiee(LocalDateTime datePlanifiee) { this.datePlanifiee = datePlanifiee; }
    public void setDepartement(String departement) { this.departement = departement; }
    public void setZone(String zone) { this.zone = zone; }

}
