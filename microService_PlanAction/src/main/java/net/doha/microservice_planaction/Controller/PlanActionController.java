package net.doha.microservice_planaction.Controller;


import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.ActionDTO;
import net.doha.microservice_planaction.DTO.CreateAction;
import net.doha.microservice_planaction.DTO.CreatePlanActionRequest;
import net.doha.microservice_planaction.DTO.PlanActionDTO;
import net.doha.microservice_planaction.Entities.StatutPlan;
import net.doha.microservice_planaction.service.PlanActionService;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planaction")
@RequiredArgsConstructor
public class PlanActionController {
    private final PlanActionService planActionService;

    @PostMapping
    public PlanActionDTO createPlan(@RequestBody  CreatePlanActionRequest req, Authentication auth) throws BadRequestException {
        return planActionService.createPlanAction(req, auth);
    }

    @GetMapping
    public List<PlanActionDTO> getAllPlans() {
        return planActionService.getAllPlans();
    }

    @GetMapping("/{planId}")
    public PlanActionDTO getPlanById(@PathVariable Long planId) {
        return planActionService.getPlanById(planId);
    }

    @GetMapping("/mes-plans")
    public List<PlanActionDTO> getMyPlans(Authentication auth) {
        return planActionService.getMyPlans(auth);
    }

    @GetMapping("/statut/{statut}")
    public List<PlanActionDTO> getPlansByStatut(@PathVariable StatutPlan statut) {
        return planActionService.getPlansByStatut(statut);
    }

    @PostMapping("/{planId}/valider")
    public PlanActionDTO validerPlan(@PathVariable Long planId, Authentication auth) {
        return planActionService.validerPlan(planId, auth);
    }

    @PostMapping("/{planId}/cloturer")
    public PlanActionDTO cloturerPlan(@PathVariable Long planId) {
        return planActionService.cloturerPlan(planId);
    }

    @PostMapping("/{planId}/actions")
    public ActionDTO ajouterActionAuPlan(@PathVariable Long planId,
                                         @RequestBody CreateAction req) {
        return planActionService.ajouterActionAuPlan(req, planId);
    }

    @PutMapping("/{planId}")
public PlanActionDTO updatePlan(@PathVariable Long planId, @RequestBody CreatePlanActionRequest req) throws BadRequestException {
    return planActionService.updatePlanAction(planId, req);
}

@DeleteMapping("/{planId}")
public void deletePlan(@PathVariable Long planId) {
    planActionService.deletePlanAction(planId);
}



}
