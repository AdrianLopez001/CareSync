package com.caresync.appointment.messaging;

import java.time.LocalDateTime;

public record AppointmentScheduledEvent(
        Long appointmentId,
        String patientName,
        String patientEmail,
        String doctorName,
        LocalDateTime scheduledAt
) {}
