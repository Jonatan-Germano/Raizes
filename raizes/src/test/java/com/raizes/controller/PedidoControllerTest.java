package com.raizes.controller;
import com.raizes.dto.*;
import com.raizes.model.*;
import com.raizes.service.PedidoService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService service;

    @Nested
    class CadastrarPedido {
        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 201 para cadastrar pedido")
            void test() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                UUID produtoId = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
                Usuario usuario = new Usuario(
                        id,
                        "Rodrigo",
                        "rodrigo@email.com",
                        "00009090",
                        true
                );
                DadosItemPedido item = new DadosItemPedido(
                        produtoId,
                        "Prato",
                        2
                );
                DadosListagemPedido pedido = new DadosListagemPedido(
                        id,
                        1,
                        LocalDateTime.now(),
                        Status.AGUARDANDO_PAGAMENTO,
                        List.of(item),
                        usuario,
                        CanalPedido.WEB,
                        FormaPagamento.CARTAO,
                        StatusPagamento.AGUARDANDO,
                        BigDecimal.valueOf(1)
                );
                Mockito.when(service.cadastrar(any(DadosCadastroPedido.class)))
                        .thenReturn(pedido);
                String json = """
                    {
                        "numero": "1",
                        "idUsuario": "123e4567-e89b-12d3-a456-426614174000",
                        "canalPedido": "WEB",
                        "itens": [
                            {
                                "id": "123e4567-e89b-12d3-a456-426614174001",
                                "nome": "Prato",
                                "quantidade": "2"
                            }
                        ],
                        "pagamento": {
                            "forma": "CARTAO"
                        }
                    }
                    """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/pedidos")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn()
                        .getResponse();

                Assertions.assertEquals(201, response.getStatus());
                URI location = URI.create(response.getHeader("Location"));
                Assertions.assertEquals(
                        "/pedidos/" + id,
                        location.getPath()
                );
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 400 para cadastrar pedido")
            void test() throws Exception {
                String json = "";
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.post("/pedidos")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }
        }
    }

    @Nested
    class AutorizarPedido {

        @Nested
        class Sucesso {
            @Test
            @DisplayName("Retornaria 200 para autorizar pedido")
            void test() throws Exception {
                String json = """
                        {
                            "idProduto": "86455e01-5829-4c6a-bc7e-6e59208c7965",
                            "statusPagamento": "PAGO"
                        }
                        """;
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.patch("/pedidos/{id}/pagamento", "123e4567-e89b-12d3-a456-426614174000")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 404 para autorizar pedido")
            void test() throws Exception {
                UUID idProduto = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174001");
                ConfirmacaoPagamentoDTO dados = new ConfirmacaoPagamentoDTO(
                        idProduto,
                        StatusPagamento.PAGO
                );
                String json = """
                        {
                            "idProduto": "123e4567-e89b-12d3-a456-426614174000",
                            "statusPagamento": "PAGO"
                        }
                        """;

                when(service.autorizarPagamento(id, dados))
                        .thenThrow(new EntityNotFoundException());
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.patch("/pedidos/{id}/pagamento", "123e4567-e89b-12d3-a456-426614174001")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 400 para autorizar pedido")
            void test2() throws Exception{
                String json = "";
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.patch("/pedidos/{id}/pagamento", "123e4567-e89b-12d3-a456-426614174000")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }
        }
    }

    @Nested
    class ListarPedido {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 200 para listar pedidos")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/pedidos")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 404 para listar pedidos")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                get("/pedidos2")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class FiltrarCanalPedido {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 200 para listar canal")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/pedidos/{canal}", "WEB")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(200, response.getStatus());
            }
        }

        @Nested
        class Falha {

            @Test
            @DisplayName("Retornaria 400 para listar canal")
            void test() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/pedidos/{canal}", "WE")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 404 para listar canal")
            void test2() throws Exception {
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.get("/pedidos2/{canal}", "WE")
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(404, response.getStatus());
            }
        }
    }

    @Nested
    class CancelarPedido {

        @Nested
        class Sucesso {

            @Test
            @DisplayName("Retornaria 204 pra excluir pedido")
            void test() throws Exception {
                String json = """
                {
                    "id": "123e4567-e89b-12d3-a456-426614174000"
                }
                """;

                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/pedidos")
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
                String json = "";
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/pedidos")
                                        .content(json)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andReturn().getResponse();
                Assertions.assertEquals(400, response.getStatus());
            }

            @Test
            @DisplayName("Retornaria 404 para excluir usuario")
            void test2() throws Exception {
                UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
                var pedido = new DadosCancelamentoPedido(id);

                String json = """
                        {
                        "id": "123e4567-e89b-12d3-a456-426614174000"
                        }
                        """;

                doThrow(new EntityNotFoundException())
                        .when(service)
                        .cancelar(pedido);
                MockHttpServletResponse response = mockMvc.perform(
                                MockMvcRequestBuilders.delete("/pedidos")
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