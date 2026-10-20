package com.raizes.repository;
import com.raizes.factory.CardapioBuilder;
import com.raizes.model.Cardapio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import static com.raizes.factory.CardapioBuilder.pratoAtivo;
import static com.raizes.factory.CardapioBuilder.pratoInativo;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class CardapioRepositoryTest {

    @Autowired
    private CardapioRepository repository;

    @Nested
    class ExistsByNome{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("retornaria false para prato existente")
            void test(){
                repository.save(pratoAtivo());
                assertFalse(repository.existsByNome("coisa"));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("retornaria true para prato existente")
            void test(){
                repository.save(pratoAtivo());
                assertTrue(repository.existsByNome("Prato"));
            }
        }
    }

    @Nested
    class FindAllByAtivoTrue{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Retornaria prato Ativo")
            void test1() {
                repository.save(pratoAtivo());
                repository.save(pratoInativo());

                Page<Cardapio> pagina =
                        repository.findAllByAtivoTrue(PageRequest.of(0, 10));

                assertEquals(1, pagina.getTotalElements());
                assertTrue(pagina.getContent().get(0).getAtivo());
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("pagina vazia")
            void test2() {
                repository.save(pratoInativo());

                Page<Cardapio> pagina =
                        repository.findAllByAtivoTrue(PageRequest.of(0, 10));

                assertTrue(pagina.isEmpty());
            }
        }
    }

    @Nested
    class PaginacaoCorreta{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("paginacao correta")
            void test() {
                repository.save(CardapioBuilder.build("Nome1", "descricao1", BigDecimal.valueOf(2),
                        "categoria1",2,true));
                repository.save(CardapioBuilder.build("Nome2", "descricao2", BigDecimal.valueOf(2),
                        "categoria2",4,true));
                repository.save(CardapioBuilder.build("Nome3", "descricao3", BigDecimal.valueOf(2),
                        "categoria3",6,true));

                Page<Cardapio> pagina =
                        repository.findAllByAtivoTrue(PageRequest.of(0, 2));

                assertEquals(2, pagina.getContent().size());
                assertEquals(3, pagina.getTotalElements());
            }
        }
    }
}