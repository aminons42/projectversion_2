package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;


@Entity
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
@Table(name = "action")  // ← Nom de table
public class Action {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String description;
    private int Avancement=0;
    private TypeAction type;
    private LocalDate dateDebut;
    private LocalDate dateEcheance;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutAction statut = StatutAction.A_FAIRE;


    @Enumerated(EnumType.STRING)
    private Priorite priorite = Priorite.MOYENNE;
    private Integer dureeEstimee;
    private Integer dureeReelle;
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String ressourcesNecessaires;
    private Long responsableId ;
    private String indicateurEfficacite;
    private String ressourcesReelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_action_id")
    private PlanAction planAction;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SuiviAction> suivis = new ArrayList<>();
    
    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Verification> verifications = new ArrayList<>();


}
