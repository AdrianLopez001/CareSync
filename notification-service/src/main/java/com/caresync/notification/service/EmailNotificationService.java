package com.caresync.notification.service;

import com.caresync.notification.dto.AppointmentScheduledEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;

    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendConfirmation(AppointmentScheduledEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.patientEmail());
        message.setSubject("Appointment Confirmation - CareSync");
        message.setText(buildEmailBody(event));

        mailSender.send(message);
        log.info("Confirmation email sent to {} for appointment #{}", event.patientEmail(), event.appointmentId());
    }

    private String buildEmailBody(AppointmentScheduledEvent event) {
        return String.format("""
                Hello, %s!

                Your appointment has been successfully scheduled.

                Doctor: %s
                Date & Time: %s

                If you need to cancel or reschedule, please contact us.

                Best regards,
                CareSync Team
                """,
                event.patientName(),
                event.doctorName(),
                event.scheduledAt().format(FORMATTER)
        );
    }
}
