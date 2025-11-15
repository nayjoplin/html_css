package com.example.enviorapido.service;

import com.example.enviorapido.model.User;
import com.example.enviorapido.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repo;

    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    UserService service;

    @Test
    void register_encodesPassword_andSaves() {
        when(encoder.encode("raw")).thenReturn("encoded");
        User saved = new User("u","encoded", java.util.List.of("ROLE_USER"));
        when(repo.save(any())).thenReturn(saved);

        User r = service.register("u","raw");
        assertNotNull(r);
        assertEquals("u", r.getUsername());
        assertEquals("encoded", r.getPassword());
        verify(repo).save(any(User.class));
    }

    @Test
    void usernameExists_presentAndAbsent() {
        when(repo.findByUsername("ex"))
                .thenReturn(Optional.of(new User()));
        when(repo.findByUsername("no"))
                .thenReturn(Optional.empty());

        assertTrue(service.usernameExists("ex"));
        assertFalse(service.usernameExists("no"));
    }
}
