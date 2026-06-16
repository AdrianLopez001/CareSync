package com.caresync.notification.dto;

import java.time.LocalDateTime;

public record AppointmentScheduledEvent(
        Long appointmentId,
        String patientName,
        String patientEmail,
        String doctorName,
        LocalDateTime scheduledAt
) {}
