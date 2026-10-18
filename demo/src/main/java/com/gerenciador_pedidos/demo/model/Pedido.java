package com.gerenciador_pedidos.demo.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "pedidos")
public class Pedido {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)

private Long id;

private List<String > itens;
private String descricao;
public Double valorTotal;
private boolean concluido;
private String status; // PENDENTE, CONCLUIDO, CANCELADO
private long criadaEm;
private long concluidaEm;

public Pedido() {
}

public Pedido(List<String >  itens, String descricao, Double valorTotal) {
this.itens = itens;
this.descricao = descricao;
this.valorTotal = valorTotal;
this.concluido = false;
this.status = "PENDENTE";
this.criadaEm = System.currentTimeMillis();
}

public Long getId() { return id; }
public void setId(Long id) { this.id = id; }

public List<String> getItens() { return itens; }
public void setItens(List<String> itens) { this.itens = itens; }

public String getDescricao() { return descricao; }
public void setDescricao(String descricao) { this.descricao = descricao; }

public Double getValorTotal() { return valorTotal; }
public void setValorTotal(Double valorTotal) { this.valorTotal = valorTotal; }

public boolean isConcluido() { return concluido; }
public void setConcluido(boolean concluido) { this.concluido = concluido; }

public String getStatus() { return status; }
public void setStatus(String status) { this.status = status; }

public long getCriadaEm() { return criadaEm; }
public void setCriadaEm(long criadaEm) { this.criadaEm = criadaEm; }

public long getConcluidaEm() { return concluidaEm; }
public void setConcluidaEm(long concluidaEm) { this.concluidaEm = concluidaEm; }
}
