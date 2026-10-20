package com.raizes.validacoes;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import com.raizes.repository.PedidoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ValidadorPedidoDuplicadoTest {

    @InjectMocks
    private ValidadorPedidoDuplicado validador;

    @Mock
    private PedidoRepository repository;

    @Mock
    private DadosCadastroPedido dados;

    @Nested
    class ValidarPedidoDuplicado {

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Validaria pedido unico")
            void test(){
                given(repository.existsByNumero(dados.numero())).willReturn(false);
                Assertions.assertDoesNotThrow(() -> validador.validar(dados));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não validaria pedido duplicado")
            void test(){
                given(repository.existsByNumero(dados.numero())).willReturn(true);
                Assertions.assertThrows(PedidoException.class, () -> validador.validar(dados));
            }
        }
    }
}