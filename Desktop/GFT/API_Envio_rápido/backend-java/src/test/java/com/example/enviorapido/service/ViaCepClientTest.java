package com.example.enviorapido.service;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

class ViaCepClientTest {

    @Test
    void buscar_whenRestReturnsMap_returnsMap() {
        ViaCepClient client = new ViaCepClient();
        Map<String, Object> ret = new HashMap<>();
        ret.put("cep", "01001-000");
        RestTemplate mock = new RestTemplate() {
            @Override
            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
                return responseType.cast(ret);
            }
        };
        ReflectionTestUtils.setField(client, "rest", mock);

        Map<String, Object> result = client.buscar("01001000");
        assertNotNull(result);
        assertEquals("01001-000", result.get("cep"));
    }

    @Test
    void buscar_whenRestThrows_returnsNull() {
        ViaCepClient client = new ViaCepClient();
        RestTemplate mock = new RestTemplate() {
            @Override
            public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
                throw new RuntimeException("fail");
            }
        };
        ReflectionTestUtils.setField(client, "rest", mock);

        Map<String, Object> result = client.buscar("00000000");
        assertNull(result);
    }
}
