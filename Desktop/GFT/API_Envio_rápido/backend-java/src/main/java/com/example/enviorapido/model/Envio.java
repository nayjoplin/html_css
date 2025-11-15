package com.example.enviorapido.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("envios")
public class Envio {
    @Id
    public String id;

    public Remetente remetente;
    public Destinatario destinatario;
    public Pacote pacote;
    public Double valorFrete;
    public String status;
    public Date createdAt;

    public static class Remetente {
        public String nome;
        public String cep;
        public String logradouro;
        public String bairro;
        public String cidade;
        public String uf;
    }

    public static class Destinatario {
        public String nome;
        public String cep;
        public String logradouro;
        public String bairro;
        public String cidade;
        public String uf;
    }

    public static class Pacote {
        public Double peso_kg;
        public Integer altura_cm;
        public Integer largura_cm;
        public Integer comprimento_cm;
    }
}
