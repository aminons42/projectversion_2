package com.hse.sabil.service_utilisateurs.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String nom;
    private String prenom;
}