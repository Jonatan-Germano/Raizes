package com.raizes.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DadosCancelamentoPedido(@NotNull UUID id) {
}
