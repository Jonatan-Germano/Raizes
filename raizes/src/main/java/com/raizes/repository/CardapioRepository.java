package com.raizes.repository;

import com.raizes.model.Cardapio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CardapioRepository extends JpaRepository<Cardapio, UUID> {

    Boolean existsByNome(String nome);

    Page<Cardapio> findAllByAtivoTrue(Pageable pageable);

    Boolean findAtivoById(UUID uuid);
}