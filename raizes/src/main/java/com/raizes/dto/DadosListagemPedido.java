package com.raizes.dto;

import com.raizes.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DadosListagemPedido(UUID id,
                                  Integer numero,
                                  LocalDateTime horario,
                                  Status status,
                                  List<DadosItemPedido> itens,
                                  Usuario usuario,
                                  CanalPedido canalPedido,
                                  FormaPagamento formaPagamento,
                                  StatusPagamento statusPagamento,
                                  BigDecimal valorTotal) {
    public DadosListagemPedido(Pedido pedido) {
        this(pedido.getId(), pedido.getNumero(), pedido.getHorario(),
                pedido.getStatus(), pedido.getItens().stream()
                        .map(item -> new DadosItemPedido(
                                item.getProduto() != null ? item.getProduto().getId() : null,
                                item.getProduto() != null ? item.getProduto().getNome() : null,
                                item.getQuantidade()
                        ))
                        .toList(), pedido.getUsuario(), pedido.getCanalPedido(),
                pedido.getFormaPagamento(), pedido.getStatusPagamento(), pedido.getValorTotal());
    }
}
