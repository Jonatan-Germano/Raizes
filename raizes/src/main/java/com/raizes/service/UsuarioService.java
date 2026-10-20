package com.raizes.service;

import com.raizes.dto.DadosAtualizacaoUsuario;
import com.raizes.dto.DadosCadastroUsuario;
import com.raizes.dto.DadosExclusaoUsuario;
import com.raizes.dto.DadosListagemUsuario;
import com.raizes.exception.PedidoException;
import com.raizes.model.Usuario;
import com.raizes.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DadosListagemUsuario cadastrar(DadosCadastroUsuario dados) {
        if(repository.existsByEmailOrTelefone(dados.email(), dados.telefone())){
            throw new PedidoException("Usuario com este email e/ou telefone já cadastrado");
        }
        Usuario usuario = new Usuario(dados);
        repository.save(usuario);
        return new DadosListagemUsuario(usuario);
    }

    public DadosListagemUsuario obter(UUID id){
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário com o ID " + id + " não foi encontrado."));;
        return new DadosListagemUsuario(usuario);
    }

    @Transactional
    public DadosListagemUsuario atualizar(DadosAtualizacaoUsuario dados){
        Usuario usuario = repository.findById(dados.id())
                .orElseThrow(EntityNotFoundException::new);
        usuario.atualizar(dados);
        return new DadosListagemUsuario(usuario);
    }

    @Transactional
    public void excluir(DadosExclusaoUsuario dados){
        repository.findById(dados.id())
                        .orElseThrow(EntityNotFoundException::new);
        repository.deleteById(dados.id());
    }
}
