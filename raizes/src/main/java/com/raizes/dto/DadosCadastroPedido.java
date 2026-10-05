package com.raizes.dto;
import com.raizes.model.CanalPedido;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record DadosCadastroPedido(@NotNull Integer numero,
                                  @NotNull UUID idUsuario,
                                  @NotNull CanalPedido canalPedido,
                                  List<DadosItemPedido> itens,
                                  DadosPagamento pagamento
                                  ) {
}
