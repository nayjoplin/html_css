package com.example.enviorapido.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class RootController {

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> root() throws IOException {
        ClassPathResource res = new ClassPathResource("static/index.html");
        byte[] b = res.getInputStream().readAllBytes();
        String html = new String(b, StandardCharsets.UTF_8);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE).body(html);
    }

}
