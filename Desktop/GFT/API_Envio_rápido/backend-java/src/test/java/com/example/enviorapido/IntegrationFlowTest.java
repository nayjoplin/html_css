package com.example.enviorapido;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import com.example.enviorapido.repository.EnvioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationFlowTest {

    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
    static RabbitMQContainer rabbit = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.11-management"));

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", () -> mongo.getReplicaSetUrl());
        r.add("spring.rabbitmq.addresses", () -> rabbit.getAmqpUrl());
    }

    @BeforeAll
    static void startContainers() {
        mongo.start();
        rabbit.start();
    }

    @AfterAll
    static void stopContainers() {
        try {
            rabbit.stop();
        } catch (Exception ignored) {}
        try {
            mongo.stop();
        } catch (Exception ignored) {}
    }

    @LocalServerPort
    int port;

    @Autowired
    private EnvioRepository repo;

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void fluxoCompletoPersistenciaPublicacao() throws Exception {
        // montar request
        Map<String,Object> body = Map.of(
                "remetente", Map.of("nome","Empresa","cep","01001000"),
                "destinatario", Map.of("nome","Cliente","cep","87020025"),
                "pacote", Map.of("peso_kg", 2.0)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> req = new HttpEntity<>(mapper.writeValueAsString(body), headers);

        ResponseEntity<Map> resp = rest.postForEntity("http://localhost:" + port + "/api/envios", req, Map.class);
        assertThat(resp.getStatusCode().value()).isEqualTo(201);

        Map<String,Object> respBody = resp.getBody();
        assertThat(respBody).containsKeys("cepOrigem","cepDestino","valorFrete","prazoEntrega","observacao");

        // validar persistência no Mongo (aguardar pequeno delay)
        TimeUnit.SECONDS.sleep(1);
        var all = repo.findAll();
        assertThat(all).isNotEmpty();
    }
}
