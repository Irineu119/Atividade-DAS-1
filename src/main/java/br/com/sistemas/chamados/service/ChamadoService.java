package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.entity.*;
import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.repository.CategoriaRepository;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.TecnicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        Optional<Cliente> c = clienteRepository.findById(dto.cliente());
        if (c.isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente " + dto.cliente() + " não existe");
        }

        Optional<Categoria> categoria = categoriaRepository.findById(dto.categoria());
        if (categoria.isEmpty()) {
            throw new RecursoNaoEncontradoException("Categoria " + dto.categoria() + " não existe");
        }

        Chamado salvo = repository.save(new Chamado(dto.titulo(), dto.descricao(), c.get(), categoria.get(), null, LocalDateTime.now(),
                                                    null, Prioridade.MEDIA, StatusChamado.ABERTO));
        return ChamadoResponse.de(salvo);
    }

    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar() {
        return repository.findAll().stream().map(ChamadoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscar(Long id) {
        return ChamadoResponse.de(buscarEntity(id));
    }

    @Transactional
    public ChamadoResponse atualizar(Long id, ChamadoRequest dto) {
        Chamado c = buscarEntity(id);

        Optional<Cliente> cliente = clienteRepository.findById(dto.cliente());
        if (cliente.isEmpty()) {
            throw new RecursoNaoEncontradoException("Cliente " + dto.cliente() + " não existe");
        }

        Optional<Categoria> categoria = categoriaRepository.findById(dto.categoria());
        if (categoria.isEmpty()) {
            throw new RecursoNaoEncontradoException("Categoria " + dto.categoria() + " não existe");
        }

        Optional<Tecnico> tecnico = tecnicoRepository.findById(dto.tecnico());
        if (tecnico.isEmpty()) {
            throw new RecursoNaoEncontradoException("Técnico " + dto.categoria() + " não existe");
        }


        c.setTitulo(dto.titulo());
        c.setDescricao(dto.descricao());
        c.setCliente(cliente.get());
        c.setCategoria(categoria.get());
        c.setTecnico(tecnico.get());
        c.setDataFechamento(dto.dataFechamento());
        c.setPrioridade(dto.prioridade());
        c.setStatus(dto.status());
        return ChamadoResponse.de(c);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntity(id));
    }

    private Chamado buscarEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Chamado " + id + " não encontrado"));
    }

}
