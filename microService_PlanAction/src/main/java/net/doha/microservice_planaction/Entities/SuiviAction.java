package net.doha.microservice_planaction.Entities;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class SuiviAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false)
    private Integer avancement;

    @Column(columnDefinition = "TEXT")
    private String difficultes;

    @Column(columnDefinition = "TEXT")
    private String solutions;

    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;
}
