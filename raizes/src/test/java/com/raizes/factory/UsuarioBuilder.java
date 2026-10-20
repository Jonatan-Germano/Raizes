package com.raizes.factory;

import com.raizes.dto.DadosCadastroUsuario;
import com.raizes.model.Usuario;

public class UsuarioBuilder {

    private UsuarioBuilder() {
    }

    public static Usuario build(String nome, String email, String telefone, Boolean ativo) {
        DadosCadastroUsuario dados =
                new DadosCadastroUsuario(nome, email, telefone);

        return new Usuario(dados);
    }

    public static Usuario usuarioAtivo() {
        return build("João", "joao@email.com", "11223344", true);
    }

    public static Usuario usuarioAtivo2() {
        return build("Alguém", "alguem@email.com", "03009090",  true);
    }
}
