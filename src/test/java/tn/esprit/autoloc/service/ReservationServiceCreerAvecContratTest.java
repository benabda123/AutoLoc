package tn.esprit.autoloc.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.ClientRepository;
import tn.esprit.autoloc.repository.ContratRepository;
import tn.esprit.autoloc.repository.ReservationRepository;
import tn.esprit.autoloc.repository.VehiculeRepository;
import tn.esprit.autoloc.service.imp.ReservationServiceImpl;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
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
@DisplayName("Service 2 — creerReservationEtContratAssocie")
class ReservationServiceCreerAvecContratTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ClientRepository      clientRepository;
    @Mock private VehiculeRepository    vehiculeRepository;
    @Mock private ContratRepository     contratRepository;
    @Mock private ReservationMapper     reservationMapper;

    @InjectMocks
    private ReservationServiceImpl service;

    // --- Fixtures ---
    private Client    client;
    private Vehicule  vehicule;
    private ReservationWithContratRequestDTO validRequest;

    private static final LocalDate DATE_DEBUT     = LocalDate.now().plusDays(3);
    private static final LocalDate DATE_FIN       = LocalDate.now().plusDays(10); // 7 jours
    private static final LocalDate DATE_SIGNATURE = LocalDate.now().plusDays(1);
    private static final BigDecimal TARIF          = new BigDecimal("50.00");
    // montant attendu : 50.00 × 7 = 350.00
    private static final BigDecimal MONTANT_ATTENDU = new BigDecimal("350.00");

    @BeforeEach
    void setUp() {
        client = new Client();
        client.setIdClient(1L);
        client.setNom("Ben Ali");
        client.setPrenom("Sami");

        vehicule = new Vehicule();
        vehicule.setIdVehicule(10L);
        vehicule.setImmatriculation("TN-456-XYZ");
        vehicule.setTarifJournalier(TARIF);
        vehicule.setStatut(StatutVehicule.DISPONIBLE);

        validRequest = new ReservationWithContratRequestDTO();
        validRequest.setIdClient(1L);
        validRequest.setIdVehicule(10L);
        validRequest.setDateDebut(DATE_DEBUT);
        validRequest.setDateFin(DATE_FIN);
        validRequest.setDateSignature(DATE_SIGNATURE);
    }

    // -------------------------------------------------------------------------
    // CAS NOMINAL
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Nominal — création réservation + contrat, montant calculé correctement")
    void creer_nominal_retourneDTOAvecContrat() {
        // Arrange
        Reservation savedReservation = new Reservation();
        savedReservation.setIdReservation(20L);
        savedReservation.setDateDebut(DATE_DEBUT);
        savedReservation.setDateFin(DATE_FIN);
        savedReservation.setStatut(StatutReservation.CONFIRMEE);
        savedReservation.setClient(client);
        savedReservation.setVehicule(vehicule);

        Contrat savedContrat = new Contrat();
        savedContrat.setIdContrat(30L);
        savedContrat.setMontantTotal(MONTANT_ATTENDU);
        savedContrat.setValide(true);

        ContratDTO contratDTO = new ContratDTO();
        contratDTO.setIdContrat(30L);
        contratDTO.setMontantTotal(MONTANT_ATTENDU);

        ReservationResponseDTO expectedDto = new ReservationResponseDTO();
        expectedDto.setIdReservation(20L);
        expectedDto.setStatut("CONFIRMEE");
        expectedDto.setContrat(contratDTO);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findActiveOverlappingReservations(anyLong(), anyList(), any(), any()))
                .thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(contratRepository.save(any(Contrat.class))).thenReturn(savedContrat);
        when(reservationMapper.toResponseDto(any(Reservation.class))).thenReturn(expectedDto);

        // Act
        ReservationResponseDTO result = service.creerReservationEtContratAssocie(validRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIdReservation()).isEqualTo(20L);
        assertThat(result.getStatut()).isEqualTo("CONFIRMEE");
        assertThat(result.getContrat()).isNotNull();
        assertThat(result.getContrat().getMontantTotal()).isEqualByComparingTo(MONTANT_ATTENDU);
    }

    @Test
    @DisplayName("Nominal — vérification précise du calcul montantTotal (tarifJournalier × nbJours)")
    void creer_nominal_calculMontantTotal_exact() {
        // Arrange
        Reservation savedReservation = new Reservation();
        savedReservation.setIdReservation(20L);
        savedReservation.setDateDebut(DATE_DEBUT);
        savedReservation.setDateFin(DATE_FIN);
        savedReservation.setStatut(StatutReservation.CONFIRMEE);
        savedReservation.setClient(client);
        savedReservation.setVehicule(vehicule);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findActiveOverlappingReservations(anyLong(), anyList(), any(), any()))
                .thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationMapper.toResponseDto(any())).thenReturn(new ReservationResponseDTO());

        // Act
        service.creerReservationEtContratAssocie(validRequest);

        // Assert : capturer le Contrat passé à save() et vérifier son montantTotal
        ArgumentCaptor<Contrat> contratCaptor = ArgumentCaptor.forClass(Contrat.class);
        verify(contratRepository).save(contratCaptor.capture());
        Contrat contratSave = contratCaptor.getValue();

        // tarif=50.00, dateDebut+3 → dateFin+10 : 7 jours → 50 × 7 = 350.00
        assertThat(contratSave.getMontantTotal()).isEqualByComparingTo(MONTANT_ATTENDU);
        assertThat(contratSave.getValide()).isTrue();
        assertThat(contratSave.getDateSignature()).isEqualTo(DATE_SIGNATURE);
    }

    @Test
    @DisplayName("Nominal — statut de la réservation créée est CONFIRMEE")
    void creer_nominal_statutReservationEstConfirmee() {
        Reservation savedReservation = new Reservation();
        savedReservation.setIdReservation(20L);
        savedReservation.setStatut(StatutReservation.CONFIRMEE);
        savedReservation.setClient(client);
        savedReservation.setVehicule(vehicule);
        savedReservation.setDateDebut(DATE_DEBUT);
        savedReservation.setDateFin(DATE_FIN);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findActiveOverlappingReservations(anyLong(), anyList(), any(), any()))
                .thenReturn(List.of());
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);
        when(contratRepository.save(any(Contrat.class))).thenAnswer(inv -> inv.getArgument(0));
        when(reservationMapper.toResponseDto(any())).thenReturn(new ReservationResponseDTO());

        service.creerReservationEtContratAssocie(validRequest);

        ArgumentCaptor<Reservation> reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationCaptor.capture());
        assertThat(reservationCaptor.getValue().getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Dates incohérentes
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 400 — dateFin <= dateDebut")
    void creer_dateFinAvantDateDebut_lanceBusinessRuleException() {
        validRequest.setDateFin(DATE_DEBUT); // égal → invalide

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("fin");

        verifyNoInteractions(clientRepository, vehiculeRepository, reservationRepository);
    }

    @Test
    @DisplayName("Erreur 400 — dateSignature postérieure à dateDebut")
    void creer_dateSignatureApresDateDebut_lanceBusinessRuleException() {
        validRequest.setDateSignature(DATE_DEBUT.plusDays(1)); // après dateDebut

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("signature");

        verifyNoInteractions(clientRepository, vehiculeRepository, reservationRepository);
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Client introuvable
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 404 — client introuvable")
    void creer_clientIntrouvable_lanceResourceNotFoundException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("1");

        verifyNoInteractions(vehiculeRepository, reservationRepository);
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Véhicule introuvable
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 404 — véhicule introuvable")
    void creer_vehiculeIntrouvable_lanceResourceNotFoundException() {
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("10");

        verifyNoInteractions(reservationRepository);
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Véhicule indisponible
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 409 — véhicule en statut LOUE")
    void creer_vehiculeLoue_lanceBusinessRuleException() {
        vehicule.setStatut(StatutVehicule.LOUE);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("LOUE");

        verifyNoInteractions(reservationRepository);
    }

    @Test
    @DisplayName("Erreur 409 — véhicule en statut MAINTENANCE")
    void creer_vehiculeMaintenance_lanceBusinessRuleException() {
        vehicule.setStatut(StatutVehicule.MAINTENANCE);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("MAINTENANCE");
    }

    // -------------------------------------------------------------------------
    // CAS D'ERREUR — Chevauchement de réservation
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Erreur 409 — chevauchement avec une réservation active existante")
    void creer_chevauchementExistant_lanceBusinessRuleException() {
        Reservation existante = new Reservation();
        existante.setIdReservation(99L);
        existante.setStatut(StatutReservation.CONFIRMEE);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findActiveOverlappingReservations(anyLong(), anyList(), any(), any()))
                .thenReturn(List.of(existante));

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContainingAll("TN-456-XYZ", "1");

        verify(reservationRepository, never()).save(any());
        verifyNoInteractions(contratRepository);
    }

    @Test
    @DisplayName("Erreur 409 — multiple chevauchements : message indique le nombre")
    void creer_multipleChevauchements_messageContientNombre() {
        Reservation r1 = new Reservation();
        Reservation r2 = new Reservation();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(10L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.findActiveOverlappingReservations(anyLong(), anyList(), any(), any()))
                .thenReturn(List.of(r1, r2));

        assertThatThrownBy(() -> service.creerReservationEtContratAssocie(validRequest))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("2");
    }
}
