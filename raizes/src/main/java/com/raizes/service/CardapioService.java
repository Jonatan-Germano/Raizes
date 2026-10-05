package com.raizes.service;

import com.raizes.dto.DadosAtualizacaoCardapio;
import com.raizes.dto.DadosCadastroCardapio;
import com.raizes.dto.DadosExclusaoCardapio;
import com.raizes.dto.DadosListagemCardapio;
import com.raizes.exception.PedidoException;
import com.raizes.model.Cardapio;
import com.raizes.repository.CardapioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardapioService {

    private final CardapioRepository repository;

    public CardapioService(CardapioRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DadosListagemCardapio cadastrar(DadosCadastroCardapio dados) {
        if(repository.existsByNome(dados.nome())){
            throw new PedidoException("Prato com este nome já cadastrado");
        }
        Cardapio cardapio = new Cardapio(dados);
        repository.save(cardapio);
        return new DadosListagemCardapio(cardapio);
    }

    public Page<DadosListagemCardapio> obter(Pageable pageable) {
        return repository.findAllByAtivoTrue(pageable).map(DadosListagemCardapio::new);
    }

    @Transactional
    public DadosListagemCardapio atualizar(DadosAtualizacaoCardapio dados) {
        Cardapio cardapio = repository.findById(dados.id())
                .orElseThrow(() -> new EntityNotFoundException("Prato não encontrado com o ID: " + dados.id()));
        cardapio.atualizar(dados);
        return new DadosListagemCardapio(cardapio);
    }

    @Transactional
    public void excluir(DadosExclusaoCardapio dados) {
        if(!repository.existsById(dados.id())){
            throw new EntityNotFoundException("Prato não encontrado com o ID: " + dados.id());
        }
        repository.deleteById(dados.id());
    }
}
