package net.doha.microservice_planaction.Controller;


import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.CreateVerificationRequest;
import net.doha.microservice_planaction.DTO.VerificationDTO;
import net.doha.microservice_planaction.Entities.ResultatVerification;
import net.doha.microservice_planaction.service.VerificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@CrossOrigin("*")
public class VerificationController {
    private final VerificationService verificationService;

    // Créer une vérification pour une action
    @PostMapping("/action/{actionId}")
    public VerificationDTO createVerification(@PathVariable Long actionId,
                                              @RequestBody CreateVerificationRequest req) {
        return verificationService.createVerification(req, actionId);
    }

    // Obtenir une vérification par ID
    @GetMapping("/{verificationId}")
    public VerificationDTO getVerificationById(@PathVariable Long verificationId) {
        return verificationService.getVerificationById(verificationId);
    }

    // Vérifications par action
    @GetMapping("/action/{actionId}/all")
    public List<VerificationDTO> getVerificationByAction(@PathVariable Long actionId) {
        return verificationService.getVerificationByAction(actionId);
    }

    // Vérifications par résultat
    @GetMapping("/resultat/{resultat}")
    public List<VerificationDTO> getVerificationByResultat(@PathVariable ResultatVerification resultat) {
        return verificationService.getVerificationSByResultat(resultat);
    }

    // Toutes les vérifications
    @GetMapping
    public List<VerificationDTO> getAllVerifications() {
        return verificationService.getALLVerification();
    }
}
