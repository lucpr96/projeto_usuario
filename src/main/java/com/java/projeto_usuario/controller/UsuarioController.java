package com.java.projeto_usuario.controller;

import com.java.projeto_usuario.business.UsuarioService;
import com.java.projeto_usuario.business.dto.UsuarioDTO;
import com.java.projeto_usuario.infrastructure.entity.Usuario;
import com.java.projeto_usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor

public class UsuarioController {

    //Os mesmos metodos necessarios, oriundos da UsuarioService
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // Mapeamento dos Metodos presentes na UsuarioService

    //Mapeamento do Metodo para Salvar o usuario - Post/usuario
    @PostMapping
    public ResponseEntity<UsuarioDTO> salvaUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        return ResponseEntity.ok(usuarioService.salvaUsuario(usuarioDTO));
    }

    //Mapeamento do Login do usario - POST/usuario/login
    @PostMapping("/login") // O URI adicionou só pode ocrrer no POST
    public String login(@RequestBody UsuarioDTO usuarioDTO){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                        usuarioDTO.getSenha())
        );
        return "Bearer" + jwtUtil.generateToken(authentication.getName());
    }

    //Mapeamento do metodo de Buscar o usuario por seu Email - GET/usuario?email=...
    @GetMapping
    public ResponseEntity<Usuario> buscaUsuarioPorEmail(@RequestParam("email") String email){
        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    //Mapeamente de Deletar o usuario por seu Email - DELETE/usuario/...
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteUsuarioPorEmail(@PathVariable String email){
        usuarioService.deleteUsuarioPorEmail(email);
        return ResponseEntity.ok().build();
    }

    //Mapeamento do metodo de Modificar os Dados do Usuario - PUT/
    //OBS - Necessário passar as anotacoes para puxar o token
    @PutMapping
    public ResponseEntity<UsuarioDTO> atualizaDadosUsuario(@RequestBody UsuarioDTO dto, @RequestHeader("Authorization") String token){
        return ResponseEntity.ok((usuarioService.atualizarDadosUsuario(token, dto)));
    }
}
