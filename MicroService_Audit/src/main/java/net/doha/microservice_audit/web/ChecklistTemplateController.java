package net.doha.microservice_audit.web;

import jakarta.validation.Valid;
import net.doha.microservice_audit.DTO.CheckListDTO;
import net.doha.microservice_audit.DTO.CreateQuestionRequest;
import net.doha.microservice_audit.DTO.CreateTemplateRequest;
import net.doha.microservice_audit.DTO.QuestionAuditDTO;
import net.doha.microservice_audit.service.CheckListeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class ChecklistTemplateController {

    public ChecklistTemplateController(CheckListeService templateService) {
        this.templateService = templateService;
    }

    private CheckListeService templateService;

    @PostMapping
    public ResponseEntity<CheckListDTO> createTemplate(
            @Valid @RequestBody CreateTemplateRequest request , Authentication authentication) {
        CheckListDTO template = templateService.createTemplate(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(template);
    }

    @GetMapping
    public ResponseEntity<List<CheckListDTO>> getAllTemplates(
            @RequestParam(required = false, defaultValue = "false") Boolean actifsOnly) {
        if (actifsOnly) {
            return ResponseEntity.ok(templateService.getTemplatesActifs());
        }
        return ResponseEntity.ok(templateService.getAllTemplates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheckListDTO> getTemplateById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.getTemplateById(id));
    }

    @PostMapping("/{templateId}/questions")
    public ResponseEntity<QuestionAuditDTO> ajouterQuestion(
            @PathVariable Long templateId,
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionAuditDTO question = templateService.ajouterQuestion(templateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(question);
    }
}
