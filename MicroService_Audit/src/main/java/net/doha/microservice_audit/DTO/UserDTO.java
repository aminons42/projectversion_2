package net.doha.microservice_audit.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nom;
    private String prenom;
}
