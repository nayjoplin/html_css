package com.example.enviorapido.amqp;

import com.example.enviorapido.model.Envio;
import com.example.enviorapido.repository.EnvioRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitReceiver {

    private final EnvioRepository repo;

    public RabbitReceiver(EnvioRepository repo) {
        this.repo = repo;
    }

    @RabbitListener(queues = "envios")
    public void receive(Envio envio) {
        try {
            if (envio != null) repo.save(envio);
        } catch (Exception ignored) {
            // se houver erro ao processar mensagem, ignorar para não travar o consumidor
        }
    }
}
