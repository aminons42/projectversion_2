package com.hse.sabil.service_utilisateurs.controller;

import com.hse.sabil.service_utilisateurs.dto.JwtAuthResponse;
import com.hse.sabil.service_utilisateurs.dto.LoginRequest;
import com.hse.sabil.service_utilisateurs.dto.RegisterRequest;
import com.hse.sabil.service_utilisateurs.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController // 1. Indique que c'est un contrôleur d'API REST
@RequestMapping("/api/auth") // 2. L'URL de base pour ce contrôleur
@CrossOrigin(origins = "*") // 3. Autorise les requêtes de ton futur frontend Angular
public class AuthController {

    // 4. On injecte le "cerveau" (le service)
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 5. La ROUTE pour le LOGIN
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginRequest loginRequest) {
        // On appelle la méthode login du service
        JwtAuthResponse jwtAuthResponse = authService.login(loginRequest);
        return ResponseEntity.ok(jwtAuthResponse); // Renvoie un statut "200 OK" avec le token
    }

    // 6. La ROUTE pour l'INSCRIPTION
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        // On appelle la méthode register du service
        // On utilise un try-catch pour gérer les erreurs (ex: username déjà pris)
        try {
            authService.register(registerRequest);
            return ResponseEntity.ok("Utilisateur enregistré avec succès!");
        } catch (RuntimeException e) {
            // Si le service envoie une erreur, on la renvoie au frontend
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST) // Renvoie un statut "400 Bad Request"
                    .body(e.getMessage());
        }
    }
}