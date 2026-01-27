package net.doha.microservice_audit.service;


import net.doha.microservice_audit.DTO.*;
import net.doha.microservice_audit.Repository.CheckListRepo;
import net.doha.microservice_audit.Repository.QuestionRepo;
import net.doha.microservice_audit.entities.CheckList;
import net.doha.microservice_audit.entities.QuestionAudit;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CheckListeService {

    private CheckListRepo templateRepository;

    private QuestionRepo questionRepository;
    private RestTemplate restTemplate;

    public CheckListeService(CheckListRepo templateRepository, QuestionRepo questionRepository, RestTemplate restTemplate) {
        this.templateRepository = templateRepository;
        this.questionRepository = questionRepository;
        this.restTemplate = restTemplate;
    }



    private static final String USER_SERVICE_URL = "http://localhost:8080/api/users";

    public CheckListDTO createTemplate(CreateTemplateRequest request, Authentication auth) {
        String username = auth.getName();
        UserDTO createur = getUserByUsername(username);

        CheckList template = new CheckList();
        template.setNom(request.getNom());
        template.setDescription(request.getDescription());
        template.setTypeAudit(request.getTypeAudit());
        template.setCreateurId(createur.getId());

        template = templateRepository.save(template);
        return convertToDTO(template, createur);
    }

    public List<CheckListDTO> getTemplatesActifs() {
        return templateRepository.findByActif(true).stream()
                .map(template -> {
                    UserDTO createur = getUserById(template.getCreateurId());
                    return convertToDTO(template, createur);
                })
                .collect(Collectors.toList());
    }

    public QuestionAuditDTO ajouterQuestion(Long templateId, CreateQuestionRequest request) {
        CheckList template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template non trouvé"));

        QuestionAudit question = new QuestionAudit();
        question.setLibelle(request.getLibelle());
        question.setDescription(request.getDescription());
        question.setTypeReponse(request.getTypeReponse());
        question.setCriticite(request.getNiveauCriticite());
        question.setCategorie(request.getCategorie());
        question.setChecklist(template);

        question = questionRepository.save(question);
        return convertQuestionToDTO(question);
    }

    private CheckListDTO convertToDTO(CheckList template, UserDTO createur) {
        CheckListDTO dto = new CheckListDTO();
        dto.setId(template.getId());
        dto.setNom(template.getNom());
        dto.setDescription(template.getDescription());
        dto.setTypeAudit(template.getTypeAudit());
        dto.setActif(template.getActif());
        dto.setVersion(template.getVersion());
        dto.setDateCreation(template.getDateCreation());
        dto.setCreateurId(template.getCreateurId());

        if (createur != null) {
            dto.setCreateurNom(createur.getNom() + " " + createur.getPrenom());
        }

        List<QuestionAuditDTO> questions = questionRepository
                .findByChecklistTemplateIdOrderByOrdre(template.getId())
                .stream()
                .map(this::convertQuestionToDTO)
                .collect(Collectors.toList());
        dto.setQuestions(questions);

        return dto;
    }

    private QuestionAuditDTO convertQuestionToDTO(QuestionAudit question) {
        QuestionAuditDTO dto = new QuestionAuditDTO();
        dto.setId(question.getId());
        dto.setLibelle(question.getLibelle());
        dto.setDescription(question.getDescription());
        dto.setTypeReponse(question.getTypeReponse());
        dto.setExigenceReglementaire(question.getExigenceReglementaire());
        dto.setPonderation(question.getPonderation());
        dto.setCriticite(question.getCriticite());
        dto.setOrdre(question.getOrdre());
        dto.setObligatoire(question.getObligatoire());
        dto.setCategorie(question.getCategorie());
        return dto;
    }

    private UserDTO getUserById(Long userId) {
        if (userId == null) return null;
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, UserDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    private UserDTO getUserByUsername(String username) {
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + "/by-username/" + username, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Utilisateur non trouvé: " + username);
        }
    }
    public List<CheckListDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(template -> {
                    UserDTO createur = getUserById(template.getCreateurId());
                    return convertToDTO(template, createur);
                })
                .collect(Collectors.toList());
    }
    public CheckListDTO getTemplateById(Long id) {
        CheckList template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template non trouvé"));

        UserDTO createur = getUserById(template.getCreateurId());
        return convertToDTO(template, createur);
    }



}
