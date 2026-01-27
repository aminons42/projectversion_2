package com.hse.sabil.service_utilisateurs.dto;

import lombok.Data;

@Data // Parfait pour un DTO : génère getters, setters, constructeurs
public class LoginRequest {
    private String username;
    private String password;
}
