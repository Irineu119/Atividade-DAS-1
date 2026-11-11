package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Chamado;
import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;

import java.time.LocalDateTime;

public record ChamadoResponse(Long id, String titulo, String descricao, Long cliente, Long categoria, Long tecnico, LocalDateTime dataAbertura,
                              LocalDateTime dataFechamento, Prioridade prioridade, StatusChamado statusChamado) {

    public static ChamadoResponse de(Chamado c) {
        return new ChamadoResponse(c.getId(), c.getTitulo(), c.getDescricao(), c.getCliente().getId(), c.getCategoria().getId(),
                c.getTecnico() != null ? c.getTecnico().getId() : null, c.getDataAbertura(), c.getDataFechamento(), c.getPrioridade(), c.getStatus());
    }

}
