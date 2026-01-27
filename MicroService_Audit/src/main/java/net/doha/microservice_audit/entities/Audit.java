package net.doha.microservice_audit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor @Builder
@Table(name="Audits")
public class Audit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Audit_id")
    private Long id ;



    @Column(nullable = false, length = 200)
    private String titre ;

    @Column(columnDefinition = "TEXT")
    private String description ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeAudit typeAudit;


    @Column(name = "date_debut")
    private LocalDateTime dateDebut ;
    @Column(name = "date_fin")
    private LocalDateTime dateFin;
    @Column(name = "date_planifiee")
    private LocalDateTime datePlanifie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatutAudit statut=StatutAudit.PLANIFIE;

    @Column(name = "auditeur_id", nullable = false)
    private Long auditeurId;

    @Column(length = 100)
    private String departement ;
    @Column(length = 100)
    private String zone ;
    @Column(name = "score_global")
    private Double scoreGlobal;
    @Column(name = "taux_conformite")
    private Double tauxConformite;

    @Column(columnDefinition = "TEXT")
    private String observations;
    @Column(columnDefinition = "TEXT")
    private String recommandations;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "date_modification")
    private LocalDateTime dateModification = LocalDateTime.now();

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_template_id")
    private CheckList checklistTemplate;

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReponseAUdit> reponses = new ArrayList<>();

    @OneToMany(mappedBy = "audit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NonComformite> nonConformites = new ArrayList<>();


    @Column(columnDefinition = "TEXT")
    private String recommendations;

    public void calculerScore() {
        if (reponses.isEmpty()) {
            this.scoreGlobal = 0.0;
            return;
        }
        long conformes = reponses.stream()
                .filter(ReponseAUdit::getConforme)
                .count();
        this.scoreGlobal = (conformes * 100.0) / reponses.size();
    }

    public void calculerTauxConformite() {
        calculerScore();
        this.tauxConformite = this.scoreGlobal;
    }

    public void terminer() {
        this.statut = StatutAudit.TERMINE;
        this.dateFin = LocalDateTime.now();
        calculerScore();
    }

    public void valider() {
        if (this.statut == StatutAudit.TERMINE) {
            this.statut = StatutAudit.VALIDE;
        }
    }





}
