package com.raizes.dto;

import com.raizes.model.Cardapio;

import java.math.BigDecimal;
import java.util.UUID;

public record DadosListagemCardapio(UUID id,
                                    String nome,
                                    String descricao,
                                    BigDecimal preco,
                                    String categoria,
                                    Boolean ativo,
                                    Integer estoque) {
    public DadosListagemCardapio(Cardapio cardapio) {
        this(cardapio.getId(), cardapio.getNome(), cardapio.getDescricao(),
                cardapio.getPreco(), cardapio.getCategoria(), cardapio.getAtivo()
            , cardapio.getEstoque());
    }
}
