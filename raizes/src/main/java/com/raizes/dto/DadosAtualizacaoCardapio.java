package com.raizes.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosAtualizacaoCardapio(@NotNull UUID id,
                                       String nome,
                                       String descricao,
                                       BigDecimal preco,
                                       String categoria,
                                       Boolean ativo,
                                       Integer estoque) {
}
