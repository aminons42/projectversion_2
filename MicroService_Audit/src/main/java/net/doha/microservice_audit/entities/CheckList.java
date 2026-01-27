package net.doha.microservice_audit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
public class CheckList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    @Column(nullable = false, length = 200)
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeAudit typeAudit;

    @Column(nullable = false)
    private Boolean actif = true;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    @Column(name = "createur_id")
    private Long createurId;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL)
    private List<QuestionAudit> questions=new ArrayList<>();

    @OneToMany
    private List<Audit> audits=new ArrayList<>();

    public CheckList cloner() {
        CheckList clone = new CheckList();
        clone.setNom(this.nom + " - Copie");
        clone.setDescription(this.description);
        clone.setTypeAudit(this.typeAudit);
        clone.setActif(true);
        clone.setVersion(this.version + 1);
        clone.setCreateurId(this.createurId);
        return clone;
    }

    public void activer() {
        this.actif = true;
    }

    public void desactiver() {
        this.actif = false;
    }

    public void ajouterQuestion(QuestionAudit question) {
        questions.add(question);
        question.setChecklist(this);
    }
}

