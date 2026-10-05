package com.raizes.validacoes;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import com.raizes.repository.UsuarioRepository;
import org.springframework.stereotype.Component;

@Component
public class ValidadoUsuarioAtivo implements ValidadorPedido {

    private final UsuarioRepository repository;

    public ValidadoUsuarioAtivo(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(DadosCadastroPedido dados) {
        if(!repository.findAtivoById(dados.idUsuario())){
            throw new PedidoException("Usuario com conta inativa");
        }
    }
}
