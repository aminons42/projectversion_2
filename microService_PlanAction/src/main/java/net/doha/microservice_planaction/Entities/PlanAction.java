package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data; 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="plan_action") 
@Getter @Setter @ToString @AllArgsConstructor @NoArgsConstructor
public class PlanAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String titre;
    @Column(columnDefinition = "TEXT")
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

    private LocalDate  dateCreation;
    private LocalDate dateEcheance;
    private LocalDate dateCloture;
    private Long responsableId;
    private Double budgetEstime;
    private Double coutReel;
    @Column(name = "valideur_id")
    private Long valideurId;

    private LocalDate dateValidation;
    @OneToMany(mappedBy = "planAction",cascade=CascadeType.ALL,orphanRemoval = true)
    private List<Action> actions =new ArrayList<>();


    public void valider(Long valideurId) {
        this.statut = StatutPlan.VALIDE;
        this.valideurId = valideurId;
        this.dateValidation = LocalDate.now();
    }

    public void cloturer() {
        this.statut = StatutPlan.CLOTURE;
        this.dateCloture = LocalDate.now();
    }
    public Integer calculerProgression() {
        if (actions.isEmpty()) return 0;
        long terminees = actions.stream()
                .filter(a -> a.getStatut() == StatutAction.TERMINEE || a.getStatut() == StatutAction.VERIFIEE)
                .count();
        return (int) ((terminees * 100.0) / actions.size());
    }


}
