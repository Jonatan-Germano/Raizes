package com.raizes.controller;
import com.raizes.dto.DadosAtualizacaoCardapio;
import com.raizes.dto.DadosCadastroCardapio;
import com.raizes.dto.DadosExclusaoCardapio;
import com.raizes.dto.DadosListagemCardapio;
import com.raizes.service.CardapioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;

@RestController
@RequestMapping("/cardapio")
public class CardapioController {
    
    private final CardapioService service;

    public CardapioController(CardapioService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosListagemCardapio> cadastrar(@RequestBody @Valid DadosCadastroCardapio dados, UriComponentsBuilder uriBuilder) {
        DadosListagemCardapio cardapio = service.cadastrar(dados);
        URI uri = uriBuilder.path("/cardapio/{id}").buildAndExpand(cardapio.id()).toUri();
        return ResponseEntity.created(uri).body(cardapio);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemCardapio>> obterCardapio(@PageableDefault (size = 10, sort = {"nome"}) Pageable pageable) {
        return ResponseEntity.ok(service.obter(pageable));
    }

    @PutMapping
    public ResponseEntity<DadosListagemCardapio> atualizar(@RequestBody @Valid DadosAtualizacaoCardapio dados) {
        return ResponseEntity.ok(service.atualizar(dados));
    }

    @DeleteMapping
    public ResponseEntity excluir(@RequestBody @Valid DadosExclusaoCardapio dados) {
        service.excluir(dados);
        return ResponseEntity.noContent().build();
    }
}
