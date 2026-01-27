package net.doha.microservice_audit.Repository;

import net.doha.microservice_audit.entities.CheckList;
import net.doha.microservice_audit.entities.TypeAudit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckListRepo extends JpaRepository<CheckList,Long> {
    List<CheckList> findByActif(Boolean actif);
    List<CheckList> findByTypeAudit(TypeAudit typeAudit);
    List<CheckList> findByCreateurId(Long createurId);
}
