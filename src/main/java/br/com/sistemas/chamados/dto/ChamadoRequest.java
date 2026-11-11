package br.com.sistemas.chamados.dto;

import br.com.sistemas.chamados.entity.Prioridade;
import br.com.sistemas.chamados.entity.StatusChamado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ChamadoRequest(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 100, message = "Título deve ter no máximo 100 caracteres")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 2048, message = "Descrição deve ter no máximo 2048 caracteres")
        String descricao,

        //@NotBlank(message = "Cliente é obrigatório")
        Long cliente,

        //@NotBlank(message = "Categoria é obrigatória")
        Long categoria,

        Long tecnico,
        LocalDateTime dataFechamento,
        Prioridade prioridade,
        StatusChamado status
) {}
