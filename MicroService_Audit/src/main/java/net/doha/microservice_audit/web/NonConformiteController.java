package net.doha.microservice_audit.web;

import jakarta.validation.Valid;
import net.doha.microservice_audit.DTO.CreateNonConformiteRequest;
import net.doha.microservice_audit.DTO.NonConformiteDTO;
import net.doha.microservice_audit.entities.StatutNC;
import net.doha.microservice_audit.service.NonConformiteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/non-conformites")
public class NonConformiteController {

    private NonConformiteService ncService;

    public NonConformiteController(NonConformiteService ncService) {
        this.ncService = ncService;
    }



    @PostMapping
    public ResponseEntity<NonConformiteDTO> createNonConformite(
            @Valid @RequestBody CreateNonConformiteRequest request) {
        NonConformiteDTO nc = ncService.createNonConformite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nc);
    }

    @GetMapping
    public ResponseEntity<List<NonConformiteDTO>> getAllNonConformites(
            @RequestParam(required = false) StatutNC statut) {
        if (statut != null) {
            return ResponseEntity.ok(ncService.getNonConformitesByStatut(statut));
        }
        return ResponseEntity.ok(ncService.getNonConformitesByAudit(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NonConformiteDTO> getNonConformiteById(@PathVariable Long id) {
        return ResponseEntity.ok(ncService.getNonConformiteById(id));
    }

    @GetMapping("/audit/{auditId}")
    public ResponseEntity<List<NonConformiteDTO>> getNonConformitesByAudit(
            @PathVariable Long auditId) {
        return ResponseEntity.ok(ncService.getNonConformitesByAudit(auditId));
    }

    @PutMapping("/{ncId}/plan-action/{planActionId}")
    public ResponseEntity<NonConformiteDTO> lierPlanAction(
            @PathVariable Long ncId,
            @PathVariable Long planActionId) {
        return ResponseEntity.ok(ncService.lierPlanAction(ncId, planActionId));
    }
}

