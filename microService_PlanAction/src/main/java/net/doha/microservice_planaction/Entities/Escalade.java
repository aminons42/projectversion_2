package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Escalade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MotifEscalade motif;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NiveauEscalade niveau;

    @Column(nullable = false)
    private LocalDateTime date = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEscalade statut = StatutEscalade.OUVERTE;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "traite_par")
    private Long traitePar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_action_id")
    private PlanAction planAction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private Action action;

    public void traiter(Long userId) {
        this.statut = StatutEscalade.EN_TRAITEMENT;
        this.traitePar = userId;
    }

    public void resoudre(String resolution) {
        this.statut = StatutEscalade.RESOLUE;
        this.resolution = resolution;
    }
}
