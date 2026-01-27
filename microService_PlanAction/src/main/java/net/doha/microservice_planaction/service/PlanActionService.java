package net.doha.microservice_planaction.service;

import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.*;
import net.doha.microservice_planaction.Entities.Action;
import net.doha.microservice_planaction.Entities.PlanAction;
import net.doha.microservice_planaction.Entities.StatutPlan;
import net.doha.microservice_planaction.Repository.*;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PlanActionService {
    private final PlanActionRepo planActionRepo;
    private final RestTemplate restTemplate;
    private final ActionService actionService;


    @Value("${service.users.url:http://service_utilisateurs}")
    private String userServiceUrl;
    @Value("")
    private String auditServiceUrl;
    @Value("")
    private String IncidentServiceUrl;


    private UserDTO getUserByUsername(String username) {
        try {
            return restTemplate.getForObject(userServiceUrl + "/api/users/by-username/" + username, UserDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Utilisateur non trouvé: " + username);
        }
    }


    public PlanActionDTO createPlanAction(CreatePlanActionRequest request, Authentication auth) throws BadRequestException {
        String username = auth.getName();
        UserDTO responsable = getUserByUsername(username);
        PlanAction plan = new PlanAction();
        plan.setTitre(request.getTitre());
        plan.setDescription(request.getDescription());
        plan.setSource(request.getSource());
        plan.setSourceId(request.getSourceId());
        plan.setPriorite(request.getPriorite());
        plan.setResponsableId(responsable.getId());
        plan.setDateEcheance(request.getDateEcheance());
        plan.setBudgetEstime(request.getBudgetEstime());
        plan.setStatut(StatutPlan.BROUILLON);
        planActionRepo.save(plan);
        return convertToDTO(plan, responsable);
    }


    public List<PlanActionDTO> getAllPlans() {
        return planActionRepo.findAll().stream()
                .map(plan -> {
                    UserDTO responsable = getUserById(plan.getResponsableId());
                    return convertToDTO(plan, responsable);
                })
                .collect(Collectors.toList());
    }

    public PlanActionDTO getPlanById(Long id) {
        PlanAction plan = planActionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan d'action non trouvé"));
        UserDTO responsable = getUserById(plan.getResponsableId());
        return convertToDTO(plan, responsable);
    }

    public List<PlanActionDTO> getMyPlans(Authentication auth) {
        String username = auth.getName();
        UserDTO user = getUserByUsername(username);

        return planActionRepo.findByResponsableId(user.getId()).stream()
                .map(plan -> convertToDTO(plan, user))
                .collect(Collectors.toList());
    }

    public List<PlanActionDTO> getPlansByStatut(StatutPlan statut) {
        return planActionRepo.findByStatut(statut).stream()
                .map(plan -> {
                    UserDTO responsable = getUserById(plan.getResponsableId());
                    return convertToDTO(plan, responsable);
                })
                .collect(Collectors.toList());
    }

    public PlanActionDTO validerPlan(Long id, Authentication auth) {
        PlanAction plan = planActionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan d'action non trouvé"));

        String username = auth.getName();
        UserDTO valideur = getUserByUsername(username);

        plan.valider(valideur.getId());
        plan = planActionRepo.save(plan);

        UserDTO responsable = getUserById(plan.getResponsableId());
        return convertToDTO(plan, responsable);
    }

    public PlanActionDTO cloturerPlan(Long id) {
        PlanAction plan = planActionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan d'action non trouvé"));

        plan.cloturer();
        plan = planActionRepo.save(plan);

        UserDTO responsable = getUserById(plan.getResponsableId());
        return convertToDTO(plan, responsable);
    }

    private PlanActionDTO convertToDTO(PlanAction plan, UserDTO responsable) {
        PlanActionDTO dto = new PlanActionDTO();
        dto.setId(plan.getId());
        dto.setTitre(plan.getTitre());
        dto.setDescription(plan.getDescription());
        dto.setSource(plan.getSource());
        dto.setSourceId(plan.getSourceId());
        dto.setPriorite(plan.getPriorite());
        dto.setStatut(plan.getStatut());
        dto.setResponsablePlanId(plan.getResponsableId());

        if (responsable != null) {
            dto.setResponsableNom(responsable.getNom() + " " + responsable.getPrenom());
        }

        dto.setDateCreation(plan.getDateCreation());
        dto.setDateEcheance(plan.getDateEcheance());
        dto.setDateCloture(plan.getDateCloture());
        dto.setBudgetEstime(plan.getBudgetEstime());
        dto.setCoutReel(plan.getCoutReel());
        dto.setValideurId(plan.getValideurId());
        dto.setDateValidation(plan.getDateValidation());
        dto.setProgression(plan.calculerProgression());

        if (plan.getValideurId() != null) {
            UserDTO valideur = getUserById(plan.getValideurId());
            if (valideur != null) {
                dto.setValideurNom(valideur.getNom() + " " + valideur.getPrenom());
            }
        }

        return dto;
    }

    private UserDTO getUserById(Long userId) {
        if (userId == null) return null;
        try {
            return restTemplate.getForObject(
                    userServiceUrl + "/api/users/" + userId,
                    UserDTO.class
            );
        } catch (Exception e) {
            return null;
        }
    }


    public ActionDTO ajouterActionAuPlan(CreateAction req, Long planId){
        PlanAction plan = planActionRepo.findById(planId).orElseThrow(()->new RuntimeException("Plan not found"));

        if(plan.getStatut()==StatutPlan.TERMINE){
            throw new RuntimeException("Impossible d'ajouter une action à un plan terminé");
        }
        return actionService.createAction(plan, req);

    }

    public PlanAction getPlanActionEntity(Long id) {
        return planActionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("PlanAction not found"));
    }




}


















