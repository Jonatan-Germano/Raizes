package com.raizes.model;

import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "Pedido")
@Table(name = "pedidos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(unique = true)
    private Integer numero;

    @NotNull
    private LocalDateTime horario;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(cascade =  CascadeType.PERSIST,  fetch = FetchType.LAZY, mappedBy = "pedido")
    private List<ItemPedido> itens = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Usuario usuario;

    @Column(name = "forma_pagamento")
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Column(name = "status_pagamento")
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento;

    @NotNull
    @Positive
    private BigDecimal valorTotal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_pedido")
    private CanalPedido canalPedido;

    public Pedido(Integer numero, Usuario usuario, CanalPedido canalPedido, List<ItemPedido> itens, FormaPagamento formaPagamento) {
        this.numero = numero;
        this.usuario = usuario;
        this.canalPedido = canalPedido;
        this.itens = new ArrayList<>(itens);
        this.statusPagamento = StatusPagamento.AGUARDANDO;
        this.formaPagamento = formaPagamento;
        this.horario = LocalDateTime.now();
        this.status = Status.AGUARDANDO_PAGAMENTO;

        for (ItemPedido item : this.itens) {
            item.vincularPedido(this);
        }
        this.valorTotal = calcularTotal();
    }

    public Pedido(@Valid DadosCadastroPedido dados) {
        this.numero = dados.numero();

    }

    private BigDecimal calcularTotal() {
        return itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    public void autorizarPagamento(
            StatusPagamento statusPagamento) {

        if (this.statusPagamento == null) {
            throw new PedidoException(
                    "Pedido não possui pagamento"
            );
        }
        if (this.status == Status.CANCELADO){
            throw new PedidoException("Este pedido foi cancelado");
        }
//        if (this.status == Status.PAGO) {
//            throw new PedidoException("Este pedido já foi pago");
//        }

        this.statusPagamento = statusPagamento;
        if(this.statusPagamento == StatusPagamento.PAGO) {
            this.status = Status.PAGO;
        }
    }

    public void cancelar() {
        this.status = Status.CANCELADO;
    }
}
