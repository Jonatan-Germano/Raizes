package com.raizes.validacoes;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import com.raizes.repository.PedidoRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadorPedidoDuplicado implements ValidadorPedido {

    private final PedidoRepository repository;

    public ValidadorPedidoDuplicado(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(DadosCadastroPedido dados) {
        if(repository.existsByNumero(dados.numero())) {
            throw new PedidoException("Pedido duplicado");
        }
    }
}
