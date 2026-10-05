package com.raizes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DadosItemPedido(@NotNull UUID id,
                              @NotBlank String nome,
                              @NotNull Integer quantidade) {
}
