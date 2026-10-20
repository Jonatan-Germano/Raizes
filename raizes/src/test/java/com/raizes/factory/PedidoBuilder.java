package com.raizes.factory;

import com.raizes.dto.DadosCadastroPedido;
import com.raizes.dto.DadosItemPedido;
import com.raizes.dto.DadosListagemPedido;
import com.raizes.dto.DadosPagamento;
import com.raizes.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PedidoBuilder {

    public static Pedido build(Usuario usuario, Cardapio cardapio) {
        UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        DadosItemPedido item = new DadosItemPedido(
                idProduto,
                "Prato",
                2
        );
        List<ItemPedido> itens = new ArrayList<>();
        ItemPedido item2 = new ItemPedido(
                cardapio,
                item.quantidade()
        );
        itens.add(item2);
        DadosListagemPedido dadosListagem = new DadosListagemPedido(
                id,
                1,
                LocalDateTime.now(),
                Status.AGUARDANDO_PAGAMENTO,
                List.of(item),
                usuario,
                CanalPedido.WEB,
                FormaPagamento.CARTAO,
                StatusPagamento.AGUARDANDO,
                BigDecimal.valueOf(1)
        );
        return new Pedido(
                dadosListagem.numero(),
                usuario,
                dadosListagem.canalPedido(),
                itens,
                dadosListagem.formaPagamento()
        );
    }

    public static Pedido pedido1(Usuario usuario, Cardapio cardapio) {
        return build(usuario, cardapio);
    }

    public static Pedido pedido2(Usuario usuario, Cardapio cardapio) {
        return build(usuario, cardapio);
    }

    public static DadosCadastroPedido pedidoDados(Usuario usuario) {
        UUID produtoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
        DadosItemPedido item = new DadosItemPedido(
                produtoId,
                "Prato",
                2
        );
        DadosPagamento DadosPagamento = new DadosPagamento(
                FormaPagamento.CARTAO
        );
        DadosCadastroPedido pedidoDados = new DadosCadastroPedido(
                1,
                usuario.getId(),
                CanalPedido.WEB,
                List.of(item),
                DadosPagamento
        );
        return pedidoDados;
    }
}
