package net.doha.microservice_planaction.DTO;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nom;
    private String prenom;
    private String email;
}
