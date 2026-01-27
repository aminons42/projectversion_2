package com.hse.sabil.service_utilisateurs.service;

import com.hse.sabil.service_utilisateurs.repo_jpa.Utilisateur_repo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private final Utilisateur_repo utilisateur_repo;

    public UserDetailsServiceImp(Utilisateur_repo utilisateur_repo) {
        this.utilisateur_repo = utilisateur_repo;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails user = utilisateur_repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le nom : " + username));
        return user;

    }
}
