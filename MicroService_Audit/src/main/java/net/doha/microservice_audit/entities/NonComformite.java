package net.doha.microservice_audit.entities;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NonComformite {
    @Id
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;



    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Gravite gravite = Gravite.MINEURE;

    @Column(name = "cause_racine", columnDefinition = "TEXT")
    private String causeRacine;

    @Column(name = "action_immediate", columnDefinition = "TEXT")
    private String actionImmediateRealisee;

    // Référence vers le service Plans d'Action
    @Column(name = "plan_action_id")
    private Long planActionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatutNC statut = StatutNC.OUVERTE;

    @Column(name = "date_detection", nullable = false, updatable = false)
    private LocalDateTime dateDetection = LocalDateTime.now();

    @Column(name = "date_cloture")
    private LocalDateTime dateCloture;

    // Référence vers le responsable du traitement
    @Column(name = "responsable_id")
    private Long responsableId;

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reponse_id")
    private ReponseAUdit reponseAudit;



    // Méthodes métier
    public void creerPlanAction(Long planActionId) {
        this.planActionId = planActionId;
        this.statut = StatutNC.EN_TRAITEMENT;
    }

    public void cloturer() {
        this.statut = StatutNC.CLOTUREE;
        this.dateCloture = LocalDateTime.now();
    }

    public void escalader(Long nouveauResponsableId) {
        this.responsableId = nouveauResponsableId;
        if (this.gravite == Gravite.MAJEURE) {
            this.gravite = Gravite.CRITIQUE;
        }
    }
}

