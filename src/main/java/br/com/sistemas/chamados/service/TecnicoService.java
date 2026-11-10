package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.ClienteResponse;
import br.com.sistemas.chamados.dto.TecnicoRequest;
import br.com.sistemas.chamados.dto.TecnicoResponse;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Tecnico;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository repository;

    public TecnicoService(TecnicoRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TecnicoResponse criar(TecnicoRequest dto) {
        if (repository.existsByEmail(dto.email())) {
            throw new RegraNegocioException("Já existe técnico com o e-mail " + dto.email());
        }

        Tecnico c = repository.save(new Tecnico(dto.nome(), dto.email(), dto.perfil()));
        return TecnicoResponse.de(c);
    }

    @Transactional(readOnly = true)
    public List<TecnicoResponse> listar() {
        return repository.findAll().stream().map(TecnicoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public TecnicoResponse buscar(Long id) {
        return TecnicoResponse.de(buscarEntity(id));
    }

    @Transactional
    public TecnicoResponse atualizar(Long id, TecnicoRequest dto) {
        Tecnico t = buscarEntity(id);
        if (repository.existsByEmailAndIdNot(dto.email(), id)) {
            throw new RegraNegocioException("Já existe técnico com o e-mail " + dto.email());
        }

        t.setNome(dto.nome());
        t.setEmail(dto.email());
        t.setPerfil(dto.perfil());
        return TecnicoResponse.de(t);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id));
    }

    private Tecnico buscarEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Técnico " + id + " não encontrado"));
    }

}
