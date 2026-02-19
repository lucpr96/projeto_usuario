package com.java.projeto_usuario.infrastructure.repository;


import com.java.projeto_usuario.infrastructure.entity.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
    // Optional = Usado para não permitir o retorno de informações nulas.
    // Metodo para encontrar o Usuario pelo email

    @Transactional
    void deleteByEmail(String email);
    // Metodo para deleter Usuario por email. @Transactional -> Obrigatório para metodos delete
}
