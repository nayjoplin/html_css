package com.example.enviorapido.repository;

import com.example.enviorapido.model.Envio;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EnvioRepository extends MongoRepository<Envio, String> {
}
