package com.hse.sabil.service_utilisateurs.repo_jpa;

import com.hse.sabil.service_utilisateurs.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Role_repo extends JpaRepository<Role,Integer> {
    Optional<Role> findByNom(String nom);

}
