package com.hse.sabil.service_utilisateurs.dto;

import com.hse.sabil.service_utilisateurs.model.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
    private UserInfo user;
    
    public JwtAuthResponse(String token) {
        this.token = token;
        this.user = null;
    }
    
    // Classe interne pour les infos utilisateur
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String nom;
        private String prenom;
        
        public static UserInfo fromUtilisateur(Utilisateur user) {
            return new UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNom(),
                user.getPrenom()
            );
        }
    }
}