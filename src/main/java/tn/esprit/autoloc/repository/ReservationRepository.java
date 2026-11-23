package tn.esprit.autoloc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Détecte les réservations actives (EN_ATTENTE ou CONFIRMEE) sur un véhicule
     * qui chevauchent la période [dateDebut, dateFin].
     *
     * Chevauchement : la réservation existante commence avant dateFin
     *                 ET se termine après dateDebut.
     */
    @Query("""
            SELECT r FROM Reservation r
            WHERE r.vehicule.idVehicule = :idVehicule
              AND r.statut IN :statuts
              AND r.dateDebut < :dateFin
              AND r.dateFin  > :dateDebut
            """)
    List<Reservation> findActiveOverlappingReservations(
            @Param("idVehicule") Long idVehicule,
            @Param("statuts")    List<StatutReservation> statuts,
            @Param("dateDebut")  LocalDate dateDebut,
            @Param("dateFin")    LocalDate dateFin
    );
}
