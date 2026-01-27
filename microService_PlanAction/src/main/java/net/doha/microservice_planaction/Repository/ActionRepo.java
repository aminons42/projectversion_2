package net.doha.microservice_planaction.Repository;

import net.doha.microservice_planaction.Entities.Action;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActionRepo extends JpaRepository<Action, Long> {
    List<Action> findByPlanActionId(Long planActionId);
    List<Action> findByStatut(StatutAction statut);
    List<Action> findByResponsableId(Long responsableId);
    List<Action> findByTypeAction(TypeAction typeAction);


}
