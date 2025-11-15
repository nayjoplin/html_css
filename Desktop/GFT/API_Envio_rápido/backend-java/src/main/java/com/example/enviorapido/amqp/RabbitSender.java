package com.example.enviorapido.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.example.enviorapido.model.Envio;

@Component
public class RabbitSender {
    private final RabbitTemplate rabbitTemplate;

    public RabbitSender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarEnvio(Envio envio) {
        try {
            rabbitTemplate.convertAndSend("", "fila_calculo_frete", envio);
        } catch (Exception e) {
            // falha no envio não impede fluxo
        }
    }
}
