package com.caresync.appointment.messaging;

import com.caresync.appointment.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class AppointmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public AppointmentEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishAppointmentScheduled(AppointmentScheduledEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.ROUTING_KEY,
                event
        );
    }
}
