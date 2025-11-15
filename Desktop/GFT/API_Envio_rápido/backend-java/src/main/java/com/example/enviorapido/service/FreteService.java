package com.example.enviorapido.service;

import org.springframework.stereotype.Service;

import com.example.enviorapido.model.Envio;

@Service
public class FreteService {
    public double calcular(Envio.Pacote pacote) {
        if (pacote == null || pacote.peso_kg == null) return 0.0;
        double peso = pacote.peso_kg;
        double base = 10.0; // tarifa base
        double porKg = 8.0; // por kg
        double volumeFactor = 1.0;
        if (pacote.altura_cm != null && pacote.largura_cm != null && pacote.comprimento_cm != null) {
            double volume = pacote.altura_cm * pacote.largura_cm * pacote.comprimento_cm;
            volumeFactor = 1.0 + (volume / 100000.0);
        }
        return Math.round((base + porKg * peso) * volumeFactor * 100.0) / 100.0;
    }

    public FreteResult calcularComApi(String cepOrigem, String cepDestino, Envio.Pacote pacote) {
        // Mock de chamada a API externa de frete: combina cálculo local com prazo estimado
        double valor = calcular(pacote);
        int prazo = 3; // padrão
        try {
            // heurística simples: se DDD/UF diferente aumentar prazo
            if (cepOrigem != null && cepDestino != null && !cepOrigem.substring(0,2).equals(cepDestino.substring(0,2))) {
                prazo += 2;
            }
        } catch (Exception ignored) {}

        String obs = "Calculado via mock de frete interno";
        return new FreteResult(valor, prazo, obs);
    }

    public static class FreteResult {
        public double valorFrete;
        public int prazoEntrega;
        public String observacao;

        public FreteResult(double valorFrete, int prazoEntrega, String observacao) {
            this.valorFrete = valorFrete;
            this.prazoEntrega = prazoEntrega;
            this.observacao = observacao;
        }
    }
}
