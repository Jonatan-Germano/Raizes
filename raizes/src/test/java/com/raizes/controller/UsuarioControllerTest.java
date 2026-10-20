package com.raizes.controller;
import com.raizes.dto.DadosAtualizacaoUsuario;
import com.raizes.dto.DadosCadastroUsuario;
import com.raizes.dto.DadosExclusaoUsuario;
import com.raizes.dto.DadosListagemUsuario;
import com.raizes.service.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.net.URI;
import java.util.UUID;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService service;

    @Nested
    class CadastrarUsuario {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 201 para cadastrar usuario")
            void test() throws Exception {

                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

                DadosListagemUsuario usuario = new DadosListagemUsuario(
                        id,
                        "Rodrigo",
                        "rodrigo@email.com",
                        "00009090",
                        true
                );

                Mockito.when(service.cadastrar(Mockito.any(DadosCadastroUsuario.class)))
                        .thenReturn(usuario);

                String json = """
                        {
                            "nome": "Rodrigo",
                            "email": "rodrigo@email.com",
                            "telefone": "00009090"
                        }
                        """;

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse();

                Assertions.assertEquals(201, response.getStatus());
                URI location = URI.create(response.getHeader("Location"));
                Assertions.assertEquals(
                        "/usuarios/" + id,
                        location.getPath()
                );
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 400 para cadastrar usuario")
            void test() throws Exception {
                String json = "";
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }
        }
    }

    @Nested
    class ObterUsuario {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 200 para obter usuario")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/usuarios/{id}", "123e4567-e89b-12d3-a456-426614174000")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 404 para obter usuario")
            void test() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                when(service.obter(id))
                        .thenThrow(new EntityNotFoundException());
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/usuarios/{id}", "123e4567-e89b-12d3-a456-426614174000")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class AtualizarUsuario {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 200 para atualizar usuario")
            void test() throws Exception {
                String json = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "nome": "Rodrigo",
                            "email": "rodrigo@email.com",
                            "telefone": "00009090"
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 400 para atualizar usuario")
            void test() throws Exception {
                String json = """
                        {
                            "nome": "Rodrigo",
                            "email": "rodrigo@email.com",
                            "telefone": "00009090"
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 404 para atualizar usuario")
            void test2() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var usuario = new DadosAtualizacaoUsuario(
                        id,
                        "Rodrigo",
                        "rodrigo@email.com",
                        "00009090",
                        true
                );
                String json = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "nome": "Rodrigo",
                            "email": "rodrigo@email.com",
                            "telefone": "00009090",
                            "ativo": true
                        }
                        """;
                doThrow(new EntityNotFoundException())
                        .when(service)
                        .atualizar(usuario);
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class ExcluirUsuario {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 204 para excluir usuario")
            void test() throws Exception{
                String json = """
                        {
                        "id": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(204, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 400 para excluir usuario")
            void test() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var usuario = new DadosExclusaoUsuario(id);

                String json = "";

                doThrow(new EntityNotFoundException())
                        .when(service)
                        .excluir(usuario);

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andReturn()
                        .getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 404 para excluir usuario")
            void test2() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var usuario = new DadosExclusaoUsuario(id);

                String json = """
                        {
                        "id": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """;

                doThrow(new EntityNotFoundException())
                        .when(service)
                        .excluir(usuario);

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/usuarios")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON)
                        )
                        .andReturn()
                        .getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }
}


