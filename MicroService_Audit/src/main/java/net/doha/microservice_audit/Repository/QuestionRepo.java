package net.doha.microservice_audit.Repository;

import net.doha.microservice_audit.entities.CheckList;
import net.doha.microservice_audit.entities.QuestionAudit;
import org.aspectj.weaver.patterns.TypePatternQuestions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepo extends JpaRepository<QuestionAudit,Long> {
    List<QuestionAudit> findByChecklistTemplateIdOrderByOrdre(Long templateId);
}

