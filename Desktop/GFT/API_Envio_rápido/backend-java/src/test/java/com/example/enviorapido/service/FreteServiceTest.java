package com.example.enviorapido.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.enviorapido.model.Envio;
import org.junit.jupiter.api.Test;

class FreteServiceTest {

    private final FreteService service = new FreteService();

    @Test
    void calcular_nullPacote_returnsZero() {
        double v = service.calcular(null);
        assertEquals(0.0, v, 0.0001);
    }

    @Test
    void calcular_simpleWeight_calculatesBase() {
        Envio.Pacote p = new Envio.Pacote();
        p.peso_kg = 2.5;
        double v = service.calcular(p);
        // base 10 + 8 * 2.5 = 30.0
        assertEquals(30.0, v, 0.001);
    }

    @Test
    void calcular_withVolume_increasesByVolumeFactor() {
        Envio.Pacote p = new Envio.Pacote();
        p.peso_kg = 1.0;
        p.altura_cm = 100;
        p.largura_cm = 50;
        p.comprimento_cm = 20; // volume 100000 -> factor 1 + 1 = 2
        double v = service.calcular(p);
        // base 10 + 8*1 = 18 -> *2 = 36
        assertEquals(36.0, v, 0.01);
    }

    @Test
    void calcularComApi_samePrefix_shorterPrazo() {
        Envio.Pacote p = new Envio.Pacote();
        p.peso_kg = 3.0;
        FreteService.FreteResult r = service.calcularComApi("12345-000", "12399-999", p);
        assertNotNull(r);
        assertEquals(service.calcular(p), r.valorFrete, 0.001);
        assertTrue(r.prazoEntrega >= 3);
        assertNotNull(r.observacao);
    }

    @Test
    void calcularComApi_differentPrefix_longerPrazo() {
        Envio.Pacote p = new Envio.Pacote();
        p.peso_kg = 3.0;
        FreteService.FreteResult r = service.calcularComApi("12000-000", "99000-000", p);
        assertNotNull(r);
        // when prefix differs, prazo should be original + 2
        assertEquals(5, r.prazoEntrega);
    }
}
