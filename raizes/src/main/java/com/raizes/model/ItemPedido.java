package com.raizes.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "ItemPedido")
@Table(name  = "item_pedidos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Positive
    private BigDecimal valorUnitario;

    @NotNull
    @Positive
    private Integer quantidade;

    private String descricao;

    @ManyToOne(optional=false)
    @JoinColumn(name = "pedido_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cardapio produto;

    public ItemPedido(Cardapio produto, Integer quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.descricao = produto.getDescricao();
        this.valorUnitario = produto.getPreco();
    }

    public BigDecimal getSubtotal() {
        return valorUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void vincularPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
