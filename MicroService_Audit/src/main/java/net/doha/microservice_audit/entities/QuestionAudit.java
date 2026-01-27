package net.doha.microservice_audit.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Question_id")
    private Long id ;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String libelle;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TypeReponse typeReponse = TypeReponse.OUI_NON;

    @Column(name = "exigence_reglementaire", columnDefinition = "TEXT")
    private String exigenceReglementaire;

    @Column(columnDefinition = "TEXT")
    private String critere;

    private Integer ponderation = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NiveauCriticite criticite = NiveauCriticite.MOYENNE;

    @Column(nullable = false)
    private Integer ordre = 0;

    @Column(nullable = false)
    private Boolean obligatoire = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="categorie_id")
    private CheckList checklist;

    private String categorie;
    @OneToMany(mappedBy = "question")
    private List<ReponseAUdit> reponses = new ArrayList<>();

    public boolean validerReponse(String valeur) {
        if (typeReponse == TypeReponse.OUI_NON) {
            return "OUI".equalsIgnoreCase(valeur) || "NON".equalsIgnoreCase(valeur);
        }
        if (typeReponse == TypeReponse.CONFORME_NON_CONFORME) {
            return "CONFORME".equalsIgnoreCase(valeur) || "NON_CONFORME".equalsIgnoreCase(valeur);
        }
        return true;
    }


}
