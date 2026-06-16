package com.caresync.appointment.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AppointmentRequest(
        @NotBlank(message = "Patient name is required")
        String patientName,

        @NotBlank(message = "Patient email is required")
        @Email(message = "Invalid email format")
        String patientEmail,

        @NotBlank(message = "Doctor name is required")
        String doctorName,

        @NotNull(message = "Scheduled date/time is required")
        @Future(message = "Appointment must be scheduled in the future")
        LocalDateTime scheduledAt
) {}
