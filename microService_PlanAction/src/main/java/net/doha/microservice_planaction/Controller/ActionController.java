package net.doha.microservice_planaction.Controller;


import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.ActionDTO;
import net.doha.microservice_planaction.DTO.CreateAction;
import net.doha.microservice_planaction.DTO.UpdateActionRequest;
import net.doha.microservice_planaction.Entities.StatutAction;
import net.doha.microservice_planaction.Entities.TypeAction;
import net.doha.microservice_planaction.service.ActionService;
import net.doha.microservice_planaction.service.PlanActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actions")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ActionController {
    private final ActionService actionService;
    private final PlanActionService planActionService;

    @PostMapping("/plan/{PlanId}")
    public ActionDTO createAction(@PathVariable Long PlanId, @RequestBody CreateAction req) {
        var plan=planActionService.getPlanActionEntity(PlanId);
        return actionService.createAction(plan, req);
    }

    @PutMapping("{actionId}")
    public ActionDTO updateAction(@PathVariable Long actionId, @RequestBody UpdateActionRequest req) {
             return actionService.updateAction(actionId, req);
    }


    @DeleteMapping("/{actionId}")
    public void deleteAction(@PathVariable Long actionId) {
        actionService.deleteAction(actionId);
    }
    @GetMapping("/{actionId}")
    public ActionDTO getActionById(@PathVariable Long actionId) {
        return actionService.getActionByiD(actionId);
    }


    @GetMapping("/plan/{planId}")
    public List<ActionDTO> getActionsByPlan(@PathVariable Long planId) {
        return actionService.getActionByPlanAction(planId);
    }


    @GetMapping("/statut/{statut}")
    public List<ActionDTO> getActionsByStatut(@PathVariable StatutAction statut) {
        return actionService.getActionsByStatut(statut);
    }

    @GetMapping("/responsable/{responsableId}")
    public List<ActionDTO> getActionsByResponsable(@PathVariable Long responsableId) {
        return actionService.getActionsByResponsable(responsableId);
    }


    @GetMapping("/type/{typeAction}")
    public List<ActionDTO> getActionsByType(@PathVariable TypeAction typeAction) {
        return actionService.getActionsByType(typeAction);
    }


    @GetMapping
    public List<ActionDTO> getAllActions() {
        return actionService.getAllActions();
    }

    @GetMapping("/stats/retard")
    public ActionsEnRetardStats getActionsEnRetard() {
        List<ActionDTO> allActions = actionService.getAllActions();
        
        long enRetard = allActions.stream()
            .filter(a -> StatutAction.EN_RETARD.equals(a.getStatut()))
            .count();
        
        return new ActionsEnRetardStats(enRetard);
    }

    // Inner class pour la réponse stats
    public static class ActionsEnRetardStats {
        private long count;

        public ActionsEnRetardStats(long count) {
            this.count = count;
        }

        public long getCount() { return count; }
        public void setCount(long count) { this.count = count; }
    }}
