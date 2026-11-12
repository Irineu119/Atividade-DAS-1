package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.entity.*;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChamadoService {

    private final ChamadoRepository repository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final TecnicoRepository tecnicoRepository;

    public ChamadoService(ChamadoRepository repository, ClienteRepository clienteRepository, CategoriaRepository categoriaRepository,
                          TecnicoRepository tecnicoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.tecnicoRepository = tecnicoRepository;
    }

    @Transactional
    public ChamadoResponse criar(ChamadoRequest dto) {
        if (dto.titulo() == null)
            throw new RegraNegocioException("Título é obrigatório");

        Cliente c = buscarEntity(dto.cliente(), clienteRepository, "Cliente");
        Categoria categoria = buscarEntity(dto.categoria(), categoriaRepository, "Categoria");

        Chamado salvo = repository.save(new Chamado(dto.titulo(), dto.descricao(), c, categoria, null, LocalDateTime.now(),
                                                    null, Prioridade.MEDIA, StatusChamado.ABERTO));
        return ChamadoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar(StatusChamado status, Prioridade prioridade, Long clienteId) {
        List<ChamadoResponse> todos = repository.findAll().stream().map(ChamadoResponse::de).toList();
        List<ChamadoResponse> ret = new ArrayList<>();
        for (ChamadoResponse c : todos) {
            if (status != null && c.statusChamado() != status)
                continue;

            if (prioridade != null && c.prioridade() != prioridade)
                continue;

            if (clienteId != null && !c.cliente().equals(clienteId))
                continue;

            ret.add(c);
        }

        return ret;
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id, repository, "Chamado"));
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado c = buscarEntity(id, repository, "Chamado");
        if (c.getStatus() == StatusChamado.FECHADO)
            throw new RegraNegocioException("Chamado fechado " + id + " não pode ser modificado");

        Cliente cliente = buscarEntity(dto.cliente(), clienteRepository, "Cliente");
        Categoria categoria = buscarEntity(dto.categoria(), categoriaRepository, "Categoria");

        if (dto.titulo() != null)
            c.setTitulo(dto.titulo());

        if (dto.descricao() != null)
            c.setDescricao(dto.descricao());

        if (cliente != null)
            c.setCliente(cliente);

        if (categoria != null)
            c.setCategoria(categoria);

        if (dto.prioridade() != null)
            c.setPrioridade(dto.prioridade());

        return ChamadoResponse.de(c);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id, repository, "Chamado"));
    }

    @Transactional
    public ChamadoResponse atribuir(Long id, ChamadoRequest dto) {
        Chamado c = buscarEntity(id, repository, "Chamado");
        if (c.getStatus() == StatusChamado.FECHADO)
            throw new RegraNegocioException("Chamado fechado " + id + " não pode ser modificado");

        Tecnico tecnico = buscarEntity(dto.tecnico(), tecnicoRepository, "Técnico");
        if (tecnico != null || dto.tecnico() == null)
            c.setTecnico(tecnico);

        return ChamadoResponse.de(c);
    }

    @Transactional
    public ChamadoResponse status(Long id, ChamadoRequest dto) {
        if (dto.status() == null)
            throw new RegraNegocioException("Status é obrigatório");

        Chamado c = buscarEntity(id, repository, "Chamado");
        if (c.getStatus() == StatusChamado.FECHADO)
            throw new RegraNegocioException("Chamado fechado " + id + " não pode ser modificado");

        c.setStatus(dto.status());
        if (c.getStatus() == StatusChamado.FECHADO)
            c.setDataFechamento(LocalDateTime.now());

        return ChamadoResponse.de(c);
    }

    private <E> E buscarEntity(Long id, JpaRepository<E, Long> repository, String nome) {
        if (id == null) {
            return null;
        }

        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(nome + " " + id + " não encontrado(a)"));
    }

}
