package com.porto.testecnae.service;

import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.service.impl.AtividadeEconomicaCnaeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeEconomicaCnaeServiceTest {

    @Mock
    private AtividadeEconomicaCnaeRepository repository;

    @InjectMocks
    private AtividadeEconomicaCnaeServiceImpl service;

    @Test
    @DisplayName("Deve buscar CNAE por codigo com sucesso")
    void deveBuscarPorCodigoComSucesso() {
        var cnae = new AtividadeEconomicaCnae(1L, "6201-5/01", "Desenvolvimento de programas", "Tecnologia");
        when(repository.findByCodigo("6201-5/01")).thenReturn(Optional.of(cnae));

        var response = service.buscarPorCodigo("6201-5/01");

        assertNotNull(response);
        assertEquals("6201-5/01", response.codigo());
        verify(repository, times(1)).findByCodigo("6201-5/01");
    }

    @Test
    @DisplayName("Deve lancar excecao NOT_FOUND quando codigo CNAE nao existir")
    void deveLancarExcecaoQuandoCodigoNaoExistir() {
        when(repository.findByCodigo("9999-9/99")).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.buscarPorCodigo("9999-9/99"));
        verify(repository, times(1)).findByCodigo("9999-9/99");
    }

    @Test
    @DisplayName("Deve buscar CNAEs por descricao contendo o termo")
    void deveBuscarPorDescricao() {
        var cnae = new AtividadeEconomicaCnae(1L, "6201-5/01", "Desenvolvimento de programas", "Tecnologia");
        when(repository.buscarPorDescricao("programas")).thenReturn(List.of(cnae));

        var result = service.buscarPorDescricao("programas");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(repository, times(1)).buscarPorDescricao("programas");
    }
}