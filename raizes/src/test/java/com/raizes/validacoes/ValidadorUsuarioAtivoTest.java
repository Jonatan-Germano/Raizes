package com.raizes.validacoes;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.exception.PedidoException;
import com.raizes.repository.UsuarioRepository;
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
class ValidadorUsuarioAtivoTest {

    @InjectMocks
    private ValidadorUsuarioAtivo validador;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private DadosCadastroPedido dados;

    @Nested
    class ValidarUsuarioAtivo{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("deve validar usuario ativo")
            void teste1() {
                given(repository.findAtivoById(dados.idUsuario())).willReturn(true);
                Assertions.assertDoesNotThrow(() -> validador.validar(dados));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não deve validar usuario ativo")
            void teste2() {
                given(repository.findAtivoById(dados.idUsuario())).willReturn(false);
                Assertions.assertThrows(PedidoException.class, () -> validador.validar(dados));
            }
        }
    }
}