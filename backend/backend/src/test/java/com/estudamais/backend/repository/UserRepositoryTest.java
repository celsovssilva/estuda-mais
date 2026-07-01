package com.estudamais.backend.repository;

import com.estudamais.backend.entity.User;
import com.estudamais.backend.response.AuthResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private UserRepository usuarioRepository;

    @Test
    public void deveSalvarEBuscarUsuarioComSucesso() {
        // 1. Cenario (Arrange): Preparamos os dados
        User novoUsuario = new User();
        novoUsuario.setName("Maria");
        novoUsuario.setEmail("maria@email.com");

        User usuarioSalvo = usuarioRepository.save(novoUsuario);
        User usuarioBuscado = usuarioRepository.findById(usuarioSalvo.getId()).orElse(null);

        // 3. Verificação (Assert): Checamos se o resultado é o esperado
        assertNotNull(usuarioBuscado, "O usuário buscado não deveria ser nulo");
        assertEquals("Maria", usuarioBuscado.getName(), "O nome salvo deve ser igual ao buscado");
    }
}