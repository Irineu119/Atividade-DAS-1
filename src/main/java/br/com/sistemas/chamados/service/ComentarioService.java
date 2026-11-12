package br.com.sistemas.chamados.service;

import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.dto.ComentarioResponse;
import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Cliente;
import br.com.sistemas.chamados.entity.Comentario;
import br.com.sistemas.chamados.exception.RecursoNaoEncontradoException;
import br.com.sistemas.chamados.exception.RegraNegocioException;
import br.com.sistemas.chamados.repository.ChamadoRepository;
import br.com.sistemas.chamados.repository.ClienteRepository;
import br.com.sistemas.chamados.repository.ComentarioRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComentarioService {

    private final ComentarioRepository repository;
    private final ClienteRepository clienteRepository;
    private final ChamadoRepository chamadoRepository;

    public ComentarioService(ComentarioRepository repository, ClienteRepository clienteRepository, ChamadoRepository chamadoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.chamadoRepository = chamadoRepository;
    }

    public ComentarioResponse criar(ComentarioRequest dto) {
        Cliente autor = buscarEntity(dto.autor(), clienteRepository, "Cliente");
        if (autor == null)
            throw new RegraNegocioException("Cliente é obrigatório");

        Chamado chamado = buscarEntity(dto.chamado(), chamadoRepository, "Chamado");
        if (chamado == null)
            throw new RegraNegocioException("Chamado é obrigatório");

        Comentario salvo = repository.save(new Comentario(dto.texto(), LocalDateTime.now(), autor, chamado));
        return ComentarioResponse.de(salvo);
    }

    public List<ComentarioResponse> listar(Long idChamado) {
        List<ComentarioResponse> todos = repository.findAll().stream().map(ComentarioResponse::de).toList();
        List<ComentarioResponse> ret = new ArrayList<>();
        for (ComentarioResponse c : todos) {
            if (idChamado != null && !c.chamado().getId().equals(idChamado))
                break;

            ret.add(c);
        }

        return ret;
    }

    private <E> E buscarEntity(Long id, JpaRepository<E, Long> repository, String nome) {
        if (id == null) {
            return null;
        }

        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(nome + " " + id + " não encontrado(a)"));
    }

}
