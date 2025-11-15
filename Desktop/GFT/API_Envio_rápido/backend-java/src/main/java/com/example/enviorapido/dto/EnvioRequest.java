package com.example.enviorapido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EnvioRequest {

    @Valid
    @NotNull(message = "Remetente é obrigatório")
    public RemetenteRequest remetente;

    @Valid
    @NotNull(message = "Destinatário é obrigatório")
    public DestinatarioRequest destinatario;

    @Valid
    @NotNull(message = "Pacote é obrigatório")
    public PacoteRequest pacote;

    public static class RemetenteRequest {
        @NotBlank
        public String nome;
        @NotBlank(message = "Nome do remetente não pode ficar em branco")
        public String cep;
    }

    public static class DestinatarioRequest {
        @NotBlank
        public String nome;
        @NotBlank(message = "Nome do destinatário não pode ficar em branco")
        public String cep;
    }

    public static class PacoteRequest {
        @NotNull
        @Min(value = 1, message = "Peso mínimo é 1 kg")
        public Double peso_kg;
        public Integer altura_cm;
        public Integer largura_cm;
        public Integer comprimento_cm;
    }
}
