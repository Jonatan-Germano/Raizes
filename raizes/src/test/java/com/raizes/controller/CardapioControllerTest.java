package com.raizes.controller;
import com.raizes.dto.*;
import com.raizes.service.CardapioService;
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
import java.math.BigDecimal;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class CardapioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CardapioService service;

    @Nested
    class CadastrarPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Retornaria 201 para cadastrar prato")
            void test() throws Exception {

                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                DadosListagemCardapio prato = new DadosListagemCardapio(
                        id,
                        "Prato",
                        "descricao",
                        BigDecimal.valueOf(1.00),
                        "Categoria",
                        true,
                        2

                );
                Mockito.when(service.cadastrar(any(DadosCadastroCardapio.class)))
                        .thenReturn(prato);
                String json = """
                        {
                            "nome": "Prato",
                            "descricao": "Descricao",
                            "preco" : "1.00",
                            "categoria": "Categoria",
                            "estoque": 2
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse();

                Assertions.assertEquals(201, response.getStatus());
                URI location = URI.create(Objects.requireNonNull(response.getHeader("Location")));
                Assertions.assertEquals(
                        "/cardapio/" + id,
                        location.getPath()
                );
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Retornaria 400 para cadastrar prato")
            void test() throws Exception {
                String json = "";
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }
        }

    }

    @Nested
    class ObterCardapio{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("deve retornar 200 para obter cardapio")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/cardapio")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("deve retornar 404 para obter cardapio")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                get("/cardapio2")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class AtualizarPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Retornaria 200 para atualizar prato")
            void test() throws Exception {
                String json = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "nome": "Prato",
                            "descricao": "Prato",
                            "preco": "1.00",
                            "categoria": "Categoria",
                            "estoque": 2
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Retornaria 400 para atualizar prato")
            void test() throws Exception {
                String json = """
                        {
                            "nome": "Prato",
                            "descricao": "Prato",
                            "preco": "1.00",
                            "categoria": "Categoria",
                            "estoque": 2
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 404 para atualizar prato")
            void test2() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var prato = new DadosAtualizacaoCardapio(
                        id,
                        "Prato",
                        "descricao",
                        BigDecimal.valueOf(1),
                        "Categoria",
                        true,
                        2
                );
                String json = """
                        {
                            "id": "123e4567-e89b-12d3-a456-426614174000",
                            "nome": "Prato",
                            "descricao": "descricao",
                            "preco": "1",
                            "categoria": "Categoria",
                            "ativo": true,
                            "estoque": 2
                        }
                        """;
                doThrow(new EntityNotFoundException())
                        .when(service)
                        .atualizar(prato);
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.put("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class ExcluirPrato{

        @Nested
        class Sucesso{

            @Test
            @DisplayName("Retornaria 204 pra excluir prato")
            void test() throws Exception {
                String json = """
                        {
                        "id": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/cardapio")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(204, response.getStatus());
            }
        }

        @Nested
        class Falha{

            @Test
            @DisplayName("Retornaria 400 para excluir usuario")
            void test() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var prato = new DadosExclusaoCardapio(id);

                String json = "";

                doThrow(new EntityNotFoundException())
                        .when(service)
                        .excluir(prato);

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/cardapio")
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
                var prato = new DadosExclusaoCardapio(id);

                String json = """
                        {
                        "id": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """;

                doThrow(new EntityNotFoundException())
                        .when(service)
                        .excluir(prato);

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/cardapio")
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