package com.raizes.dto;

import com.raizes.model.StatusPagamento;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ConfirmacaoPagamentoDTO(@NotNull UUID idProduto,
        StatusPagamento statusPagamento) {

}
