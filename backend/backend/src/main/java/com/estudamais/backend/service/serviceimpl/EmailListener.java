package com.estudamais.backend.service.serviceimpl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    @RabbitListener(queues = "${rabbitmq.queue.welcome}")
    public void handleWelcomeEmail(String payload) {
        String[] data = payload.split(";");
        String name = data[0];
        String email = data[1];
        System.out.println("--------------------------------------------------");
        System.out.println("DISPARANDO EMAIL DE BOAS-VINDAS ASSÍNCRONO VIA RABBITMQ");
        System.out.println("Para: " + email);
        System.out.println("Assunto: Bem-vindo ao EstudaMais, " + name + "!");
        System.out.println("Corpo: Seu ecossistema de gestão de estudos está pronto. Bons estudos!");
        System.out.println("--------------------------------------------------");
    }
}