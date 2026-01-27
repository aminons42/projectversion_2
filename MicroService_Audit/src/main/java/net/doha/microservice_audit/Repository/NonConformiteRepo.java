package net.doha.microservice_audit.Repository;

import net.doha.microservice_audit.entities.Gravite;
import net.doha.microservice_audit.entities.NonComformite;
import net.doha.microservice_audit.entities.StatutNC;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NonConformiteRepo extends JpaRepository<NonComformite,Long> {
    List<NonComformite> findByAuditId(Long auditId);
    List<NonComformite> findByStatut(StatutNC statut);
    List<NonComformite> findByGravite(Gravite gravite);
    List<NonComformite> findByResponsableId(Long responsableId);
}
