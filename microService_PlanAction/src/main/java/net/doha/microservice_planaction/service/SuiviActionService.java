package net.doha.microservice_planaction.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.CreateSuiviRequest;
import net.doha.microservice_planaction.DTO.SuiviActionDTO;
import net.doha.microservice_planaction.Entities.Action;
import net.doha.microservice_planaction.Entities.SuiviAction;
import net.doha.microservice_planaction.Repository.ActionRepo;
import net.doha.microservice_planaction.Repository.SuiviActionRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SuiviActionService {
     private final SuiviActionRepo suiviActionRepo;
     private final ActionRepo actionRepo;

     private SuiviActionDTO convertToDTO(SuiviAction suivi) {
         SuiviActionDTO dto = new SuiviActionDTO();

         dto.setId(suivi.getId());
         dto.setDate(suivi.getDate());
         dto.setCommentaire(suivi.getCommentaire());
         dto.setAvancement(suivi.getAvancement());

         if (suivi.getAction() != null) {
             dto.setActionId(suivi.getAction().getId());
         }
         dto.setDifficultes(suivi.getDifficultes());
         dto.setSolutions(suivi.getSolutions());


         return dto;
     }



    public SuiviActionDTO ajouterSuivi(CreateSuiviRequest req) {
        Action action = actionRepo.findById(req.getActionId())
                .orElseThrow(() -> new EntityNotFoundException("Action introuvable"));

        SuiviAction suivi = new SuiviAction();
        suivi.setAction(action);
        suivi.setCommentaire(req.getCommentaire());
        suivi.setDate(LocalDateTime.now());

        if (req.getAvancement() != null) {
            suivi.setAvancement(req.getAvancement());

            action.setAvancement(req.getAvancement());
            actionRepo.save(action);
        }

        if (req.getUtilisateurId() != null) {
            suivi.setUtilisateurId(req.getUtilisateurId());
        }

        suiviActionRepo.save(suivi);

        return convertToDTO(suivi);
    }

    // 🔹 2. Récupérer l'historique de suivi d'une action
    public List<SuiviActionDTO> getSuiviByAction(Long actionId) {
        List<SuiviAction> suivis = suiviActionRepo.findByActionIdOrderByDateDesc(actionId);

        return suivis.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 🔹 3. (Optionnel) récupérer un suivi par id
    public SuiviActionDTO getSuiviById(Long id) {
        SuiviAction suivi = suiviActionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suivi introuvable"));

        return convertToDTO(suivi);
    }

     }






