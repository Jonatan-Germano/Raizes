package com.raizes.repository;
import com.raizes.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import static com.raizes.factory.UsuarioBuilder.build;
import static com.raizes.factory.UsuarioBuilder.usuarioAtivo;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Nested
    class FindAtivoById {

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Retornaria true para usuario ativo")
            void test(){
                Usuario usuario = repository.save(usuarioAtivo());
                Boolean ativo = repository.findAtivoById(usuario.getId());
                assertNotNull(ativo);
                assertTrue(ativo);
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("retornaria null quando usuario não existir")
            void test(){
                Boolean ativo = repository.findAtivoById(usuarioAtivo().getId());
                assertNull(ativo);
            }
        }
    }

    @Nested
    class ExistsByEmailOrTelefone{
        @Nested
        class Sucesso{

            @Test
            @DisplayName("retornaria false quando email/tel não existir")
            void test(){
                repository.save(usuarioAtivo());
                assertFalse(repository.existsByEmailOrTelefone("email@email.com","99887766"));
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("retornaria true quando email/tel  existir")
            void test(){
                repository.save(usuarioAtivo());
                assertTrue(repository.existsByEmailOrTelefone("joao@email.com","11223344"));
            }
        }
    }

    @Nested
    class EmailOuTelDuplicado{

        @Nested
        class Falha{

            @Test
            @DisplayName("Erro de email duplicado")
            void test1(){
                repository.save(usuarioAtivo());
                Usuario outro = build("Maria", "joao@email.com", "99887766", true);
                assertThrows(DataIntegrityViolationException.class, () -> {
                    repository.saveAndFlush(outro);
                });
            }

            @Test
            @DisplayName("Erro de telefone duplicado")
            void test2(){
                repository.save(usuarioAtivo());
                Usuario outro = build("Maria", "maria@email.com", "11223344", true);
                assertThrows(DataIntegrityViolationException.class, () -> {
                    repository.saveAndFlush(outro);
                });
            }
        }
    }
}