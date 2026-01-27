package com.example.incidents.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime date;
    private String description;
    private String location;
    @Column(nullable = false)
    private String creator_name;
    private String statut;
    @Enumerated(EnumType.STRING)
    private TypeIncident type_incident;


}
