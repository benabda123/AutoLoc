package tn.esprit.autoloc.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.exception.BusinessRuleException;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final ClientRepository      clientRepository;
    private final VehiculeRepository    vehiculeRepository;
    private final ContratRepository     contratRepository;
    private final ReservationMapper     reservationMapper;

    // -------------------------------------------------------------------------
    // CRUD existant (conservé intact)
    // -------------------------------------------------------------------------

    @Override
    public List<Reservation> retrieveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation addReservation(Reservation r) {
        return reservationRepository.save(r);
    }

    @Override
    public Reservation updateReservation(Reservation r) {
        return reservationRepository.save(r);
    }

    @Override
    public Reservation retrieveReservation(Long idReservation) {
        return reservationRepository.findById(idReservation).orElse(null);
    }

    @Override
    public void removeReservation(Long idReservation) {
        reservationRepository.deleteById(idReservation);
    }

    // -------------------------------------------------------------------------
    // Service 1 : affecterReservationAClient
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {

        // 1. Charger la réservation (404 si introuvable)
        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation", idReservation));

        // 2. Interdire l'affectation sur une réservation terminale
        if (reservation.getStatut() == StatutReservation.ANNULEE
                || reservation.getStatut() == StatutReservation.TERMINEE) {
            throw new BusinessRuleException(
                    "Impossible d'affecter un client à une réservation dont le statut est : "
                            + reservation.getStatut(),
                    HttpStatus.CONFLICT);
        }

        // 3. Rechercher le client par nom + prénom (insensible à la casse)
        List<Client> clients = clientRepository.findByNomIgnoreCaseAndPrenomIgnoreCase(nom, prenom);

        if (clients.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Aucun client trouvé avec le nom « " + nom + " » et le prénom « " + prenom + " »");
        }
        if (clients.size() > 1) {
            throw new BusinessRuleException(
                    "Plusieurs clients (" + clients.size() + ") partagent le nom « " + nom
                            + " » et le prénom « " + prenom
                            + " ». Veuillez utiliser l'identifiant unique du client.",
                    HttpStatus.CONFLICT);
        }

        // 4. Associer et sauvegarder
        reservation.setClient(clients.get(0));
        Reservation saved = reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(saved);
    }

    // -------------------------------------------------------------------------
    // Service 2 : creerReservationEtContratAssocie
    // -------------------------------------------------------------------------

    @Override
    @Transactional
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {

        // 1. Validation des cohérences de dates
        if (!requestDTO.getDateFin().isAfter(requestDTO.getDateDebut())) {
            throw new BusinessRuleException(
                    "La date de fin doit être strictement postérieure à la date de début.",
                    HttpStatus.BAD_REQUEST);
        }
        if (requestDTO.getDateSignature().isAfter(requestDTO.getDateDebut())) {
            throw new BusinessRuleException(
                    "La date de signature du contrat ne peut pas être postérieure à la date de début de réservation.",
                    HttpStatus.BAD_REQUEST);
        }

        // 2. Charger le client (404 si introuvable)
        Client client = clientRepository.findById(requestDTO.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client", requestDTO.getIdClient()));

        // 3. Charger le véhicule (404 si introuvable)
        Vehicule vehicule = vehiculeRepository.findById(requestDTO.getIdVehicule())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule", requestDTO.getIdVehicule()));

        // 4. Vérifier que le véhicule est DISPONIBLE
        if (vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
            throw new BusinessRuleException(
                    "Le véhicule « " + vehicule.getImmatriculation()
                            + " » n'est pas disponible (statut actuel : " + vehicule.getStatut() + ").",
                    HttpStatus.CONFLICT);
        }

        // 5. Vérifier l'absence de chevauchement sur la période demandée
        List<StatutReservation> statutsActifs = List.of(
                StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE);

        List<Reservation> chevauchements = reservationRepository.findActiveOverlappingReservations(
                vehicule.getIdVehicule(),
                statutsActifs,
                requestDTO.getDateDebut(),
                requestDTO.getDateFin());

        if (!chevauchements.isEmpty()) {
            throw new BusinessRuleException(
                    "Le véhicule « " + vehicule.getImmatriculation()
                            + " » possède déjà " + chevauchements.size()
                            + " réservation(s) active(s) qui chevauchent la période demandée ["
                            + requestDTO.getDateDebut() + " → " + requestDTO.getDateFin() + "].",
                    HttpStatus.CONFLICT);
        }

        // 6. Créer la Reservation (statut CONFIRMEE — contrat créé dans la même transaction)
        Reservation reservation = new Reservation();
        reservation.setDateDebut(requestDTO.getDateDebut());
        reservation.setDateFin(requestDTO.getDateFin());
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 7. Calculer le montant total : tarifJournalier × nombre de jours
        long nbJours = ChronoUnit.DAYS.between(requestDTO.getDateDebut(), requestDTO.getDateFin());
        BigDecimal montantTotal = vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(nbJours));

        // 8. Créer le Contrat et maintenir la cohérence bidirectionnelle OneToOne
        Contrat contrat = new Contrat();
        contrat.setDateSignature(requestDTO.getDateSignature());
        contrat.setMontantTotal(montantTotal);
        contrat.setValide(true);
        contrat.setReservation(savedReservation);   // propriétaire (FK id_reservation)
        Contrat savedContrat = contratRepository.save(contrat);

        savedReservation.setContrat(savedContrat);  // côté inverse (cohérence en mémoire)

        // 9. Retourner le DTO complet
        return reservationMapper.toResponseDto(savedReservation);
    }
}
