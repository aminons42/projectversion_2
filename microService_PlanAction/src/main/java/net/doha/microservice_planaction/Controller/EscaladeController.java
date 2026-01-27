package net.doha.microservice_planaction.Controller;


import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.CreateEscaladeRequeste;
import net.doha.microservice_planaction.DTO.EscaladeDTO;
import net.doha.microservice_planaction.Entities.StatutEscalade;
import net.doha.microservice_planaction.service.EscaladeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/escalades")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EscaladeController {
    private final EscaladeService escaladeService;

    // Créer une escalade
    @PostMapping
    public EscaladeDTO creerEscalade(@RequestBody CreateEscaladeRequeste req) {
        return escaladeService.creerEscalade(req);
    }

    // Traiter une escalade
    @PostMapping("/{escaladeId}/traiter/{userId}")
    public EscaladeDTO traiterEscalade(@PathVariable Long escaladeId, @PathVariable Long userId) {
        return escaladeService.traiterEscalade(escaladeId, userId);
    }

    // Résoudre une escalade
    @PostMapping("/{escaladeId}/resoudre")
    public EscaladeDTO resoudreEscalade(@PathVariable Long escaladeId, @RequestParam String resolution) {
        return escaladeService.resoudreEscalade(escaladeId, resolution);
    }

    // Récupérer une escalade par ID
    @GetMapping("/{escaladeId}")
    public EscaladeDTO getEscalade(@PathVariable Long escaladeId) {
        return escaladeService.getEscalade(escaladeId);
    }

    // Récupérer escalades par action
    @GetMapping("/action/{actionId}")
    public List<EscaladeDTO> getEscaladesByAction(@PathVariable Long actionId) {
        return escaladeService.getEscaladesByAction(actionId);
    }

    // Récupérer escalades par plan
    @GetMapping("/plan/{planId}")
    public List<EscaladeDTO> getEscaladesByPlan(@PathVariable Long planId) {
        return escaladeService.getEscaladesByPlan(planId);
    }

    // Récupérer escalades par statut
    @GetMapping("/statut/{statut}")
    public List<EscaladeDTO> getEscaladesByStatut(@PathVariable StatutEscalade statut) {
        return escaladeService.getEscaladesByStatut(statut);
    }
}
