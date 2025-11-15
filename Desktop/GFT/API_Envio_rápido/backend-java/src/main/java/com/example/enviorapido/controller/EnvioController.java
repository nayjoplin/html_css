package com.example.enviorapido.controller;

import com.example.enviorapido.amqp.RabbitSender;
import com.example.enviorapido.model.Envio;
import com.example.enviorapido.repository.EnvioRepository;
import com.example.enviorapido.service.FreteService;
import com.example.enviorapido.service.ViaCepClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioRepository repo;
    private final FreteService freteService;
    private final ViaCepClient viaCep;
    private final RabbitSender sender;

    public EnvioController(EnvioRepository repo, FreteService freteService, ViaCepClient viaCep, RabbitSender sender) {
        this.repo = repo;
        this.freteService = freteService;
        this.viaCep = viaCep;
        this.sender = sender;
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody com.example.enviorapido.dto.EnvioRequest req) {
        // validar CEPs via ViaCEP
        String cepRem = req.remetente.cep.replaceAll("[^0-9]", "");
        String cepDest = req.destinatario.cep.replaceAll("[^0-9]", "");

        Map<String, Object> dadosRem = viaCep.buscar(cepRem);
        if (dadosRem == null || dadosRem.containsKey("erro")) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "CEP inválido"));
        }
        Map<String, Object> dadosDest = viaCep.buscar(cepDest);
        if (dadosDest == null || dadosDest.containsKey("erro")) {
            return ResponseEntity.badRequest().body(Map.of("mensagem", "CEP inválido"));
        }

        Envio envio = mapToEntity(req);

        // preencher endereços com dados do ViaCEP
        preencherEnderecoComDados(envio.remetente, dadosRem);
        preencherEnderecoComDados(envio.destinatario, dadosDest);

        // chamar adaptador de frete (mock/externo)
        com.example.enviorapido.service.FreteService.FreteResult freteResult = freteService.calcularComApi(cepRem, cepDest, envio.pacote);

        envio.valorFrete = freteResult.valorFrete;
        envio.status = "CRIADO";
        envio.createdAt = new Date();

        Envio saved = repo.save(envio);
        sender.enviarEnvio(saved);

        Map<String,Object> resposta = Map.of(
                "cepOrigem", cepRem,
                "cepDestino", cepDest,
                "valorFrete", freteResult.valorFrete,
                "prazoEntrega", freteResult.prazoEntrega,
                "observacao", freteResult.observacao
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Envio> update(@PathVariable String id, @RequestBody Envio envio) {
        return repo.findById(id).map(existing -> {
            envio.id = existing.id;
            envio.createdAt = existing.createdAt;
            Envio saved = repo.save(envio);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/endereco")
    public ResponseEntity<Envio> patchEndereco(@PathVariable String id, @RequestBody Map<String, Object> body) {
        return repo.findById(id).map(existing -> {
            if (body.containsKey("remetente")) {
                Map<String,Object> r = (Map<String,Object>) body.get("remetente");
                existing.remetente.nome = (String) r.getOrDefault("nome", existing.remetente.nome);
                existing.remetente.cep = (String) r.getOrDefault("cep", existing.remetente.cep);
                preencherEndereco(existing.remetente);
            }
            if (body.containsKey("destinatario")) {
                Map<String,Object> d = (Map<String,Object>) body.get("destinatario");
                existing.destinatario.nome = (String) d.getOrDefault("nome", existing.destinatario.nome);
                existing.destinatario.cep = (String) d.getOrDefault("cep", existing.destinatario.cep);
                preencherEndereco(existing.destinatario);
            }
            Envio saved = repo.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        return repo.findById(id).map(existing -> {
            repo.deleteById(id);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    private void preencherEndereco(Object pessoa) {
        try {
            String cep = null;
            if (pessoa instanceof Envio.Remetente) cep = ((Envio.Remetente) pessoa).cep;
            if (pessoa instanceof Envio.Destinatario) cep = ((Envio.Destinatario) pessoa).cep;
            if (cep == null) return;
            Map<String, Object> dados = viaCep.buscar(cep.replaceAll("[^0-9]",""));
            if (dados == null || dados.containsKey("erro")) return;
            if (pessoa instanceof Envio.Remetente) {
                Envio.Remetente r = (Envio.Remetente) pessoa;
                r.logradouro = (String) dados.getOrDefault("logradouro", r.logradouro);
                r.bairro = (String) dados.getOrDefault("bairro", r.bairro);
                r.cidade = (String) dados.getOrDefault("localidade", r.cidade);
                r.uf = (String) dados.getOrDefault("uf", r.uf);
            } else if (pessoa instanceof Envio.Destinatario) {
                Envio.Destinatario d = (Envio.Destinatario) pessoa;
                d.logradouro = (String) dados.getOrDefault("logradouro", d.logradouro);
                d.bairro = (String) dados.getOrDefault("bairro", d.bairro);
                d.cidade = (String) dados.getOrDefault("localidade", d.cidade);
                d.uf = (String) dados.getOrDefault("uf", d.uf);
            }
        } catch (Exception ignored) {
            // se falhar ao buscar endereco, continuar sem bloquear (erro ignorado)
        }
    }

    private void preencherEnderecoComDados(Object pessoa, Map<String,Object> dados) {
        try {
            if (dados == null || dados.containsKey("erro")) return;
            if (pessoa instanceof Envio.Remetente) {
                Envio.Remetente r = (Envio.Remetente) pessoa;
                r.logradouro = (String) dados.getOrDefault("logradouro", r.logradouro);
                r.bairro = (String) dados.getOrDefault("bairro", r.bairro);
                r.cidade = (String) dados.getOrDefault("localidade", r.cidade);
                r.uf = (String) dados.getOrDefault("uf", r.uf);
            } else if (pessoa instanceof Envio.Destinatario) {
                Envio.Destinatario d = (Envio.Destinatario) pessoa;
                d.logradouro = (String) dados.getOrDefault("logradouro", d.logradouro);
                d.bairro = (String) dados.getOrDefault("bairro", d.bairro);
                d.cidade = (String) dados.getOrDefault("localidade", d.cidade);
                d.uf = (String) dados.getOrDefault("uf", d.uf);
            }
        } catch (Exception ignored) {
            // não bloqueia fluxo
        }
    }

    private Envio mapToEntity(com.example.enviorapido.dto.EnvioRequest req) {
        Envio e = new Envio();
        e.remetente = new Envio.Remetente();
        e.remetente.nome = req.remetente.nome;
        e.remetente.cep = req.remetente.cep;

        e.destinatario = new Envio.Destinatario();
        e.destinatario.nome = req.destinatario.nome;
        e.destinatario.cep = req.destinatario.cep;

        e.pacote = new Envio.Pacote();
        e.pacote.peso_kg = req.pacote.peso_kg;
        e.pacote.altura_cm = req.pacote.altura_cm;
        e.pacote.largura_cm = req.pacote.largura_cm;
        e.pacote.comprimento_cm = req.pacote.comprimento_cm;

        return e;
    }
}
