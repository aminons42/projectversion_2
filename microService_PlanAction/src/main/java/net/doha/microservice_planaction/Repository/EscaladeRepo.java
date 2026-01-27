package net.doha.microservice_planaction.Repository;

import net.doha.microservice_planaction.Entities.Escalade;
import net.doha.microservice_planaction.Entities.StatutEscalade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscaladeRepo extends JpaRepository<Escalade, Long> {
    List<Escalade> findByPlanActionId(Long planActionId);
    List<Escalade> findByActionId(Long actionId);
    List<Escalade> findByStatut(StatutEscalade statut);
}
