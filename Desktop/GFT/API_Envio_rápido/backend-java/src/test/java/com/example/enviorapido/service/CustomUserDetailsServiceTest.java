package com.example.enviorapido.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.enviorapido.model.User;
import com.example.enviorapido.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository repo;

    @InjectMocks
    CustomUserDetailsService svc;

    @Test
    void loadUserByUsername_found_returnsUserDetails() {
        User u = new User();
        u.setUsername("joe");
        u.setPassword("p");
        u.setRoles(List.of("ROLE_USER"));
        when(repo.findByUsername("joe")).thenReturn(Optional.of(u));

        UserDetails ud = svc.loadUserByUsername("joe");
        assertEquals("joe", ud.getUsername());
        assertEquals("p", ud.getPassword());
        assertFalse(ud.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_missing_throws() {
        when(repo.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("x"));
    }
}
