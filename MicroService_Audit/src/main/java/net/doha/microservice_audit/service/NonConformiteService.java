package net.doha.microservice_audit.service;

import net.doha.microservice_audit.DTO.CreateNonConformiteRequest;
import net.doha.microservice_audit.DTO.NonConformiteDTO;
import net.doha.microservice_audit.DTO.UserDTO;
import net.doha.microservice_audit.Repository.AuditRepo;
import net.doha.microservice_audit.Repository.NonConformiteRepo;
import net.doha.microservice_audit.entities.Audit;
import net.doha.microservice_audit.entities.NonComformite;
import net.doha.microservice_audit.entities.StatutNC;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

public class NonConformiteService {

    private NonConformiteRepo ncRepository;


    private AuditRepo auditRepository;


    private RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://localhost:8080/api/users";

    public NonConformiteDTO createNonConformite(CreateNonConformiteRequest request) {
        Audit audit = auditRepository.findById(request.getAuditId())
                .orElseThrow(() -> new RuntimeException("Audit non trouvé"));

        NonComformite nc = new NonComformite();
        nc.setAudit(audit);
        nc.setDescription(request.getDescription());
        nc.setGravite(request.getGravite());
        nc.setCauseRacine(request.getCauseRacine());
        nc.setStatut(StatutNC.OUVERTE);

        ncRepository.save(nc);
        return convertToDTO(nc);
    }

    public List<NonConformiteDTO> getNonConformitesByAudit(Long auditId) {
        return ncRepository.findByAuditId(auditId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NonConformiteDTO getNonConformiteById(Long id) {
        NonComformite nc = ncRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Non-conformité non trouvée"));
        return convertToDTO(nc);
    }

    public List<NonConformiteDTO> getNonConformitesByStatut(StatutNC statut) {
        return ncRepository.findByStatut(statut).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public NonConformiteDTO lierPlanAction(Long ncId, Long planActionId) {
        NonComformite nc = ncRepository.findById(ncId)
                .orElseThrow(() -> new RuntimeException("Non-conformité non trouvée"));

        nc.setPlanActionId(planActionId);
        nc.setStatut(StatutNC.EN_TRAITEMENT);

        nc = ncRepository.save(nc);
        return convertToDTO(nc);
    }

    private NonConformiteDTO convertToDTO(NonComformite nc) {
        NonConformiteDTO dto = new NonConformiteDTO();
        dto.setId(nc.getId());
        dto.setDescription(nc.getDescription());
        dto.setGravite(nc.getGravite());
        dto.setCauseRacine(nc.getCauseRacine());
        dto.setActionImmediateRealisee(nc.getActionImmediateRealisee());
        dto.setPlanActionId(nc.getPlanActionId());
        dto.setStatut(nc.getStatut());
        dto.setDateDetection(nc.getDateDetection());
        dto.setDateCloture(nc.getDateCloture());
        dto.setAuditId(nc.getAudit().getId());
        dto.setAuditTitre(nc.getAudit().getTitre());

        if (nc.getResponsableId() != null) {
            try {
                UserDTO responsable = restTemplate.getForObject(
                        USER_SERVICE_URL + "/" + nc.getResponsableId(),
                        UserDTO.class
                );
                dto.setResponsableId(nc.getResponsableId());
                dto.setResponsableNom(responsable.getNom() + " " + responsable.getPrenom());
            } catch (Exception e) {
                dto.setResponsableId(nc.getResponsableId());
            }
        }

        return dto;
    }
}
