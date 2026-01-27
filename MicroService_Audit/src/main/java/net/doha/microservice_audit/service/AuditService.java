package net.doha.microservice_audit.service;

import net.doha.microservice_audit.DTO.*;
import net.doha.microservice_audit.Repository.*;
import net.doha.microservice_audit.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditService {
    private  AuditRepo auditRepo;
    private  CheckListRepo checkListRepo;
    private  NonConformiteRepo nonConformiteRepo;
    private  QuestionRepo questionRepo;
    private RestTemplate restTemplate;

    public AuditService(AuditRepo auditRepo, CheckListRepo checkListRepo, NonConformiteRepo nonConformiteRepo, QuestionRepo questionRepo, RestTemplate restTemplate) {
        this.auditRepo = auditRepo;
        this.checkListRepo = checkListRepo;
        this.nonConformiteRepo = nonConformiteRepo;
        this.questionRepo = questionRepo;
        this.restTemplate = restTemplate;
    }
    private static final String USER_SERVICE_URL = "http://localhost:8080/api/users";

    public AuditDTO createAudit(CreateAuditRequest request, Authentication auth) {
        String username = auth.getName();
        UserDTO auditeur = getUserByUsername(username);

        CheckList template = checkListRepo.findById(request.getChecklistTemplateId())
                .orElseThrow(() -> new RuntimeException("Template non trouvé"));

        Audit audit = new Audit();
        audit.setTitre(request.getTitre());
        audit.setDescription(request.getDescription());
        audit.setTypeAudit(request.getTypeAudit());
        audit.setAuditeurId(auditeur.getId());
        audit.setChecklistTemplate(template);
        audit.setDatePlanifie(request.getDatePlanifiee());
        audit.setDepartement(request.getDepartement());
        audit.setZone(request.getZone());
        audit.setStatut(StatutAudit.PLANIFIE);

        audit = auditRepo.save(audit);
        return convertToDTO(audit, auditeur);

    }



    public List<AuditDTO> getAllAudits() {
        return auditRepo.findAll().stream()
                .map(audit -> {
                    UserDTO auditeur = getUserById(audit.getAuditeurId());
                    return convertToDTO(audit, auditeur);
                })
                .collect(Collectors.toList());
    }

    public AuditDTO getAuditById(Long id) {
        Audit audit = auditRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));
        UserDTO auditeur = getUserById(audit.getAuditeurId());
        return convertToDTO(audit, auditeur);
    }

    public AuditDTO demarrerAudit(Long id) {
        Audit audit = auditRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        audit.setStatut(StatutAudit.EN_COURS);
        audit.setDateDebut(LocalDateTime.now());

        // Initialiser les réponses vides
        List<QuestionAudit> questions = questionRepo
                .findByChecklistTemplateIdOrderByOrdre(audit.getChecklistTemplate().getId());

        for (QuestionAudit question : questions) {
            ReponseAUdit reponse = new ReponseAUdit();
            reponse.setAudit(audit);
            reponse.setQuestion(question);
            reponse.setValeur("");
            reponse.setConforme(true);
            reponse.setUtilisateurId(audit.getAuditeurId());
            audit.getReponses().add(reponse);
        }

        audit = auditRepo.save(audit);
        UserDTO auditeur = getUserById(audit.getAuditeurId());
        return convertToDTO(audit, auditeur);
    }

    public AuditDTO terminerAudit(Long id) {
        Audit audit = auditRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        audit.terminer();

        // Créer des NC pour les réponses non conformes
        audit.getReponses().stream()
                .filter(r -> !r.getConforme() && r.getActionRequise())
                .forEach(reponse -> {
                    NonComformite nc = new NonComformite();
                    nc.setAudit(audit);
                    nc.setDescription("NC détectée: " + reponse.getQuestion().getLibelle());
                    nc.setGravite(Gravite.MINEURE);
                    nc.setStatut(StatutNC.OUVERTE);
                    audit.getNonConformites().add(nc);
                });

        auditRepo.save(audit);
        UserDTO auditeur = getUserById(audit.getAuditeurId());
        return convertToDTO(audit, auditeur);
    }

    public List<AuditDTO> getMyAudits(Authentication auth) {
        String username = auth.getName();
        UserDTO user = getUserByUsername(username);

        return auditRepo.findByAuditeurId(user.getId()).stream()
                .map(audit -> convertToDTO(audit, user))
                .collect(Collectors.toList());
    }

    public List<AuditDTO> getAuditsByStatut(StatutAudit statut) {
        return auditRepo.findByStatut(statut).stream()
                .map(audit -> {
                    UserDTO auditeur = getUserById(audit.getAuditeurId());
                    return convertToDTO(audit, auditeur);
                })
                .collect(Collectors.toList());
    }

    public ReponseDTO ajouterReponse(Long auditId, CreateReponseRequeste request, Authentication auth) {
        Audit audit = auditRepo.findById(auditId)
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        QuestionAudit question = questionRepo.findById(request.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question non trouvée"));

        String username = auth.getName();
        UserDTO user = getUserByUsername(username);

        ReponseAUdit reponse = new ReponseAUdit();
        reponse.setAudit(audit);
        reponse.setQuestion(question);
        reponse.setValeur(request.getValeur());
        reponse.setConforme(request.getConforme());
        reponse.setCommentaire(request.getCommentaire());
        reponse.setActionRequise(!request.getConforme());
        reponse.setUtilisateurId(user.getId());

        audit.getReponses().add(reponse);
        auditRepo.save(audit);

        return convertReponseToDTO(reponse, user);
    }

    private AuditDTO convertToDTO(Audit audit, UserDTO auditeur) {
        AuditDTO dto = new AuditDTO();
        dto.setId(audit.getId());
        dto.setTitre(audit.getTitre());
        dto.setDescription(audit.getDescription());
        dto.setTypeAudit(audit.getTypeAudit().name());
        dto.setStatut(audit.getStatut().name());
        dto.setDateDebut(audit.getDateDebut());
        dto.setDateFin(audit.getDateFin());
        dto.setDatePlanifie(audit.getDatePlanifie());
        dto.setAuditeurId(audit.getAuditeurId());
        dto.setAuditeurNom(auditeur.getNom());
        dto.setAuditeurPrenom(auditeur.getPrenom());
        dto.setDepartement(audit.getDepartement());
        dto.setZone(audit.getZone());
        dto.setScoreGlobal(audit.getScoreGlobal());
        dto.setTauxConformite(audit.getTauxConformite());
        dto.setObservations(audit.getObservations());
        dto.setRecommandations(audit.getRecommandations());
        dto.setDateCreation(audit.getDateCreation());

        if (audit.getChecklistTemplate() != null) {
            dto.setTemplateId(audit.getChecklistTemplate().getId());
            dto.setTemplateNom(audit.getChecklistTemplate().getNom());
        }

        return dto;
    }

    private ReponseDTO convertReponseToDTO(ReponseAUdit reponse, UserDTO user) {
        ReponseDTO dto = new ReponseDTO();
        dto.setId(reponse.getId());
        dto.setQuestionId(reponse.getQuestion().getId());
        dto.setQuestionLibelle(reponse.getQuestion().getLibelle());
        dto.setValeur(reponse.getValeur());
        dto.setConforme(reponse.getConforme());
        dto.setCommentaire(reponse.getCommentaire());
        dto.setActionRequise(reponse.getActionRequise());
        dto.setDateReponse(reponse.getDateReponse());
        dto.setUtilisateurId(user.getId());
        dto.setUtilisateurNom(user.getNom() + " " + user.getPrenom());
        return dto;
    }

    private UserDTO getUserById(Long userId) {
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + "/" + userId, UserDTO.class);
        } catch (Exception e) {
            UserDTO defaultUser = new UserDTO();
            defaultUser.setId(userId);
            defaultUser.setNom("Utilisateur");
            defaultUser.setPrenom("Inconnu");
            return defaultUser;
        }
    }

    private UserDTO getUserByUsername(String username) {
        try {
            return restTemplate.getForObject(USER_SERVICE_URL + "/by-username/" + username, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Utilisateur non trouvé: " + username);
        }
    }








}
