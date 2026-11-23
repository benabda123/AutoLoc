package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Client;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Recherche les clients par nom et prénom (insensible à la casse).
     * Retourne une liste : vide = 404, taille > 1 = 409 (homonymes).
     */
    List<Client> findByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);
}
