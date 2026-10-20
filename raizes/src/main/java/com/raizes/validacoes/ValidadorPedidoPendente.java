package com.raizes.validacoes;

import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import com.raizes.model.StatusPagamento;
import com.raizes.repository.PedidoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPedidoPendente implements ValidadorPedido {

    private final PedidoRepository repository;

    public ValidadorPedidoPendente(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(DadosCadastroPedido dados) {
//        boolean existePedidoPendente =
//                repository.existsByUsuarioIdAndStatusPagamento(
//                        dados.idUsuario(),
//                        StatusPagamento.AGUARDANDO
//                );

        if (repository.existsByUsuarioIdAndStatusPagamento(
                dados.idUsuario(),
                StatusPagamento.AGUARDANDO)) {
            throw new PedidoException(
                    "O usuário já possui um pedido aguardando pagamento"
            );
        }
    }
}
