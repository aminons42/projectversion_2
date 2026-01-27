package com.hse.sabil.service_utilisateurs.service;

import com.hse.sabil.service_utilisateurs.dto.JwtAuthResponse;
import com.hse.sabil.service_utilisateurs.dto.LoginRequest;
import com.hse.sabil.service_utilisateurs.dto.RegisterRequest;
import com.hse.sabil.service_utilisateurs.model.Role;
import com.hse.sabil.service_utilisateurs.model.Utilisateur;
import com.hse.sabil.service_utilisateurs.repo_jpa.Role_repo;
import com.hse.sabil.service_utilisateurs.repo_jpa.Utilisateur_repo;
import com.hse.sabil.service_utilisateurs.security.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Transactional
@Service
public class AuthService {
    private JwtUtil jwtutil;
    private Utilisateur_repo utilisateur_repo;
    private Role_repo role_repo;
    private AuthenticationManager auth_manager;
    private PasswordEncoder passwordEncoder;


    public AuthService(JwtUtil jwtutil, Utilisateur_repo utilisateur_repo, Role_repo role_repo, AuthenticationManager auth_manager, PasswordEncoder passwordEncoder) {
        this.jwtutil = jwtutil;
        this.utilisateur_repo = utilisateur_repo;
        this.role_repo = role_repo;
        this.auth_manager = auth_manager;
        this.passwordEncoder = passwordEncoder;

    }

    public void register(RegisterRequest request){
        if (utilisateur_repo.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("User is already registered ");
        }

            Utilisateur user = new Utilisateur();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setNom(request.getNom());
            user.setPrenom(request.getPrenom());
            Role userRole = role_repo.findByNom("ROLE_EMPLOYE").orElseThrow(() -> new RuntimeException("Erreur: Le rôle par défaut n'a pas été trouvé."));
            user.setRoles(Set.of(userRole)); // Utilise Set.of() pour assigner


            utilisateur_repo.save(user);
    }
    public JwtAuthResponse login(LoginRequest request){
        auth_manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = utilisateur_repo.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));


        String token = jwtutil.generateToken(userDetails);


        return new JwtAuthResponse(token);

    }
}
