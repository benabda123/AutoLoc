package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.imp.ReservationServiceImpl;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.exception.BusinessRuleException;
import tn.esprit.autoloc.web.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Service 1 — affecterReservationAClient")
class ReservationServiceAffecterClientTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ClientRepository      clientRepository;
    @Mock private VehiculeRepository    vehiculeRepository;
    @Mock private ContratRepository     contratRepository;
    @Mock private ReservationMapper     reservationMapper;

    @InjectMocks
    private ReservationServiceImpl service;

    // --- Fixtures ---
    private Reservation reservation;
    private Client      client;

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setIdClient(1L);
        client.setNom("Dupont");
        client.setPrenom("Marie");

        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(10L);
        vehicule.setImmatriculation("TN-123-ABC");

        reservation = new Reservation();
        reservation.setIdReservation(5L);
        reservation.setDateDebut(LocalDate.now().plusDays(2));
        reservation.setDateFin(LocalDate.now().plusDays(5));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setVehicule(vehicule);
    }

    // -------------------------------------------------------------------------
    // CAS NOMINAL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Nominal — affecter un client unique à une réservation EN_ATTENTE")
    void affecterClient_nominalEnAttente_retourneDTO() {
        // Arrange
        ReservationResponseDTO expectedDto = new ReservationResponseDTO();
        expectedDto.setIdReservation(5L);
        expectedDto.setNomClient("Dupont");
        expectedDto.setPrenomClient("Marie");

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomIgnoreCaseAndPrenomIgnoreCase("Dupont", "Marie"))
                .thenReturn(List.of(client));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(reservationMapper.toResponseDto(reservation)).thenReturn(expectedDto);

        // Act
        ReservationResponseDTO result = service.affecterReservationAClient(5L, "Dupont", "Marie");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getNomClient()).isEqualTo("Dupont");
        assertThat(result.getPrenomClient()).isEqualTo("Marie");
        assertThat(result.getIdReservation()).isEqualTo(5L);

        verify(reservationRepository).save(argThat(r -> r.getClient() == client));
        verify(reservationMapper).toResponseDto(reservation);
    }

    @Test
    @DisplayName("Nominal — affecter un client à une réservation CONFIRMEE")
    void affecterClient_nominalConfirmee_retourneDTO() {
        reservation.setStatut(StatutReservation.CONFIRMEE);

        ReservationResponseDTO dto = new ReservationResponseDTO();
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomIgnoreCaseAndPrenomIgnoreCase("Dupont", "Marie"))
                .thenReturn(List.of(client));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(reservationMapper.toResponseDto(any())).thenReturn(dto);

        assertThatNoException().isThrownBy(() ->
                service.affecterReservationAClient(5L, "Dupont", "Marie"));
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Réservation introuvable
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 404 — réservation introuvable")
    void affecterClient_reservationIntrouvable_lanceResourceNotFoundException() {
        when(reservationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.affecterReservationAClient(99L, "Dupont", "Marie"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verifyNoInteractions(clientRepository);
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Statut terminal
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 409 — réservation ANNULEE")
    void affecterClient_reservationAnnulee_lanceBusinessRuleException() {
        reservation.setStatut(StatutReservation.ANNULEE);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.affecterReservationAClient(5L, "Dupont", "Marie"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("ANNULEE");

        verifyNoInteractions(clientRepository);
    }

    @Test
    @DisplayName("Erreur 409 — réservation TERMINEE")
    void affecterClient_reservationTerminee_lanceBusinessRuleException() {
        reservation.setStatut(StatutReservation.TERMINEE);
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> service.affecterReservationAClient(5L, "Dupont", "Marie"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("TERMINEE");
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Client introuvable
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 404 — aucun client avec ce nom+prénom")
    void affecterClient_clientIntrouvable_lanceResourceNotFoundException() {
        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomIgnoreCaseAndPrenomIgnoreCase("Dupont", "Marie"))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.affecterReservationAClient(5L, "Dupont", "Marie"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContainingAll("Dupont", "Marie");

        verify(reservationRepository, never()).save(any());
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Homonymes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 409 — plusieurs clients homonymes")
    void affecterClient_plusieursHomonymes_lanceBusinessRuleException() {
        Client homonyme = new Client();
        homonyme.setIdClient(2L);
        homonyme.setNom("Dupont");
        homonyme.setPrenom("Marie");

        when(reservationRepository.findById(5L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findByNomIgnoreCaseAndPrenomIgnoreCase("Dupont", "Marie"))
                .thenReturn(List.of(client, homonyme));

        assertThatThrownBy(() -> service.affecterReservationAClient(5L, "Dupont", "Marie"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("2");

        verify(reservationRepository, never()).save(any());
    }
}
