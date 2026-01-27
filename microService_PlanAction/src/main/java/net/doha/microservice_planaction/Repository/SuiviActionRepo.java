package net.doha.microservice_planaction.Repository;


import net.doha.microservice_planaction.Entities.SuiviAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SuiviActionRepo  extends JpaRepository<SuiviAction,Long> {
    List<SuiviAction> findByActionIdOrderByDateDesc(Long actionId);

}
