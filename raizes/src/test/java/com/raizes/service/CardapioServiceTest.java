package com.raizes.service;
import com.raizes.dto.DadosAtualizacaoCardapio;
import com.raizes.dto.DadosCadastroCardapio;
import com.raizes.dto.DadosExclusaoCardapio;
import com.raizes.dto.DadosListagemCardapio;
import com.raizes.exception.PedidoException;
import com.raizes.model.Cardapio;
import com.raizes.repository.CardapioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @InjectMocks
    private CardapioService service;

    @Mock
    private CardapioRepository repository;

    @Mock
    private Cardapio cardapio;

    @Mock
    private DadosCadastroCardapio dados;

    @Mock
    private DadosAtualizacaoCardapio atualizacao;

    @Nested
    class CadastrarPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Cadastraria prato")
            void test(){
                DadosCadastroCardapio dadosCadastro =
                        new DadosCadastroCardapio(
                                "prato",
                                "descricap",
                                BigDecimal.valueOf(1),
                                "categoria",
                                2
                        );
                given(repository.existsByNome(dadosCadastro.nome())).willReturn(false);
                Assertions.assertDoesNotThrow(() -> service.cadastrar(dadosCadastro));
                then(repository).should().save(any(Cardapio.class));

            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não cadastraria prato com nome duplicado")
            void test(){
                given(repository.existsByNome(dados.nome())).willReturn(true);
                Assertions.assertThrows(PedidoException.class, () -> service.cadastrar(dados));

            }
        }
    }

    @Nested
    class ObterCardapio{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Listaria pratos ativo true")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
                Cardapio prato = new Cardapio(dados);
                Page<Cardapio> page = new PageImpl<>(List.of(prato));
                given(repository.findAllByAtivoTrue(pageable)).willReturn(page);

                Page body = service.obter(pageable);

                assertNotNull(body);
                assertEquals(1, body.getTotalElements());
                then(repository).should().findAllByAtivoTrue(pageable);
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Lista vazia")
            void test(){
                Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
                given(repository.findAllByAtivoTrue(pageable)).willReturn(Page.empty());

                Page body = service.obter(pageable);

                assertEquals(Page.empty(), body);
            }
        }
    }

    @Nested
    class AtualizarPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Atualizaria prato")
            void test(){
                given(repository.findById(atualizacao.id())).willReturn(Optional.of(cardapio));
                Assertions.assertDoesNotThrow(() -> service.atualizar(atualizacao));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não atualizaria prato")
            void test(){
                given(repository.findById(atualizacao.id())).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.atualizar(atualizacao));
            }
        }
    }

    @Nested
    class ExcluirPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Excluiria prato")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                DadosListagemCardapio dadoListagem =
                        new DadosListagemCardapio(
                                id,
                                "prato",
                                "descricao",
                                BigDecimal.valueOf(1),
                                "categoria",
                                true,
                                2
                        );
                given(repository.findById(id)).willReturn(Optional.of(cardapio));
                DadosExclusaoCardapio exclusao = new DadosExclusaoCardapio(dadoListagem.id());
                repository.deleteById(dadoListagem.id());
                Assertions.assertDoesNotThrow(() -> service.excluir(exclusao));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não Excluiria prato não encontrado")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                DadosListagemCardapio dadoListagem =
                        new DadosListagemCardapio(
                                id,
                                "prato",
                                "descricao",
                                BigDecimal.valueOf(1),
                                "categoria",
                                true,
                                2
                        );
                DadosExclusaoCardapio exclusao = new DadosExclusaoCardapio(dadoListagem.id());
                given(repository.findById(dadoListagem.id())).willReturn(Optional.empty());
                Assertions.assertThrows(
                        EntityNotFoundException.class,
                        () -> service.excluir(exclusao)
                );
                then(repository).should(never()).deleteById(dadoListagem.id());
            }
        }
    }
}