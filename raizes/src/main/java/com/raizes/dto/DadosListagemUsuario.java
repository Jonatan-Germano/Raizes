package com.raizes.dto;

import com.raizes.model.Usuario;
import java.util.UUID;

public record DadosListagemUsuario(UUID id,
                                   String nome,
                                   String email,
                                   String telefone,
                                   Boolean ativo){
    public DadosListagemUsuario(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),  usuario.getAtivo());
    }
}
