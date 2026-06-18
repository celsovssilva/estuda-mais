package com.estudamais.backend.config;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.welcome}")
    private String welcomeQueue;

    @Value("${rabbitmq.exchange.internal}")
    private String internalExchange;

    @Value("${rabbitmq.routing-key.welcome}")
    private String welcomeRoutingKey;

    @Bean
    public Queue welcomeQueue() {
        return new Queue(welcomeQueue, true);
    }

    @Bean
    public TopicExchange internalExchange() {
        return new TopicExchange(internalExchange);
    }

    @Bean
    public Binding welcomeBinding() {
        return BindingBuilder
                .bind(welcomeQueue())
                .to(internalExchange())
                .with(welcomeRoutingKey);
    }
}
