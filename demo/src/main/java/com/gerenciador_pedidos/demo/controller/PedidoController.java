package com.gerenciador_pedidos.demo.controller;

import com.gerenciador_pedidos.demo.model.Pedido;
import com.gerenciador_pedidos.demo.service.PedidoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

private PedidoService service = null;

public PedidoController(PedidoService service) {
this.service = service;
}

@PostMapping
public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest request) {
Pedido pedido = service.criarPedido(request.itens(), request.descricao(), request.valorTotal());
return ResponseEntity.created(URI.create("/api/pedidos/" + pedido.getId())).body(pedido);
}

@GetMapping
public List<Pedido> listar() {
return service.listarPedidos();
}

@PutMapping("/{id}/concluir")
public Pedido concluir(@PathVariable Long id) {
return service.concluirPedido(id);
}

@PutMapping("/{id}/cancelar")
public Pedido cancelar(@PathVariable Long id) {
return service.cancelarPedido(id);
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> remover(@PathVariable Long id) {
service.removerPedido(id);
return ResponseEntity.noContent().build();
}



public record PedidoRequest(List<String> itens, String descricao, Double valorTotal) {}
}