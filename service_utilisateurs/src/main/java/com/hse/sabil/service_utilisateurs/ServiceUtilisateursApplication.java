package com.hse.sabil.service_utilisateurs;

import com.hse.sabil.service_utilisateurs.model.Role;
import com.hse.sabil.service_utilisateurs.repo_jpa.Role_repo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ServiceUtilisateursApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceUtilisateursApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoles(Role_repo roleRepo) {
		return args -> {
			if (roleRepo.findByNom("ROLE_ADMIN").isEmpty()) {
				roleRepo.save(new Role("ROLE_ADMIN"));
			}
			if (roleRepo.findByNom("ROLE_EMPLOYE").isEmpty()) {
				roleRepo.save(new Role("ROLE_EMPLOYE"));
			}
			if (roleRepo.findByNom("ROLE_MANAGER").isEmpty()) {
				roleRepo.save(new Role("ROLE_MANAGER"));
			}
			System.out.println("✅ Rôles initialisés");
		};
	}

}
