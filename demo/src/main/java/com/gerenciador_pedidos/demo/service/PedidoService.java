package com.gerenciador_pedidos.demo.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gerenciador_pedidos.demo.model.Pedido;
import com.gerenciador_pedidos.demo.repository.PedidoRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PedidoService {

private final PedidoRepository repository;
private final Counter pedidosCriados;
private final Counter pedidosErro;
private final Counter pedidosCancelados;
private final Counter pedidosConcluidos;
private final Timer pedidoLatencia;
private final AtomicLong requisicoesEmAndamento = new AtomicLong(0);

public PedidoService(PedidoRepository repository, MeterRegistry registry) {
this.repository = repository;

this.pedidosCriados = Counter.builder("pedidos.criados")
.description("Total de pedidos criados")
.register(registry);

this.pedidosErro = Counter.builder("pedidos.erro")
.description("Total de pedidos com erro")
.register(registry);

this.pedidosCancelados = Counter.builder("pedidos.cancelados")
.description("Total de pedidos cancelados")
.register(registry);

this.pedidosConcluidos = Counter.builder("pedidos.concluidos")
.description("Total de pedidos concluídos")
.register(registry);

this.pedidoLatencia = Timer.builder("pedidos.latencia")
.description("Latencia das operacoes de pedidos")
.publishPercentileHistogram()
.register(registry);

Gauge.builder("pedidos.em_andamento", requisicoesEmAndamento, AtomicLong::get)
.description("Pedidos em andamento no momento")
.register(registry);
}

@Transactional
public Pedido criarPedido(List<String> itens, String descricao, Double valorTotal) {
requisicoesEmAndamento.incrementAndGet();
Timer.Sample sample = Timer.start();
try {
Pedido pedido = new Pedido(itens, descricao, valorTotal);
Pedido salva = repository.save(pedido);
pedidosCriados.increment();
return salva;
} catch (Exception e) {
pedidosErro.increment();
throw e;
} finally {
sample.stop(pedidoLatencia);
requisicoesEmAndamento.decrementAndGet();
}
}

public List<Pedido> listarPedidos() {
Timer.Sample sample = Timer.start();
try {
return repository.findAll();
} finally {
sample.stop(pedidoLatencia);
}
}

@Transactional
public Pedido concluirPedido(Long id) {
requisicoesEmAndamento.incrementAndGet();
Timer.Sample sample = Timer.start();

try {
Optional<Pedido> optional = repository.findById(id);
if (optional.isEmpty()) {
pedidosErro.increment();
throw new RuntimeException("Pedido nao encontrado   : " + id);
}
Pedido pedido = optional.get();
pedido.setConcluido(true);
pedido.setStatus("CONCLUIDO");
pedido.setConcluidaEm(System.currentTimeMillis());
pedidosConcluidos.increment();
return repository.save(pedido);
} catch (Exception e) {
pedidosErro.increment();
throw e;
} finally {
sample.stop(pedidoLatencia);
requisicoesEmAndamento.decrementAndGet();
}
}


@Transactional
public Pedido cancelarPedido(Long id) {
requisicoesEmAndamento.incrementAndGet();
Timer.Sample sample = Timer.start();

try {
Optional<Pedido> optional = repository.findById(id);
if (optional.isEmpty()) {
pedidosErro.increment();
throw new RuntimeException("Pedido nao encontrado   : " + id);
}
Pedido pedido = optional.get();
pedido.setConcluido(true);
pedido.setStatus("CANCELADO");
pedido.setConcluidaEm(System.currentTimeMillis());
pedidosCancelados.increment();
return repository.save(pedido);
} catch (Exception e) {
pedidosErro.increment();
throw e;
} finally {
sample.stop(pedidoLatencia);
requisicoesEmAndamento.decrementAndGet();
}
}

@Transactional
public void removerPedido(Long id) {
Timer.Sample sample = Timer.start();
try {
if (!repository.existsById(id)) {
pedidosErro.increment();
throw new RuntimeException("Pedido nao encontrado: " + id);
}
repository.deleteById(id);
} catch (Exception e) {
pedidosErro.increment();
throw e;
} finally {
sample.stop(pedidoLatencia);
requisicoesEmAndamento.decrementAndGet();
}
}
}