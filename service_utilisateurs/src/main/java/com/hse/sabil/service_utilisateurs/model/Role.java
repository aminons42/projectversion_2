package com.hse.sabil.service_utilisateurs.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nom;




    public Role(String nom) {
        this.nom = nom;
    }


}
