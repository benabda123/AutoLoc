package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "agence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAgence;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String ville;

    private String adresse;
    private String telephone;
    @OneToMany(mappedBy = "agence")
    private Set<Employe> employes;

    @OneToMany(mappedBy = "agence")
    private Set<Vehicule> vehicules;
}