package br.com.sistemas.chamados.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioRequest(
        @NotBlank(message = "Texto é obrigatório")
        @Size(max = 2048, message = "Texto deve ter no máximo 2048 caracteres")
        String texto,

        Long autor,
        Long chamado
) {}
