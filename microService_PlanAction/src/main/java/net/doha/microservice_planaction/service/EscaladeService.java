package net.doha.microservice_planaction.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.doha.microservice_planaction.DTO.CreateEscaladeRequeste;
import net.doha.microservice_planaction.DTO.EscaladeDTO;
import net.doha.microservice_planaction.DTO.UserDTO;
import net.doha.microservice_planaction.Entities.Action;
import net.doha.microservice_planaction.Entities.Escalade;
import net.doha.microservice_planaction.Entities.PlanAction;
import net.doha.microservice_planaction.Entities.StatutEscalade;
import net.doha.microservice_planaction.Repository.ActionRepo;
import net.doha.microservice_planaction.Repository.EscaladeRepo;
import net.doha.microservice_planaction.Repository.PlanActionRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EscaladeService {
    private final EscaladeRepo escaladeRepo ;
    private final RestTemplate restTemplate ;
    private final ActionRepo actionRepo;
    private final PlanActionRepo planActionRepo;



    public EscaladeDTO convertToDTO(Escalade esc) {
        EscaladeDTO dto = new EscaladeDTO();

        dto.setId(esc.getId());
        dto.setMotif(esc.getMotif());
        dto.setNiveau(esc.getNiveau());
        dto.setStatut(esc.getStatut());
        dto.setDate(esc.getDate());
        dto.setDescription(esc.getDescription());
        dto.setResolution(esc.getResolution());
        dto.setTraitePar(esc.getTraitePar());

        if (esc.getPlanAction() != null)
            dto.setPlanActionId(esc.getPlanAction().getId());

        if (esc.getAction() != null)
            dto.setActionId(esc.getAction().getId());

        return dto;
    }
    public EscaladeDTO creerEscalade(CreateEscaladeRequeste req) {

        Escalade esc = new Escalade();
        esc.setMotif(req.getMotif());
        esc.setNiveau(req.getNiveau());
        esc.setDescription(req.getDescription());

        if (req.getActionId() != null) {
            Action action = actionRepo.findById(req.getActionId())
                    .orElseThrow(() -> new RuntimeException("Action introuvable"));
            esc.setAction(action);
        }

        if (req.getPlanActionId() != null) {
            PlanAction plan = planActionRepo.findById(req.getPlanActionId())
                    .orElseThrow(() -> new RuntimeException("Plan d’action introuvable"));
            esc.setPlanAction(plan);
        }

        return convertToDTO(escaladeRepo.save(esc));
    }

    // ------------------------------
    // TRAITER
    // ------------------------------
    public EscaladeDTO traiterEscalade(Long id, Long userId) {
        Escalade esc = escaladeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Escalade introuvable"));
        esc.traiter(userId);
        return convertToDTO(escaladeRepo.save(esc));
    }

    // ------------------------------
    // RÉSOUDRE
    // ------------------------------

    public EscaladeDTO resoudreEscalade(Long id, String resolution) {
        Escalade esc = escaladeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Escalade introuvable"));

        esc.resoudre(resolution);
        return convertToDTO(escaladeRepo.save(esc));
    }

    // ------------------------------
    // GET ONE
    // ------------------------------
    public EscaladeDTO getEscalade(Long id) {
        Escalade esc = escaladeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Escalade introuvable"));

        return convertToDTO(esc);
    }

    // ------------------------------
    // GET BY ACTION
    // ------------------------------

    public List<EscaladeDTO> getEscaladesByAction(Long actionId) {
        return escaladeRepo.findByActionId(actionId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------
    // GET BY PLAN
    // ------------------------------

    public List<EscaladeDTO> getEscaladesByPlan(Long planId) {
        return escaladeRepo.findByPlanActionId(planId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------
    // GET BY STATUT
    // ------------------------------
    public List<EscaladeDTO> getEscaladesByStatut(StatutEscalade statut) {
        return escaladeRepo.findByStatut(statut)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }




}

