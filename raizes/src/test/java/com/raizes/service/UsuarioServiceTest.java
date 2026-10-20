package com.raizes.service;
import com.raizes.dto.DadosAtualizacaoUsuario;
import com.raizes.dto.DadosCadastroUsuario;
import com.raizes.dto.DadosExclusaoUsuario;
import com.raizes.dto.DadosListagemUsuario;
import com.raizes.exception.PedidoException;
import com.raizes.model.Usuario;
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
import java.util.Optional;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService service;

    @Mock
    private UsuarioRepository repository;

    @Mock
    private DadosCadastroUsuario dados;

    @Mock
    private Usuario usuario;

    @Mock
    private DadosAtualizacaoUsuario atualizacao;

    @Nested
    class CadastrarUsuario{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Cadastraria usuario")
            void test(){
                DadosCadastroUsuario dadosCadastro =
                        new DadosCadastroUsuario(
                                "usuario",
                                "usuario@email.com",
                                "11223344"
                        );
                given(repository.existsByEmailOrTelefone(dadosCadastro.email(), dadosCadastro.telefone())).willReturn(false);
                Assertions.assertDoesNotThrow(() -> service.cadastrar(dadosCadastro));
                then(repository).should().save(any(Usuario.class));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não cadastraria usuario com email/telefone duplicado")
            void test(){
                given(repository.existsByEmailOrTelefone(dados.email(), dados.telefone())).willReturn(true);
                Assertions.assertThrows(PedidoException.class, () -> service.cadastrar(dados));
            }
        }
    }

    @Nested
    class ObterUsuario{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Obteria usuario")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                given(repository.findById(id)).willReturn(Optional.of(usuario));
                Assertions.assertDoesNotThrow(() -> service.obter(id));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não obtem usuario")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                given(repository.findById(id)).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.obter(id));
            }
        }
    }

    @Nested
    class AtualizarUsuario{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Atualizaria usuario")
            void test(){
                given(repository.findById(atualizacao.id())).willReturn(Optional.of(usuario));
                Assertions.assertDoesNotThrow(() -> service.atualizar(atualizacao));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não atualizaria usuario")
            void test(){
                given(repository.findById(atualizacao.id())).willReturn(Optional.empty());
                Assertions.assertThrows(EntityNotFoundException.class, () -> service.atualizar(atualizacao));
            }
        }
    }

    @Nested
    class ExcluirUsuario{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Excluiria usuario")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                DadosListagemUsuario dadoListagem =
                        new DadosListagemUsuario(
                                id,
                                "usuario",
                                "usuario@email.com",
                                "11223344",
                                true
                        );
                given(repository.findById(id)).willReturn(Optional.of(usuario));
                DadosExclusaoUsuario exclusao = new DadosExclusaoUsuario(dadoListagem.id());
                repository.deleteById(dadoListagem.id());
                Assertions.assertDoesNotThrow(() -> service.excluir(exclusao));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Não excluiria usuario não encontrado")
            void test(){
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                DadosListagemUsuario dadoListagem =
                        new DadosListagemUsuario(
                                id,
                                "usuario",
                                "usuario@email.com",
                                "11223344",
                                true
                        );
                DadosExclusaoUsuario exclusao = new DadosExclusaoUsuario(dadoListagem.id());
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