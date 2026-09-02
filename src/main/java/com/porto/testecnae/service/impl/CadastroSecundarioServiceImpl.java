package com.porto.testecnae.service.impl;

import com.porto.testecnae.domain.CadastroSecundario;
import com.porto.testecnae.domain.AtividadeEconomicaCnae;
import com.porto.testecnae.dto.AtividadeEconomicaCnaeResponse;
import com.porto.testecnae.dto.CadastroSecundarioRequest;
import com.porto.testecnae.dto.CadastroSecundarioResponse;
import com.porto.testecnae.repository.AtividadeEconomicaCnaeRepository;
import com.porto.testecnae.repository.CadastroSecundarioRepository;
import com.porto.testecnae.service.CadastroSecundarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CadastroSecundarioServiceImpl implements CadastroSecundarioService {

    private final CadastroSecundarioRepository repository;
    private final AtividadeEconomicaCnaeRepository cnaeRepository;

    @Override
    public CadastroSecundarioResponse cadastrar(CadastroSecundarioRequest request) {
        var cnae = buscarCnaeOuLancarExcecao(request.codigoCnae());

        var cadastro = CadastroSecundario.builder()
                .nomeFantasia(request.nomeFantasia())
                .documento(request.documento())
                .cnae(cnae)
                .build();

        return CadastroSecundarioResponse.fromEntity(repository.save(cadastro));
    }

    @Override
    public AtividadeEconomicaCnaeResponse validarCnae(String codigoCnae) {
        var cnae = buscarCnaeOuLancarExcecao(codigoCnae);
        return AtividadeEconomicaCnaeResponse.fromEntity(cnae);
    }

    @Override
    public List<CadastroSecundarioResponse> listarTodos() {
        return repository.findAll()
                .stream()
                .map(CadastroSecundarioResponse::fromEntity)
                .toList();
    }

    private AtividadeEconomicaCnae buscarCnaeOuLancarExcecao(String codigoCnae) {
        return cnaeRepository.findByCodigo(codigoCnae)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "CNAE " + codigoCnae + " não encontrado. Cadastro não permitido."
                ));
    }
}