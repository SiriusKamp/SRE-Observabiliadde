package com.gerenciador_pedidos.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.gerenciador_pedidos.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
long countByStatus(String status);
}
