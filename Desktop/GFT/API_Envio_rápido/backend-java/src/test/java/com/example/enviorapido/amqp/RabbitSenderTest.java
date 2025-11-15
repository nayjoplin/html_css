package com.example.enviorapido.amqp;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.example.enviorapido.model.Envio;

class RabbitSenderTest {

    @Test
    void enviarEnvio_callsConvertAndSend() {
        final boolean[] called = {false};
        final Object[] captured = new Object[3];
        RabbitTemplate template = new RabbitTemplate() {
            @Override
            public void convertAndSend(String exchange, String routingKey, Object message) {
                called[0] = true;
                captured[0] = exchange;
                captured[1] = routingKey;
                captured[2] = message;
            }
        };

        RabbitSender sender = new RabbitSender(template);
        Envio e = new Envio();
        sender.enviarEnvio(e);

        assertTrue(called[0]);
        org.junit.jupiter.api.Assertions.assertEquals("", captured[0]);
        org.junit.jupiter.api.Assertions.assertEquals("fila_calculo_frete", captured[1]);
        org.junit.jupiter.api.Assertions.assertSame(e, captured[2]);
    }
}
