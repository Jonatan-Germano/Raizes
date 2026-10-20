package com.raizes.repository;
import com.raizes.model.CanalPedido;
import com.raizes.model.Pedido;
import com.raizes.model.StatusPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Boolean existsByNumero(Integer numero);

    Page<Pedido> findAllByCanalPedido(CanalPedido canal, Pageable pageable);

    Boolean existsByUsuarioIdAndStatusPagamento(UUID usuarioId, StatusPagamento statusPagamento);

}
