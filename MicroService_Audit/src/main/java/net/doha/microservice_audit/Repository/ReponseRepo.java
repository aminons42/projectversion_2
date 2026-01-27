package net.doha.microservice_audit.Repository;

import net.doha.microservice_audit.entities.ReponseAUdit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReponseRepo extends JpaRepository<ReponseAUdit,Long> {


}
