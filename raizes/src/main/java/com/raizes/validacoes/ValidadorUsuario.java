package com.raizes.validacoes;

import com.raizes.dto.DadosCadastroPedido;
import com.raizes.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class ValidadorUsuario implements ValidadorPedido {

    private final UsuarioRepository repository;

    public ValidadorUsuario(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public void validar(DadosCadastroPedido dados) {
        if(!repository.existsById(dados.idUsuario())){
            throw new EntityNotFoundException("Usuario não encontrado");
        }
    }
}
