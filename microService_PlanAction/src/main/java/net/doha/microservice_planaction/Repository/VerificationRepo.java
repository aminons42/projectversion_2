package net.doha.microservice_planaction.Repository;

import net.doha.microservice_planaction.DTO.VerificationDTO;
import net.doha.microservice_planaction.Entities.ResultatVerification;
import net.doha.microservice_planaction.Entities.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VerificationRepo extends JpaRepository<Verification, Long> {
    List<Verification> findByActionId(Long actionId);
    List<Verification> findByResultat(ResultatVerification resultat);
}
