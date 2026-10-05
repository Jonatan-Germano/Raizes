package com.raizes.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DadosExclusaoCardapio(@NotNull UUID id) {
}
