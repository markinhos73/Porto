package com.porto.testecnae.service;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.domain.CadastroSecundario;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.repository.CadastroSecundarioRepository;
import com.porto.testecnae.service.impl.CadastroSecundarioServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroSecundarioServiceTest {

    @Mock
    private CadastroSecundarioRepository repository;

    @Mock
    private AtividadeEconomicaCnaeRepository cnaeRepository;

    @InjectMocks
    private CadastroSecundarioServiceImpl service;

    @Test
    @DisplayName("Deve cadastrar com sucesso quando CNAE existir")
    void deveCadastrarComSucesso() {
        var request = new CadastroSecundarioRequest("Tech Porto", "12345678000199", "6201-5/01");
        var cnae = new AtividadeEconomicaCnae(1L, "6201-5/01", "Desenvolvimento de programas", "Tecnologia");
        var cadastroSalvo = new CadastroSecundario(1L, "Tech Porto", "12345678000199", cnae);

        when(cnaeRepository.findByCodigo("6201-5/01")).thenReturn(Optional.of(cnae));
        when(repository.save(any(CadastroSecundario.class))).thenReturn(cadastroSalvo);

        var response = service.cadastrar(request);

        assertNotNull(response);
        assertEquals("Tech Porto", response.nomeFantasia());
        verify(repository, times(1)).save(any(CadastroSecundario.class));
    }

    @Test
    @DisplayName("Deve lancar BAD_REQUEST ao tentar cadastrar com CNAE inexistente")
    void deveLancarExcecaoAoCadastrarComCnaeInexistente() {
        var request = new CadastroSecundarioRequest("Fake", "000", "9999-9/99");
        when(cnaeRepository.findByCodigo("9999-9/99")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.cadastrar(request));
        verify(repository, never()).save(any());
    }
}