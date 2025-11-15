package com.example.enviorapido;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;

@SpringBootApplication
@EnableRabbit
public class EnvioRapidoApplication {
    public static void main(String[] args) {
        SpringApplication.run(EnvioRapidoApplication.class, args);
    }
}
