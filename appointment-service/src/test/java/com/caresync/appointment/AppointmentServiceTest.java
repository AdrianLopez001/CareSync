package com.caresync.appointment;

import com.caresync.appointment.dto.AppointmentRequest;
import com.caresync.appointment.dto.AppointmentResponse;
import com.caresync.appointment.entity.Appointment;
import com.caresync.appointment.entity.AppointmentStatus;
import com.caresync.appointment.exception.AppointmentNotFoundException;
import com.caresync.appointment.repository.AppointmentRepository;
import com.caresync.appointment.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock private AppointmentRepository repository;
    @Mock private ApplicationEventPublisher eventPublisher;

    private AppointmentService service;

    @BeforeEach
    void setUp() {
        service = new AppointmentService(repository, eventPublisher);
    }

    @Test
    void shouldScheduleAppointmentAndPublishEvent() {
        AppointmentRequest request = new AppointmentRequest(
                "John Doe", "john@example.com", "Dr. Smith",
                LocalDateTime.now().plusDays(1)
        );

        Appointment saved = buildAppointment(1L, request);
        when(repository.save(any())).thenReturn(saved);

        AppointmentResponse response = service.scheduleAppointment(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.status()).isEqualTo(AppointmentStatus.SCHEDULED);
        verify(eventPublisher).publishEvent(any());
    }

    @Test
    void shouldThrowWhenAppointmentNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAppointment(99L))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void shouldListAppointmentsByEmail() {
        AppointmentRequest req = new AppointmentRequest(
                "Jane Doe", "jane@example.com", "Dr. Jones",
                LocalDateTime.now().plusDays(2)
        );
        when(repository.findByPatientEmail("jane@example.com"))
                .thenReturn(List.of(buildAppointment(2L, req)));

        List<AppointmentResponse> results = service.listAppointmentsByEmail("jane@example.com");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).patientEmail()).isEqualTo("jane@example.com");
    }

    private Appointment buildAppointment(Long id, AppointmentRequest req) {
        Appointment a = new Appointment();
        a.setPatientName(req.patientName());
        a.setPatientEmail(req.patientEmail());
        a.setDoctorName(req.doctorName());
        a.setScheduledAt(req.scheduledAt());
        a.setStatus(AppointmentStatus.SCHEDULED);
        try {
            var idField = Appointment.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(a, id);
            var createdField = Appointment.class.getDeclaredField("createdAt");
            createdField.setAccessible(true);
            createdField.set(a, LocalDateTime.now());
        } catch (Exception ignored) {}
        return a;
    }
}
