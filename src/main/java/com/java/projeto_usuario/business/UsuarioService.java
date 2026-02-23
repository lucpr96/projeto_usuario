package com.java.projeto_usuario.business;

import com.java.projeto_usuario.business.converter.UsuarioConverter;
import com.java.projeto_usuario.business.dto.UsuarioDTO;
import com.java.projeto_usuario.infrastructure.entity.Usuario;
import com.java.projeto_usuario.infrastructure.exceptions.ConflictException;
import com.java.projeto_usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.java.projeto_usuario.infrastructure.repository.UsuarioRepository;
import com.java.projeto_usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    //Dependencias necessarias - Puxa metodos de outras classe necessarias para esta
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    //Metodos de Salvar, Verificar, Buscar e Deletar

    //Metodo que ira salvar o usuario no banco de dados, além de encriptar a senha do usuario - POST/usuario
    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO) {
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO);
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));
    }

    //Metodo que ira verficar se existe o email cadastrado e caso sim, sera retornado uma Exception
    public void emailExiste(String email){
        try{
            boolean existe = verificaEmailExistente(email);
            if(existe){
                throw new ConflictException("Email já cadastrado " + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email já cadastrado " + e.getCause());
        }
    }

    //Metodo que fara a busca de um usuario cadastrado na usuarioRepository - POST/usuario/login
    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    //Metodo para fazer a busca de Usuario por email - Metodo GET/usuario?email=...
    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado: " + email));
    }


    //Metodo para fazer o delete do Usuario por email - Metodo DELETE/usuario/...
    public void deleteUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

    // Metodo para atualizar os Dados de um Usuario já cadastrado -
    // Através do Token (Bearer - Gerado em POST/login - Pelo Jason Web Token)
    public UsuarioDTO atualizarDadosUsuario(String token, UsuarioDTO dto) {

        // Busca do email do usuário através do token (para tirar a obrigatoriedade de passar o email)
        String email = jwtUtil.extractUserEmail(token.substring(7));

        //Criptografia de senha
        dto.setSenha(dto.getSenha() !=null ? passwordEncoder.encode(dto.getSenha()) : null);

        //Busca dos dados do usuario no banco de dados
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        //Mescla dos dados da requisição DTO com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto, usuarioEntity);

        //Foi salvo os dados do usuario convertido e depois foi capturado o retorno e convertido para UsarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

}


