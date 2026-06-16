package com.caresync.appointment.messaging;

import com.caresync.appointment.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Forwards the Spring application event to RabbitMQ only after the DB transaction commits.
 * This prevents phantom notifications when a transaction rolls back after publishing.
 */
@Component
public class AppointmentTransactionalPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AppointmentTransactionalPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onAppointmentScheduled(AppointmentScheduledEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROUTING_KEY, event);
    }
}
