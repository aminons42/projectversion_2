package net.doha.microservice_planaction.service;

import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.*;
import net.doha.microservice_planaction.Entities.Action;
import net.doha.microservice_planaction.Entities.PlanAction;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;
import net.doha.microservice_planaction.Repository.ActionRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepo actionRepo;
    private final RestTemplate restTemplate;

    @Value("${service.users.url:http://service_utilisateurs}")
    private String userServiceUrl;

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

    private ActionDTO convertToDTO(Action action, UserDTO responsable) {
        ActionDTO dto = new ActionDTO();

        dto.setId(action.getId());
        dto.setDescription(action.getDescription());
        dto.setTypeAction(action.getType());
        dto.setResponsableId(action.getResponsableId());
        // Responsable nom complet (si API user a renvoyé un user)
        if (responsable != null) {
            dto.setResponsableNom(responsable.getNom() + " " + responsable.getPrenom());}
        dto.setDateDebut(action.getDateDebut());
        dto.setDateEcheance(action.getDateEcheance());
        dto.setDureeEstimee(action.getDureeEstimee());
        dto.setDureeReelle(action.getDureeReelle());
        dto.setStatut(action.getStatut());
        dto.setPriorite(action.getPriorite());
        // Progression = Avancement (s'il existe dans Action)
        dto.setProgression(action.getAvancement());
        dto.setRessourcesNecessaires(action.getRessourcesNecessaires());
        dto.setIndicateurEfficacite(action.getIndicateurEfficacite());
        // Rattachement plan d'action
        if (action.getPlanAction() != null) {
            dto.setPlanActionId(action.getPlanAction().getId());
        }

        return dto;
    }

    public ActionDTO createAction(PlanAction planAction, CreateAction req) {
        Action action = new Action();
        action.setPlanAction(planAction);
        action.setDescription(req.getDescription());
        action.setType(req.getTypeAction());
        action.setResponsableId(req.getResponsableId());
        action.setDateEcheance(req.getDateEcheance());
        action.setDureeEstimee(req.getDureeEstimee());
        action.setPriorite(req.getPriorite());
        action.setRessourcesNecessaires(req.getRessourcesNecessaires());
        action.setStatut(StatutAction.A_FAIRE);
        actionRepo.save(action);
        UserDTO responsable = getUserById(action.getResponsableId());

        return convertToDTO(action, responsable);
    }
    public ActionDTO updateAction(Long ActionId, UpdateActionRequest req) {
        Action action = actionRepo.findById(ActionId).orElseThrow(() -> new RuntimeException("Action not found"));
        if (action.getStatut() == StatutAction.TERMINEE || action.getStatut() == StatutAction.VERIFIEE) {
            throw new RuntimeException("Impossible de modifier une action terminée ou vérifiée");
        }
        if (req.getDescription() != null) {
            action.setDescription(req.getDescription());
        }
        if (req.getTypeAction() != null) {
            action.setType(req.getTypeAction());
        }
        if (req.getResponsableId() != null) {
            action.setResponsableId(req.getResponsableId());
        }
        if (req.getDateEcheance() != null) {
            action.setDateEcheance(req.getDateEcheance());
        }
        if (req.getDureeEstimee() != null) {
            action.setDureeEstimee(req.getDureeEstimee());
        }
        if (req.getPriorite() != null) {
            action.setPriorite(req.getPriorite());
        }
        if (req.getRessourcesNecessaires() != null) {
            action.setRessourcesNecessaires(req.getRessourcesNecessaires());
        }
        if (req.getIndicateurEfficacite() != null) {
            action.setIndicateurEfficacite(req.getIndicateurEfficacite());
        }
        actionRepo.save(action);
        UserDTO responsable = getUserById(action.getResponsableId());
        return convertToDTO(action, responsable);

    }
    public ActionDTO getActionByiD(Long id){
        Action action=actionRepo.findById(id).orElseThrow(() -> new RuntimeException("Action not found"));
        UserDTO responsable=getUserById(action.getResponsableId());
        return convertToDTO(action, responsable);
    }
     public List<ActionDTO> getActionByPlanAction(Long planActionId){
         List<Action> actions=actionRepo.findByPlanActionId(planActionId);
         return actions.stream().map(a->convertToDTO(a,getUserById(a.getResponsableId() ) ) ).collect(Collectors.toList());
     }
    public List<ActionDTO> getActionsByStatut(StatutAction statut) {
        List<Action> actions = actionRepo.findByStatut(statut);

        return actions.stream()
                .map(a -> convertToDTO(a, getUserById(a.getResponsableId())))
                .collect(Collectors.toList());
    }

    public List<ActionDTO> getActionsByResponsable(Long responsableId) {
        List<Action> actions = actionRepo.findByResponsableId(responsableId);

        return actions.stream()
                .map(a -> convertToDTO(a, getUserById(a.getResponsableId())))
                .collect(Collectors.toList());
    }
    public List<ActionDTO> getActionsByType(TypeAction typeAction) {
        List<Action> actions = actionRepo.findByType(typeAction);

        return actions.stream()
                .map(a -> convertToDTO(a, getUserById(a.getResponsableId())))
                .collect(Collectors.toList());
    }

    public void deleteAction(Long ActionId) {
        Action action = actionRepo.findById(ActionId).orElseThrow(() -> new RuntimeException("Action not found"));
        if(action.getStatut()==StatutAction.TERMINEE||action.getStatut()==StatutAction.VERIFIEE){
            throw new RuntimeException("Impossible de supprimer une action terminer");
        }
        actionRepo.delete(action);
    }

    public List<ActionDTO> getAllActions() {
        List<Action> actions = actionRepo.findAll();
        return actions.stream().map(a -> convertToDTO(a, getUserById(a.getResponsableId()))).collect(Collectors.toList());
    }

    public Action getAction(Long ActionId) {
       return actionRepo.findById(ActionId).orElseThrow(() -> new RuntimeException("Action not found"));

    }


    }
