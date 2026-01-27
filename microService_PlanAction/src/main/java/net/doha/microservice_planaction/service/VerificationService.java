package net.doha.microservice_planaction.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.ActionDTO;
import net.doha.microservice_planaction.DTO.CreateVerificationRequest;
import net.doha.microservice_planaction.DTO.UserDTO;
import net.doha.microservice_planaction.DTO.VerificationDTO;
import net.doha.microservice_planaction.Entities.*;
import net.doha.microservice_planaction.Repository.ActionRepo;
import net.doha.microservice_planaction.Repository.VerificationRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationService {
    private final VerificationRepo verificationRepo;
    private final ActionService actionService;
    private final ActionRepo actionRepo;

    private final RestTemplate restTemplate;

    @Value("${service.users.url:http://service_utilisateurs}")
    private String userServiceUrl;


    public VerificationDTO convertToDTO(Verification verification, UserDTO verificateur) {
        VerificationDTO verifDTO = new VerificationDTO();
        verifDTO.setId(verification.getId());
        verifDTO.setVerificateurId(verification.getVerificateurId());
        verifDTO.setDateVerification(verification.getDateProchaineSurveillance());
        if (verificateur != null) {
            verifDTO.setVerificateurNom(verificateur.getNom());
        }

        verifDTO.setResultat(verification.getResultat());
        verifDTO.setCommentaire(verification.getCommentaire());
        verifDTO.setEfficace(verification.getEfficace());
        verifDTO.setDateProchaineSurveillance(verification.getDateProchaineSurveillance());
        if (verification.getAction() != null) {
            verifDTO.setActionId(verification.getAction().getId());
        }
        return verifDTO;
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


    public VerificationDTO createVerification(CreateVerificationRequest req, Long ActionId) {
        Action action = actionService.getAction(ActionId);
        Verification verification = new Verification();
        verification.setAction(action);
        verification.setVerificateurId(req.getVerificateurId());
        verification.setResultat(req.getResultat());
        verification.setCommentaire(req.getCommentaire());
        verification.setDateProchaineSurveillance(req.getDateProchaineSurveillance());
        if (req.getResultat() == ResultatVerification.CONFORME) {
            verification.valider();
            action.setStatut(StatutAction.VERIFIEE);
        } else {
            verification.rejeter();
            action.setStatut(StatutAction.A_FAIRE);
        }
        verificationRepo.save(verification);
        actionRepo.save(action);
        UserDTO verificateur = getUserById(verification.getVerificateurId());
        return convertToDTO(verification, verificateur);
    }

    public VerificationDTO getVerificationById(Long verificationId) {
        Verification verification  =verificationRepo.findById(verificationId).orElseThrow(()->new RuntimeException("verification not found"));
        UserDTO verificateur = getUserById(verification.getVerificateurId());
        return convertToDTO(verification, verificateur);
    }
    public List<VerificationDTO> getVerificationByAction(Long actionId){
            List<Verification> verifications = verificationRepo.findByActionId(actionId);
            return verifications.stream()
                    .map(v->convertToDTO(v,getUserById(v.getVerificateurId())))
                    .collect(Collectors.toList());
    }

    public List<VerificationDTO> getVerificationSByResultat(ResultatVerification resultat){
        List<Verification> verifications = verificationRepo.findByResultat(resultat);
        return verifications.stream()
                .map(v->convertToDTO(v,getUserById(v.getVerificateurId())))
                .collect(Collectors.toList());

    }
    public List<VerificationDTO> getALLVerification(){
        List<Verification> verifications = verificationRepo.findAll();
        return verifications.stream()
                .map(v->convertToDTO(v,getUserById(v.getVerificateurId())))
                .collect(Collectors.toList());
    }


}