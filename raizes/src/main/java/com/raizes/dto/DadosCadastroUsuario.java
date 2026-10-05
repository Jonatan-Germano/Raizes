package com.raizes.dto;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroUsuario(@NotBlank String nome,
                                   @NotBlank String email,
                                   @NotBlank String telefone) {
}
