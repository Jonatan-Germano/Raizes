package com.raizes.repository;
import com.raizes.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import static com.raizes.factory.CardapioBuilder.pratoAtivo;
import static com.raizes.factory.PedidoBuilder.pedido1;
import static com.raizes.factory.PedidoBuilder.pedido2;
import static com.raizes.factory.UsuarioBuilder.usuarioAtivo;
import static com.raizes.factory.UsuarioBuilder.usuarioAtivo2;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ActiveProfiles("test")
@DataJpaTest
class PedidoRepositoryTest {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Nested
    class ExistsByNumero{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("retornaria false para pedido existente")
            void test(){
               Usuario usuario = usuarioRepository.save(usuarioAtivo());
                Cardapio cardapio = cardapioRepository.save(pratoAtivo());
                repository.save(pedido1(usuario, cardapio));
                assertFalse(repository.existsByNumero(50));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("retornaria false para prato existente")
            void test(){
                Usuario usuario = usuarioRepository.save(usuarioAtivo());
                Cardapio cardapio = cardapioRepository.save(pratoAtivo());
                repository.save(pedido1(usuario, cardapio));
                assertTrue(repository.existsByNumero(1));
            }
        }
    }

    @Nested
    class FindAllByCanalPedido{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Filtraria por canalPedido")
            void test(){
                Usuario usuario = usuarioRepository.save(usuarioAtivo());
                Cardapio cardapio = cardapioRepository.save(pratoAtivo());
                repository.save(pedido1(usuario, cardapio));
                Page<Pedido> pagina =
                        repository.findAllByCanalPedido(CanalPedido.WEB, PageRequest.of(0, 10));

                assertEquals(1, pagina.getTotalElements());
                assertTrue(pagina.getContent().get(0).getCanalPedido().equals(CanalPedido.WEB));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Pagina vazia")
            void test(){
                Page<Pedido> pagina =
                        repository.findAllByCanalPedido(CanalPedido.WEB, PageRequest.of(0, 10));
                assertTrue(pagina.isEmpty());
            }
        }
    }

    @Nested
    class PaginacaoCorreta{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Paginaria corretamente")
            void test(){
                Usuario usuario = usuarioRepository.save(usuarioAtivo());
                Cardapio cardapio = cardapioRepository.save(pratoAtivo());
                repository.save(pedido1(usuario, cardapio));
                Usuario usuario2 = usuarioRepository.save(usuarioAtivo2());
                Pedido pedido = pedido2(usuario2, cardapio);
                setField(pedido, "numero", 2);
                setField(pedido, "canalPedido", CanalPedido.APP);
                repository.save(pedido);

                Page<Pedido> pagina =
                        repository.findAllByCanalPedido(CanalPedido.WEB, PageRequest.of(0, 1));

                assertEquals(1, pagina.getContent().size());
                assertEquals(1, pagina.getTotalElements());
            }
        }
    }
}
