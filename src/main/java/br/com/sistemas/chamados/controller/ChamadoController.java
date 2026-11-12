package br.com.sistemas.chamados.controller;

import br.com.sistemas.chamados.dto.ChamadoRequest;
import br.com.sistemas.chamados.dto.ChamadoResponse;
import br.com.sistemas.chamados.dto.ComentarioRequest;
import br.com.sistemas.chamados.dto.ComentarioResponse;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import br.com.sistemas.chamados.service.ChamadoService;
import br.com.sistemas.chamados.service.ComentarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/chamados")
public class ChamadoController {

    private final ChamadoService service;
    private final ComentarioService comentarioService;

    public ChamadoController(ChamadoService service, ComentarioService comentarioService) {
        this.service = service;
        this.comentarioService = comentarioService;
    }

    @PostMapping
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest dto) {
        ChamadoResponse criado = service.criar(dto);
        return ResponseEntity.created(URI.create("/chamados/" + criado.id())).body(criado);
    }

    @GetMapping
    public List<ChamadoResponse> listar(@RequestParam(required = false) StatusChamado status,
                                        @RequestParam(required = false) Prioridade prioridade,
                                        @RequestParam(required = false) Long clienteId) {
        return service.listar(status, prioridade, clienteId);
    }

    @GetMapping("/{id}")
    public ChamadoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public ChamadoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ChamadoRequest dto) {
        return service.atualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/atribuir")
    public ChamadoResponse atribuir(@PathVariable Long id, @Valid @RequestBody ChamadoRequest dto) {
        return service.atribuir(id, dto);
    }

    @PatchMapping("/{id}/status")
    public ChamadoResponse status(@PathVariable Long id, @Valid @RequestBody ChamadoRequest dto) {
        return service.status(id, dto);
    }

    @PostMapping("/{id}/comentarios")
    public ComentarioResponse criarComentario(@PathVariable Long id, @Valid @RequestBody ComentarioRequest dto) {
        return comentarioService.criar(dto);
    }

    @GetMapping("/{id}/comentarios")
    public List<ComentarioResponse> listarComentarios(@PathVariable Long id) {
        return comentarioService.listar(id);
    }

}
