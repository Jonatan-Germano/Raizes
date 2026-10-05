package com.raizes.repository;

import com.raizes.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Boolean existsByEmailOrTelefone(String email, String telefone);

    @Query("""
            select u.ativo
            from Usuario u
            where
            u.id = :id
            """)
    Boolean findAtivoById(UUID id);
}
