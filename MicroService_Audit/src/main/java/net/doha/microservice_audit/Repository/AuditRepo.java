package net.doha.microservice_audit.Repository;

import net.doha.microservice_audit.entities.Audit;
import net.doha.microservice_audit.entities.StatutAudit;
import net.doha.microservice_audit.entities.TypeAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditRepo extends JpaRepository<Audit, Long> {
    List<Audit> findByAuditeurId(Long auditeurId);
    List<Audit> findByTypeAudit(TypeAudit typeAudit);
    List<Audit> findByStatut(StatutAudit statutAudit);

    @Query("SELECT a FROM Audit a WHERE a.datePlanifie BETWEEN :debut AND :fin")
    List<Audit> findByDatePlanifieeBetween(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}
