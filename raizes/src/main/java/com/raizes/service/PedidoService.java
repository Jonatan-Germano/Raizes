package com.raizes.service;

import com.raizes.dto.*;
import com.raizes.exception.PedidoException;
import com.raizes.model.*;
import com.raizes.repository.CardapioRepository;
import com.raizes.repository.PedidoRepository;
import com.raizes.repository.UsuarioRepository;
import com.raizes.validacoes.ValidadorPedido;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CardapioRepository cardapioRepository;
    private final UsuarioRepository usuarioRepository;
    private final List<ValidadorPedido> validadores;

    public PedidoService(PedidoRepository pedidoRepository, CardapioRepository cardapioRepository, UsuarioRepository usuarioRepository, List<ValidadorPedido> validadores) {
        this.pedidoRepository = pedidoRepository;
        this.cardapioRepository = cardapioRepository;
        this.usuarioRepository = usuarioRepository;
        this.validadores = validadores;
    }

    @Transactional
    public DadosListagemPedido cadastrar(DadosCadastroPedido dados) {
        validadores.forEach(validador -> validador.validar(dados));
        List<ItemPedido> itens = new ArrayList<>();
        for (DadosItemPedido itemDTO : dados.itens()) {

            Cardapio prato = cardapioRepository.findById(
                    itemDTO.id()
            ).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Prato: " + itemDTO.nome() + " não encontrado: "
                                    + itemDTO.id()
                    )
            );
            if(prato.getEstoque() == 0 || itemDTO.quantidade() > prato.getEstoque()) {
                throw new PedidoException("Estoque insuficiente");
            }
            ItemPedido item = new ItemPedido(
                    prato,
                    itemDTO.quantidade()
            );
            itens.add(item);
        }
        Usuario usuario = usuarioRepository.getReferenceById(dados.idUsuario());
        Pedido pedido = new Pedido(
                dados.numero(),
                usuario,
                dados.canalPedido(),
                itens,
                dados.pagamento().forma()
        );
        pedidoRepository.save(pedido);
        return new DadosListagemPedido(pedido);
    }

    @Transactional
    public DadosListagemPedido autorizarPagamento(UUID id, ConfirmacaoPagamentoDTO dados) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Pedido não encontrado: " + id
                        )
                );
        pedido.autorizarPagamento(dados.statusPagamento());
        if(dados.statusPagamento().equals(StatusPagamento.PAGO)){
            Cardapio prato = cardapioRepository.findById(dados.idProduto()).orElseThrow(() ->
                    new EntityNotFoundException(
                            "Prato não encontrado: " + dados.idProduto()));
            prato.atualizarEstoque(pedido.getItens().getFirst().getQuantidade());
        }
        return new DadosListagemPedido(pedido);
    }

    public Page<DadosListagemPedido> listar(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(DadosListagemPedido::new);
    }

    public Page<DadosListagemPedido> filtrarCanal(CanalPedido canal, Pageable pageable) {
        return pedidoRepository.findAllByCanalPedido(canal, pageable).map(DadosListagemPedido::new);
    }

    @Transactional
    public void cancelar(DadosCancelamentoPedido dados) {
        Pedido pedido = pedidoRepository.findById(dados.id())
                .orElseThrow(EntityNotFoundException::new);
        pedido.cancelar();
    }
}
