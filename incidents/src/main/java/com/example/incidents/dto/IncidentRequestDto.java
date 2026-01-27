package com.example.incidents.dto;


import com.example.incidents.model.TypeIncident;
import lombok.Data;

import java.time.LocalDateTime;
@Data

public class IncidentRequestDto {
    private LocalDateTime creation_date;
    private String description;
    private String Location;
    private TypeIncident type_incident;
}
