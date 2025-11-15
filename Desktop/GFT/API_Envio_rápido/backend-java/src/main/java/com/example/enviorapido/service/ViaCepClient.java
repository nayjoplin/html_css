package com.example.enviorapido.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Component
public class ViaCepClient {
    private final RestTemplate rest = new RestTemplate();

    public Map<String, Object> buscar(String cep) {
        String url = "https://viacep.com.br/ws/" + cep + "/json/";
        try {
            return rest.getForObject(url, Map.class);
        } catch (Exception e) {
            return null;
        }
    }
}
