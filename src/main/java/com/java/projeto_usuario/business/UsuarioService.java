package com.java.projeto_usuario.business;

import com.java.projeto_usuario.business.converter.UsuarioConverter;
import com.java.projeto_usuario.business.dto.UsuarioDTO;
import com.java.projeto_usuario.infrastructure.entity.Usuario;
import com.java.projeto_usuario.infrastructure.exceptions.ConflictException;
import com.java.projeto_usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.java.projeto_usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    //Metodos necessarios, oriundos da UsuarioRepository e UsuarioConverter
    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    //Metodos de Salvar, Verificar, Buscar e Deletar

    //Metodo que ira salvar o usuario no banco de dados, além de encriptar a senha do usuario
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

    //Metodo que fara a busca de um usuario cadastrado na usuarioRepository
    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    //Metodo para fazer a busca de Usuario por email - Metodo GET
    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email não encontrado: " + email));
    }


    //Metodo para fazer o delete do Usuario por email - Metodo DELTE
    public void deleteUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);
    }

}
