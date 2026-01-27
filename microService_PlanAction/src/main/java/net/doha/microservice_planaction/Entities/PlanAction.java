package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="ActionPrise") @Builder
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class PlanAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String titre;
    @Column
    private String description;

    @Column
    @Enumerated(EnumType.STRING)
    private StatutPlan statut ;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private TypeAction type;
    @Column
    @NotNull
    @Enumerated(EnumType.STRING)
    private SourcePlan source;
    private Long sourceId;


    private Priorite priorite;

    private LocalDateTime dateCreation;
    private LocalDateTime dateEcheance;
    private LocalDateTime dateCloture;
    private Long responsableId;
    private Double budgetEstime;
    private Double coutReel;
    @Column(name = "valideur_id")
    private Long valideurId;

    private LocalDateTime dateValidation;
    @OneToMany(mappedBy = "planAction",cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Action> actions =new ArrayList<Action>();


    public void valider(Long valideurId) {
        this.statut = StatutPlan.VALIDE;
        this.valideurId = valideurId;
        this.dateValidation = LocalDateTime.now();
    }

    public void cloturer() {
        this.statut = StatutPlan.CLOTURE;
        this.dateCloture = LocalDateTime.now();
    }
    public Integer calculerProgression() {
        if (actions.isEmpty()) return 0;
        long terminees = actions.stream()
                .filter(a -> a.getStatut() == StatutAction.TERMINEE || a.getStatut() == StatutAction.VERIFIEE)
                .count();
        return (int) ((terminees * 100.0) / actions.size());
    }


}
