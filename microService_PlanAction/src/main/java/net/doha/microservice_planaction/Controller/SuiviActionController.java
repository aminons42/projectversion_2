package net.doha.microservice_planaction.Controller;

import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.CreateSuiviRequest;
import net.doha.microservice_planaction.DTO.SuiviActionDTO;
import net.doha.microservice_planaction.service.SuiviActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suivis")
@RequiredArgsConstructor
@CrossOrigin("*")
public class SuiviActionController {

    private final SuiviActionService  suiviActionService;
    // Ajouter un suivi
    @PostMapping
    public SuiviActionDTO ajouterSuivi(@RequestBody CreateSuiviRequest req) {
        return suiviActionService .ajouterSuivi(req);
    }

    // Historique de suivi par action
    @GetMapping("/action/{actionId}")
    public List<SuiviActionDTO> getSuiviByAction(@PathVariable Long actionId) {
        return suiviActionService.getSuiviByAction(actionId);
    }

    // Récupérer un suivi par ID
    @GetMapping("/{suiviId}")
    public SuiviActionDTO getSuiviById(@PathVariable Long suiviId) {
        return suiviActionService.getSuiviById(suiviId);
    }


}
