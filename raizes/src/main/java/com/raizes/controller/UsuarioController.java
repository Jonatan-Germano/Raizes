package com.raizes.controller;
import com.raizes.dto.DadosAtualizacaoUsuario;
import com.raizes.dto.DadosCadastroUsuario;
import com.raizes.dto.DadosExclusaoUsuario;
import com.raizes.dto.DadosListagemUsuario;
import com.raizes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosListagemUsuario> cadastrar(@RequestBody @Valid DadosCadastroUsuario dados, UriComponentsBuilder uriBuilder) {
        DadosListagemUsuario usuario = service.cadastrar(dados);
        URI uri = uriBuilder.path("/usuarios/{id}").buildAndExpand(usuario.id()).toUri();
        return ResponseEntity.created(uri).body(usuario);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DadosListagemUsuario> obter(@PathVariable UUID id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PutMapping
    public ResponseEntity<DadosListagemUsuario> atualizar(@RequestBody @Valid DadosAtualizacaoUsuario dados) {
        return ResponseEntity.ok(service.atualizar(dados));
    }

    @DeleteMapping
    public ResponseEntity excluir(@RequestBody @Valid DadosExclusaoUsuario dados) {
        service.excluir(dados);
        return ResponseEntity.noContent().build();
    }
}
