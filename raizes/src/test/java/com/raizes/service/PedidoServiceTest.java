package com.raizes.service;
import com.raizes.dto.*;
import com.raizes.exception.PedidoException;
import com.raizes.factory.PedidoBuilder;
import com.raizes.model.*;
import com.raizes.repository.CardapioRepository;
import com.raizes.repository.PedidoRepository;
import com.raizes.repository.UsuarioRepository;
import com.raizes.validacoes.ValidadorPedido;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static com.raizes.factory.CardapioBuilder.pratoAtivo;
import static com.raizes.factory.PedidoBuilder.pedido1;
import static com.raizes.factory.PedidoBuilder.pedidoDados;
import static com.raizes.factory.UsuarioBuilder.usuarioAtivo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @InjectMocks
    private PedidoService service;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private DadosCadastroPedido dados;

    @Mock
    private Pedido pedido;

    @Spy
    private List<ValidadorPedido> validadores = new ArrayList<>();

    @Mock
    private ValidadorPedido validador;

    @Nested
    class CadastrarPedido{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Cadastraria pedido")
            void test(){
                Usuario usuario = usuarioAtivo();
                Cardapio cardapio = pratoAtivo();
                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();

                given(cardapioRepository.findById(item.id())).willReturn(Optional.of(cardapio));
                assertThat(cardapio.getEstoque() > 0);
                assertThat(item.quantidade() <= cardapio.getEstoque());
                given(usuarioRepository.getReferenceById(usuario.getId())).willReturn(usuario);
                service.cadastrar(pedido);
                then(pedidoRepository).should().save(any(Pedido.class));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não cadastraria pedido com produto não encontrado")
            void test(){
                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();
                given(cardapioRepository.findById(item.id())).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.cadastrar(pedido));
            }

            @Test
            @DisplayName("Não cadastraria com estoque menor que 0")
            void test2(){
                Cardapio cardapio = pratoAtivo();
                setField(cardapio, "estoque", 0);

                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();
                given(cardapioRepository.findById(item.id())).willReturn(Optional.of(cardapio));
                assertThat(cardapio.getEstoque() == 0);
                Assertions.assertThrows(PedidoException.class, () -> service.cadastrar(pedido));
            }

            @Test
            @DisplayName("Não cadastraria com estoque menor que quantidade")
            void test3(){
                Cardapio cardapio = pratoAtivo();
                setField(cardapio, "estoque", 1);

                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();

                given(cardapioRepository.findById(item.id())).willReturn(Optional.of(cardapio));
                assertThat(cardapio.getEstoque() > 0);
                assertThat(item.quantidade() > cardapio.getEstoque());
                Assertions.assertThrows(PedidoException.class, () -> service.cadastrar(pedido));
            }
        }
    }

    @Nested
    class AutorizarPagamento{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Autorizaria Pagamemto")
            void test(){
                UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                ConfirmacaoPagamentoDTO dadosAtualizacao = new ConfirmacaoPagamentoDTO(
                        idProduto,
                        StatusPagamento.PAGO
                );
                Cardapio cardapio = pratoAtivo();
                Pedido pedido = pedido1(usuarioAtivo(), pratoAtivo());

                given(pedidoRepository.findById(pedido.getId())).willReturn(Optional.of(pedido));
                given(cardapioRepository.findById(idProduto)).willReturn(Optional.of(cardapio));
                pedido.autorizarPagamento(dadosAtualizacao.statusPagamento());
                assertEquals(StatusPagamento.PAGO, dadosAtualizacao.statusPagamento());
                cardapio.atualizarEstoque(pedido.getItens().getFirst().getQuantidade());
                Assertions.assertDoesNotThrow(() -> service.autorizarPagamento(pedido.getId(), dadosAtualizacao));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não Autorizaria pedido nao encontrado")
            void test(){
                UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                ConfirmacaoPagamentoDTO dadosAtualizacao = new ConfirmacaoPagamentoDTO(
                        idProduto,
                        StatusPagamento.PAGO
                );
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.autorizarPagamento(pedido.getId(), dadosAtualizacao));

            }

            @Test
            @DisplayName("Não Autorizaria prato nao encontrado")
            void test2(){
                UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                ConfirmacaoPagamentoDTO dadosAtualizacao = new ConfirmacaoPagamentoDTO(
                        idProduto,
                        StatusPagamento.PAGO
                );
                given(pedidoRepository.findById(pedido.getId())).willReturn(Optional.of(pedido));
                given(cardapioRepository.findById(idProduto)).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.autorizarPagamento(pedido.getId(), dadosAtualizacao));
            }

            @Test
            @DisplayName("Recusaria pagamento")
            void test3(){
                UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                ConfirmacaoPagamentoDTO dadosAtualizacao = new ConfirmacaoPagamentoDTO(
                        idProduto,
                        StatusPagamento.RECUSADO
                );
                given(pedidoRepository.findById(pedido.getId())).willReturn(Optional.of(pedido));
                pedido.autorizarPagamento(dadosAtualizacao.statusPagamento());
                assertEquals(StatusPagamento.RECUSADO, dadosAtualizacao.statusPagamento());
                Assertions.assertThrows(PedidoException.class, () -> service.autorizarPagamento(pedido.getId(), dadosAtualizacao));
            }
        }
    }

    @Nested
    class ListarPedido{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Listaria pedidos")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("numero"));
                Pedido pedido = new Pedido(dados);
                Page<Pedido> page = new PageImpl<>(List.of(pedido));
                given(pedidoRepository.findAll(pageable)).willReturn(page);
                Page<DadosListagemPedido> body = service.listar(pageable);

                assertNotNull(body);
                assertEquals(1, body.getTotalElements());
                then(pedidoRepository).should().findAll(pageable);
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Lista vazia")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("numero"));
                given(pedidoRepository.findAll(pageable)).willReturn(Page.empty());
                Page<DadosListagemPedido> body = service.listar(pageable);

                assertEquals(Page.empty(), body);
            }
        }
    }

    @Nested
    class FiltrarCanalPedido{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Listaria por canal")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("numero"));
                Pedido pedido = new Pedido(dados);
                Page<Pedido> page = new PageImpl<>(List.of(pedido));
                given(pedidoRepository.findAllByCanalPedido(CanalPedido.WEB, pageable)).willReturn(page);
                Page<DadosListagemPedido> body = service.filtrarCanal(CanalPedido.WEB, pageable);
                assertNotNull(body);
                assertEquals(1, body.getTotalElements());
                then(pedidoRepository).should().findAllByCanalPedido(CanalPedido.WEB, pageable);
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Lista vazia por canal")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("numero"));
                given(pedidoRepository.findAllByCanalPedido(CanalPedido.APP,pageable)).willReturn(Page.empty());
                Page<DadosListagemPedido> body = service.filtrarCanal(CanalPedido.APP, pageable);
                assertEquals(Page.empty(), body);
            }
        }
    }

    @Nested
    class CancelarPedido{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Cancelaria pedido")
            void test(){
                Pedido pedido1 = PedidoBuilder.build(usuarioAtivo(), pratoAtivo());
                DadosCancelamentoPedido cancelamento = new DadosCancelamentoPedido(pedido1.getId());
                given(pedidoRepository.findById(pedido1.getId())).willReturn(Optional.of(pedido));
                pedido.cancelar();
                Assertions.assertDoesNotThrow(() -> service.cancelar(cancelamento));

            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não cancelaria pedido não encontrado")
            void test(){
                Pedido pedido1 = PedidoBuilder.build(usuarioAtivo(), pratoAtivo());
                DadosCancelamentoPedido cancelamento = new DadosCancelamentoPedido(pedido1.getId());
                given(pedidoRepository.findById(pedido1.getId())).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.cancelar(cancelamento));
                then(pedido).should(never()).cancelar();
            }
        }
    }

    @Nested
    class ChamarValidadores{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Chamaria validadores")
            void test(){
                Cardapio cardapio = pratoAtivo();
                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();
                given(cardapioRepository.findById(item.id())).willReturn(Optional.of(cardapio));
                validadores.add(validador);
                service.cadastrar(pedido);
                then(validador).should().validar(pedido);
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não chamaria validadores")
            void test(){
                DadosCadastroPedido pedido = pedidoDados(usuarioAtivo());
                DadosItemPedido item = pedido.itens().getFirst();
                given(cardapioRepository.findById(item.id())).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.cadastrar(pedido));
            }

            @Test
            @DisplayName("Não salva com erro de validação")
            void test3(){
                validadores.add(validador);
                willThrow(new PedidoException("Pedido duplicado"))
                        .given(validador)
                        .validar(dados);
                Assertions.assertThrows(PedidoException.class, () -> validador.validar(dados));
                then(pedidoRepository).should(never()).save(any());
            }
        }
    }
}
