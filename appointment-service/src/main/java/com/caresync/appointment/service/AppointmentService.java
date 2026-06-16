package com.caresync.appointment.service;

import com.caresync.appointment.dto.AppointmentRequest;
import com.caresync.appointment.dto.AppointmentResponse;
import com.caresync.appointment.entity.Appointment;
import com.caresync.appointment.exception.AppointmentNotFoundException;
import com.caresync.appointment.messaging.AppointmentScheduledEvent;
import com.caresync.appointment.repository.AppointmentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public AppointmentService(AppointmentRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public AppointmentResponse scheduleAppointment(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setPatientName(request.patientName());
        appointment.setPatientEmail(request.patientEmail());
        appointment.setDoctorName(request.doctorName());
        appointment.setScheduledAt(request.scheduledAt());

        Appointment saved = repository.save(appointment);

        // Published as a Spring event; forwarded to RabbitMQ only after DB commit
        // via AppointmentTransactionalPublisher (@TransactionalEventListener AFTER_COMMIT)
        eventPublisher.publishEvent(new AppointmentScheduledEvent(
                saved.getId(),
                saved.getPatientName(),
                saved.getPatientEmail(),
                saved.getDoctorName(),
                saved.getScheduledAt()
        ));

        return toResponse(saved);
    }

    public List<AppointmentResponse> listAppointmentsByEmail(String email) {
        return repository.findByPatientEmail(email).stream()
                .map(this::toResponse)
                .toList();
    }

    public AppointmentResponse getAppointment(Long id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatientName(),
                a.getPatientEmail(),
                a.getDoctorName(),
                a.getScheduledAt(),
                a.getStatus(),
                a.getCreatedAt()
        );
    }
}
