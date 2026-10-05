package com.raizes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DadosCadastroCardapio(@NotBlank String nome,
                                    @NotBlank String descricao,
                                    @NotNull  BigDecimal preco,
                                    @NotBlank String categoria,
                                    @NotNull Integer estoque) {
}
