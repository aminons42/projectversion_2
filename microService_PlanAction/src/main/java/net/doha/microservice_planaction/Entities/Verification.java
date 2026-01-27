package net.doha.microservice_planaction.Entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime dateVerification = LocalDateTime.now();

    @Column(name = "verificateur_id", nullable = false)
    private Long verificateurId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultatVerification resultat;

    @Column(columnDefinition = "TEXT")
    private String commentaire;

    @Column(nullable = false)
    private Boolean efficace = false;

    private LocalDateTime dateProchaineSurveillance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id", nullable = false)
    private Action action;

    public void valider() {
        this.resultat = ResultatVerification.CONFORME;
        this.efficace = true;
    }

    public void rejeter() {
        this.resultat = ResultatVerification.NON_CONFORME;
        this.efficace = false;
    }

}
