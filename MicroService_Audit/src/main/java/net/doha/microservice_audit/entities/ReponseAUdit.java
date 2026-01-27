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
public class ReponseAUdit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reponse_id")
    private Long id;
    @Column(nullable = false, length = 500)
    private String valeur;

    @Column(nullable = false)
    private Boolean conforme = true;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(columnDefinition = "TEXT")
    private String observation;

    @Column(name = "action_requise", nullable = false)
    private Boolean actionRequise = false;

    @Column(name = "date_reponse", nullable = false)
    private LocalDateTime dateReponse = LocalDateTime.now();

    // Référence vers l'utilisateur qui a répondu
    @Column(name = "utilisateur_id", nullable = false)
    private Long utilisateurId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_id", nullable = false)
    private Audit audit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionAudit question;

    @OneToOne(mappedBy = "reponseAudit", cascade = CascadeType.ALL, orphanRemoval = true)
    private NonComformite nonConformite;



    public void marquerNonConforme() {
        this.conforme = false;
        this.actionRequise = true;
    }

}
