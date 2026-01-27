package com.hse.sabil.service_utilisateurs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor // Pratique pour créer la réponse en une ligne
public class JwtAuthResponse {
    private String token;


}