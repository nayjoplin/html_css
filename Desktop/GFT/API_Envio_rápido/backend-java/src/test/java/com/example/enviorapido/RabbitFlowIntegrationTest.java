package com.example.enviorapido;

import com.example.enviorapido.model.Envio;
import com.example.enviorapido.repository.EnvioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class RabbitFlowIntegrationTest {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3-management");

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongo.getReplicaSetUrl());
        registry.add("spring.rabbitmq.addresses", () -> rabbit.getAmqpUrl());
    }

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    EnvioRepository repo;

    @Test
    void publishAndConsume_shouldPersistEnvio() throws Exception {
        Envio envio = new Envio();
        Envio.Remetente r = new Envio.Remetente();
        r.nome = "Teste Rabbit";
        r.cep = "01001000";
        envio.remetente = r;

        Envio.Destinatario d = new Envio.Destinatario();
        d.nome = "Destino Teste";
        d.cep = "87020025";
        envio.destinatario = d;

        Envio.Pacote p = new Envio.Pacote();
        p.peso_kg = 1.0;
        envio.pacote = p;

        // publicar diretamente na fila que o consumidor escuta
        rabbitTemplate.convertAndSend("", "envios", envio);

        boolean found = false;
        // aguardar por até 10s verificando se o consumidor persistiu o documento
        for (int i = 0; i < 20; i++) {
            List<Envio> all = repo.findAll();
            found = all.stream().anyMatch(e -> e.remetente != null && "Teste Rabbit".equals(e.remetente.nome));
            if (found) break;
            Thread.sleep(500);
        }

        assertTrue(found, "Envio publicado na fila deveria ser persistido pelo consumidor");
    }
}
