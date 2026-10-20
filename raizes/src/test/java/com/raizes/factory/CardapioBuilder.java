package com.raizes.factory;

import com.raizes.dto.DadosCadastroCardapio;
import com.raizes.model.Cardapio;
import java.math.BigDecimal;

public class CardapioBuilder {

    private CardapioBuilder() {
    }

    public static Cardapio build( String nome, String descricao, BigDecimal preco, String categoria, Integer estoque, Boolean ativo) {
        DadosCadastroCardapio dados =
                new DadosCadastroCardapio(nome, descricao, preco, categoria, estoque);

        return new Cardapio(dados);
    }

    public static Cardapio pratoAtivo() {
        return build( "Prato", "descricao", BigDecimal.valueOf(1), "categoria",  2, true);
    }

    public static Cardapio pratoInativo() {
        Cardapio cardapio = pratoAtivo();
        cardapio.inativar();
        return cardapio;
    }
}
