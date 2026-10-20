package com.raizes.validacoes;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
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
class ValidadorUsuarioTest {

    @InjectMocks
    private ValidadorUsuario validador;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private DadosCadastroPedido dados;

    @Nested
    class ValidarUsuarioExistente{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Validaria usuario existente")
            void test(){
                given(repository.existsById(dados.idUsuario())).willReturn(true);
                Assertions.assertDoesNotThrow(() -> validador.validar(dados));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não validaria usuario inexistente")
            void test(){
                given(repository.existsById(dados.idUsuario())).willReturn(false);
                Assertions.assertThrows(EntityNotFoundException.class, () -> validador.validar(dados));
            }
        }
    }
}