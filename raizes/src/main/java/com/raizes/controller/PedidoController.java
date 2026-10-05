package com.raizes.controller;
import com.raizes.dto.ConfirmacaoPagamentoDTO;
import com.raizes.dto.DadosCadastroPedido;
import com.raizes.dto.DadosCancelamentoPedido;
import com.raizes.dto.DadosListagemPedido;
import com.raizes.model.CanalPedido;
import com.raizes.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DadosListagemPedido> cadastrar(@RequestBody @Valid DadosCadastroPedido dados, UriComponentsBuilder uriBuilder) {
        DadosListagemPedido pedido = service.cadastrar(dados);
        URI uri = uriBuilder.path("/pedidos/{id}").buildAndExpand(pedido.id()).toUri();
        return ResponseEntity.created(uri).body(pedido);
    }

    @PatchMapping("/{id}/pagamento")
    public ResponseEntity<DadosListagemPedido> autorizarPagamento(
            @PathVariable UUID id,
            @RequestBody ConfirmacaoPagamentoDTO dados) {

        DadosListagemPedido pedidoAtualizado = service.autorizarPagamento(id, dados);
        return ResponseEntity.ok(pedidoAtualizado);
    }

    @GetMapping
    public ResponseEntity<Page<DadosListagemPedido>> listar(@PageableDefault(size = 10, sort = {"numero"}) Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{canal}")
    public ResponseEntity<Page<DadosListagemPedido>> filtrarCanal(@PathVariable CanalPedido canal, @PageableDefault(size =10, sort = {"numero"}) Pageable pageable) {
        return ResponseEntity.ok(service.filtrarCanal(canal, pageable));
    }

    @DeleteMapping
    public ResponseEntity cancelar(@RequestBody @Valid DadosCancelamentoPedido dados){
        service.cancelar(dados);
        return ResponseEntity.noContent().build();
    }
}
