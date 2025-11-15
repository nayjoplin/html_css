package com.example.enviorapido.controller;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.enviorapido.security.JwtUtil;
import com.example.enviorapido.service.UserService;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void registroRetorna201() throws Exception {
        when(userService.usernameExists("user1")).thenReturn(false);
        when(userService.register("user1","pass")).thenReturn(new com.example.enviorapido.model.User("user1","encoded","ROLE_USER".lines().toList()));

        String body = "{\"username\":\"user1\",\"password\":\"pass\"}";

        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    void loginRetornaTokenQuandoCredenciaisValidas() throws Exception {
        String body = "{\"username\":\"user1\",\"password\":\"pass\"}";
        when(jwtUtil.generateToken(org.mockito.ArgumentMatchers.any())).thenReturn("token123");

        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token123"));
    }
}
