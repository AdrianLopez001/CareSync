package com.caresync.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE = "appointment.scheduled.queue";
    public static final String EXCHANGE = "caresync.exchange";
    public static final String ROUTING_KEY = "appointment.scheduled";

    @Bean
    public Queue appointmentQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public TopicExchange caresyncExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding binding(Queue appointmentQueue, TopicExchange caresyncExchange) {
        return BindingBuilder.bind(appointmentQueue).to(caresyncExchange).with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
