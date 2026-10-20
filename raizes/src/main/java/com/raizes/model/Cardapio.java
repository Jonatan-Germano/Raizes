package com.raizes.model;
import com.raizes.dto.DadosAtualizacaoCardapio;
import com.raizes.dto.DadosCadastroCardapio;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "Cardapio")
@Table(name = "produtos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Cardapio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank
    @Column(unique = true)
    private String nome;

    private String descricao;

    @NotNull
    @Positive
    private BigDecimal preco;

    private String categoria;

    private Boolean ativo;

    @PositiveOrZero
    @NotNull
    private Integer estoque;

    public Cardapio(@Valid DadosCadastroCardapio dados) {
        this.nome = dados.nome();
        this.descricao = dados.descricao();
        this.preco = dados.preco();
        this.categoria = dados.categoria();
        this.estoque = dados.estoque();
        this.ativo = true;
    }

    public Cardapio(String nome, String descricao, BigDecimal bigDecimal, String categoria, Integer estoque, Boolean ativo) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = bigDecimal;
        this.categoria = categoria;
        this.estoque = estoque;
        this.ativo = ativo;
    }

    public void atualizar(@Valid DadosAtualizacaoCardapio dados) {
        if (dados.nome() != null) {
            this.nome = dados.nome();
        }
        if (dados.descricao() != null) {
            this.descricao = dados.descricao();
        }
        if (dados.preco() != null) {
            this.preco = dados.preco();
        }
        if (dados.categoria() != null) {
            this.categoria = dados.categoria();
        }
        if (dados.ativo() != null) {
            this.ativo = dados.ativo();
        }
        if (dados.estoque() != null) {
            this.estoque = dados.estoque();
        }
    }

    public void atualizarEstoque(Integer quantidade) {
        this.estoque = estoque - quantidade;
    }

    public void inativar() {
        this.nome = "Prato inativo";
        this.ativo = false;
    }
}