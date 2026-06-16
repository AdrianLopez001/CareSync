package com.caresync.notification.listener;

import com.caresync.notification.config.RabbitMQConfig;
import com.caresync.notification.dto.AppointmentScheduledEvent;
import com.caresync.notification.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventListener {

    private static final Logger log = LoggerFactory.getLogger(AppointmentEventListener.class);

    private final EmailNotificationService emailService;

    public AppointmentEventListener(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void onAppointmentScheduled(AppointmentScheduledEvent event) {
        log.info("Received AppointmentScheduledEvent for appointment #{}", event.appointmentId());
        emailService.sendConfirmation(event);
    }
}
