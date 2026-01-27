package com.example.incidents.service;

import com.example.incidents.dto.IncidentRequestDto;
import com.example.incidents.dto.IncidentResponseDto;
import com.example.incidents.model.Incident;
import com.example.incidents.repository.IncidentRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    // --- 1. CREATE (Créer) ---
    @Transactional
    public IncidentResponseDto create(IncidentRequestDto request) {
        // Récupérer l'utilisateur connecté (via le Token)
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Transformer DTO -> Entity
        Incident incident = mapToEntity(request);

        // Ajouter les infos manquantes
        incident.setCreator_name(username);
        incident.setStatut("OUVERT");

        // Sauvegarder
        Incident saved = incidentRepository.save(incident);

        // Retourner Entity -> DTO
        return mapToDto(saved);
    }

    // --- 2. READ ALL (Tout lire) ---
    public List<IncidentResponseDto> getAll() {
        return incidentRepository.findAll()
                .stream()
                .map(this::mapToDto) // Utilise la méthode privée en bas
                .collect(Collectors.toList());
    }

    // --- 3. READ ONE (Lire un seul) - MANQUANT ---
    public IncidentResponseDto getById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé avec l'ID : " + id));
        return mapToDto(incident);
    }

    // --- 4. UPDATE (Mettre à jour) - MANQUANT ---
    @Transactional
    public IncidentResponseDto update(Long id, IncidentRequestDto request) {
        // On cherche d'abord si l'incident existe
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));

        // On met à jour SEULEMENT les champs modifiables
        // (On ne touche pas à l'ID, ni au créateur, ni à la date de création)
        incident.setDescription(request.getDescription());
        incident.setLocation(request.getLocation());
        incident.setType_incident(request.getType_incident());

        // Note : Le statut se change souvent via une méthode dédiée (ex: closeIncident),
        // mais on peut le laisser ici si le DTO le permettait.

        Incident updated = incidentRepository.save(incident);
        return mapToDto(updated);
    }

    // --- 5. DELETE (Supprimer) - MANQUANT ---
    public void delete(Long id) {
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incident introuvable, suppression impossible.");
        }
        incidentRepository.deleteById(id);
    }

    // ==========================================
    // MÉTHODES PRIVÉES (MAPPERS) - Pour nettoyer le code
    // ==========================================

    private Incident mapToEntity(IncidentRequestDto dto) {
        Incident incident = new Incident();
        // Utilise les setters (plus sûr que le constructeur)
        incident.setDate(dto.getCreation_date());
        incident.setDescription(dto.getDescription());
        incident.setLocation(dto.getLocation());
        incident.setType_incident(dto.getType_incident());
        return incident;
    }

    private IncidentResponseDto mapToDto(Incident incident) {
        IncidentResponseDto dto = new IncidentResponseDto();
        dto.setId(incident.getId());
        dto.setDateTime(incident.getDate());
        dto.setDescription(incident.getDescription());
        dto.setLocation(incident.getLocation());
        dto.setTypeIncident(incident.getType_incident());
        dto.setStatut(incident.getStatut());
        dto.setCreatorName(incident.getCreator_name());
        return dto;
    }
}