package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String description;
    private int Avancement=0;
    private TypeAction type;
    private LocalDateTime dateDebut;
    private LocalDateTime dateEcheance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAction statut = StatutAction.A_FAIRE;


    @Enumerated(EnumType.STRING)
    private Priorite priorite = Priorite.MOYENNE;
    private Integer dureeEstimee;
    private Integer dureeReelle;

    @Column(columnDefinition = "TEXT")
    private String ressourcesNecessaires;
    private Long responsableId ;
    private String indicateurEfficacite;
    private String ressourcesReelle;

    @ManyToOne
    @JoinColumn(name = "planAction_id")
    private PlanAction planAction;


}
