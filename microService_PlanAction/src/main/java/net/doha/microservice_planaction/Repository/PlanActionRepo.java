package net.doha.microservice_planaction.Repository;

import net.doha.microservice_planaction.Entities.PlanAction;
import net.doha.microservice_planaction.Entities.Priorite;
import net.doha.microservice_planaction.Entities.SourcePlan;
import net.doha.microservice_planaction.Entities.StatutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanActionRepo extends JpaRepository<PlanAction, Long> {
    List<PlanAction> findByResponsableId(Long id);
    List<PlanAction> findByStatut(StatutPlan statut);
    List<PlanAction> findBySource(SourcePlan source);


    @Query("SELECT p FROM PlanAction p WHERE p.dateEcheance BETWEEN :debut AND :fin")
    List<PlanAction> findByDateEcheanceBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);


}
