package com.caresync.appointment.dto;

import com.caresync.appointment.entity.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        String patientName,
        String patientEmail,
        String doctorName,
        LocalDateTime scheduledAt,
        AppointmentStatus status,
        LocalDateTime createdAt
) {}
