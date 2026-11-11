package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.entity.*;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        Cliente c = buscarEntity(dto.cliente(), clienteRepository, "Cliente");
        Categoria categoria = buscarEntity(dto.categoria(), categoriaRepository, "Categoria");

        Chamado salvo = repository.save(new Chamado(dto.titulo(), dto.descricao(), c, categoria, null, LocalDateTime.now(),
                                                    null, Prioridade.MEDIA, StatusChamado.ABERTO));
        return ChamadoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar() {
        return repository.findAll().stream().map(ChamadoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id, repository, "Chamado"));
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado c = buscarEntity(id, repository, "Chamado");
        Cliente cliente = buscarEntity(dto.cliente(), clienteRepository, "Cliente");
        Categoria categoria = buscarEntity(dto.categoria(), categoriaRepository, "Categoria");
        Tecnico tecnico = buscarEntity(dto.tecnico(), tecnicoRepository, "Técnico");

        c.setTitulo(dto.titulo());
        c.setDescricao(dto.descricao());
        c.setCliente(cliente);
        c.setCategoria(categoria);
        c.setTecnico(tecnico);
        c.setDataFechamento(dto.dataFechamento());
        c.setPrioridade(dto.prioridade());
        c.setStatus(dto.status());
        return ChamadoResponse.de(c);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id, repository, "Chamado"));
    }

    private <E> E buscarEntity(Long id, JpaRepository<E, Long> repository, String nome) {
        if (id == null) {
            return null;
        }

        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(nome + " " + id + " não encontrado(a)"));
    }

}
