package com.example.enviorapido.controller;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.enviorapido.amqp.RabbitSender;
import com.example.enviorapido.model.Envio;
import com.example.enviorapido.repository.EnvioRepository;
import com.example.enviorapido.service.FreteService;
import com.example.enviorapido.service.ViaCepClient;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(EnvioController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EnvioControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private EnvioRepository repo;

    @MockBean
    private FreteService freteService;

    @MockBean
    private ViaCepClient viaCepClient;

    @MockBean
    private RabbitSender sender;

    @MockBean
    private com.example.enviorapido.security.JwtFilter jwtFilter;

    @MockBean
    private com.example.enviorapido.security.JwtUtil jwtUtil;

    @Test
    void criaEnvioRetorna201() throws Exception {
        com.example.enviorapido.dto.EnvioRequest envio = new com.example.enviorapido.dto.EnvioRequest();
        envio.remetente = new com.example.enviorapido.dto.EnvioRequest.RemetenteRequest();
        envio.remetente.nome = "Empresa";
        envio.remetente.cep = "01001000";
        envio.destinatario = new com.example.enviorapido.dto.EnvioRequest.DestinatarioRequest();
        envio.destinatario.nome = "Cliente";
        envio.destinatario.cep = "87020025";
        envio.pacote = new com.example.enviorapido.dto.EnvioRequest.PacoteRequest();
        envio.pacote.peso_kg = 1.5;

        // mock ViaCEP responses para remetente e destinatário
        when(viaCepClient.buscar("01001000")).thenReturn(java.util.Map.of("logradouro","Praça","bairro","Centro","localidade","São Paulo","uf","SP"));
        when(viaCepClient.buscar("87020025")).thenReturn(java.util.Map.of("logradouro","Rua X","bairro","Bairro","localidade","Cidade","uf","PR"));

        when(freteService.calcularComApi(org.mockito.ArgumentMatchers.eq("01001000"), org.mockito.ArgumentMatchers.eq("87020025"), org.mockito.ArgumentMatchers.any()))
            .thenReturn(new com.example.enviorapido.service.FreteService.FreteResult(25.5, 4, "ok"));
        when(repo.save(org.mockito.ArgumentMatchers.any())).thenAnswer(i -> {
            Envio e = i.getArgument(0);
            e.id = "abc123";
            e.createdAt = new Date();
            return e;
        });

        mvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(envio)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cepOrigem").value("01001000"))
                .andExpect(jsonPath("$.cepDestino").value("87020025"))
                .andExpect(jsonPath("$.valorFrete").value(25.5))
                .andExpect(jsonPath("$.prazoEntrega").value(4));
    }

    @Test
    void criaEnvioComCepInvalidoRetorna400() throws Exception {
        com.example.enviorapido.dto.EnvioRequest envio = new com.example.enviorapido.dto.EnvioRequest();
        envio.remetente = new com.example.enviorapido.dto.EnvioRequest.RemetenteRequest();
        envio.remetente.nome = "Empresa";
        envio.remetente.cep = "00000000"; // cep inválido
        envio.destinatario = new com.example.enviorapido.dto.EnvioRequest.DestinatarioRequest();
        envio.destinatario.nome = "Cliente";
        envio.destinatario.cep = "87020025";
        envio.pacote = new com.example.enviorapido.dto.EnvioRequest.PacoteRequest();
        envio.pacote.peso_kg = 1.5;

        when(viaCepClient.buscar("00000000")).thenReturn(java.util.Map.of("erro", true));

        mvc.perform(post("/api/envios").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(envio)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("CEP inválido"));
    }

    @Test
    void getEnvioRetorna200() throws Exception {
        Envio e = new Envio();
        e.id = "abc123";
        e.remetente = new Envio.Remetente();
        e.remetente.nome = "Empresa";
        e.createdAt = new Date();

        when(repo.findById("abc123")).thenReturn(Optional.of(e));

        mvc.perform(get("/api/envios/abc123")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value("abc123"));
    }

    @Test
    void criaEnvioPublicaMensagem() throws Exception {
        com.example.enviorapido.dto.EnvioRequest envio = new com.example.enviorapido.dto.EnvioRequest();
        envio.remetente = new com.example.enviorapido.dto.EnvioRequest.RemetenteRequest();
        envio.remetente.nome = "Empresa";
        envio.remetente.cep = "01001000";
        envio.destinatario = new com.example.enviorapido.dto.EnvioRequest.DestinatarioRequest();
        envio.destinatario.nome = "Cliente";
        envio.destinatario.cep = "87020025";
        envio.pacote = new com.example.enviorapido.dto.EnvioRequest.PacoteRequest();
        envio.pacote.peso_kg = 1.5;

        when(viaCepClient.buscar("01001000")).thenReturn(java.util.Map.of("logradouro","Praça","bairro","Centro","localidade","São Paulo","uf","SP"));
        when(viaCepClient.buscar("87020025")).thenReturn(java.util.Map.of("logradouro","Rua X","bairro","Bairro","localidade","Cidade","uf","PR"));

        when(freteService.calcularComApi(org.mockito.ArgumentMatchers.eq("01001000"), org.mockito.ArgumentMatchers.eq("87020025"), org.mockito.ArgumentMatchers.any()))
            .thenReturn(new com.example.enviorapido.service.FreteService.FreteResult(25.5, 4, "ok"));
        when(repo.save(org.mockito.ArgumentMatchers.any())).thenAnswer(i -> {
            Envio e = i.getArgument(0);
            e.id = "abc123";
            e.createdAt = new Date();
            return e;
        });

        mvc.perform(post("/api/envios")
                .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(envio)))
                .andExpect(status().isCreated());

        verify(sender).enviarEnvio(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void freteApiErrorRetorna500() throws Exception {
        com.example.enviorapido.dto.EnvioRequest envio = new com.example.enviorapido.dto.EnvioRequest();
        envio.remetente = new com.example.enviorapido.dto.EnvioRequest.RemetenteRequest();
        envio.remetente.nome = "Empresa";
        envio.remetente.cep = "01001000";
        envio.destinatario = new com.example.enviorapido.dto.EnvioRequest.DestinatarioRequest();
        envio.destinatario.nome = "Cliente";
        envio.destinatario.cep = "87020025";
        envio.pacote = new com.example.enviorapido.dto.EnvioRequest.PacoteRequest();
        envio.pacote.peso_kg = 1.5;

        when(viaCepClient.buscar("01001000")).thenReturn(java.util.Map.of("logradouro","Praça","bairro","Centro","localidade","São Paulo","uf","SP"));
        when(viaCepClient.buscar("87020025")).thenReturn(java.util.Map.of("logradouro","Rua X","bairro","Bairro","localidade","Cidade","uf","PR"));

        when(freteService.calcularComApi(org.mockito.ArgumentMatchers.eq("01001000"), org.mockito.ArgumentMatchers.eq("87020025"), org.mockito.ArgumentMatchers.any()))
            .thenThrow(new RuntimeException("Frete API down"));

        mvc.perform(post("/api/envios").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(envio)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensagem").value("Frete API down"));
    }

    @Test
    void criaEnvioSemRemetenteRetorna400() throws Exception {
        java.util.Map<String,Object> body = java.util.Map.of(
                "destinatario", java.util.Map.of("nome","Cliente","cep","87020025"),
                "pacote", java.util.Map.of("peso_kg", 2.0)
        );

        mvc.perform(post("/api/envios").contentType(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Falha de validação"))
                .andExpect(jsonPath("$.erros.remetente").exists());
    }
}
