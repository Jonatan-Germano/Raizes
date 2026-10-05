package com.raizes.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DadosAtualizacaoUsuario(@NotNull UUID id, String nome, String email, String telefone, Boolean ativo) {
}
