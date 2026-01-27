package com.hse.sabil.service_utilisateurs.repo_jpa;

import com.hse.sabil.service_utilisateurs.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Utilisateur_repo extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByUsername(String username);

}
