package com.example.incidents.dto;

import com.example.incidents.model.TypeIncident;
import lombok.Data;

import java.time.LocalDateTime;
@Data

public class IncidentResponseDto {
    private Long id ;
    private LocalDateTime dateTime;
    private String location;
    private TypeIncident typeIncident;
    private String description;
    private String creatorName;
    private String statut;

}
