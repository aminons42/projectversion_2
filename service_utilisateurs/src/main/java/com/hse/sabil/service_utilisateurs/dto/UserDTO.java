package com.hse.sabil.service_utilisateurs.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String nom;
    private String prenom;
}