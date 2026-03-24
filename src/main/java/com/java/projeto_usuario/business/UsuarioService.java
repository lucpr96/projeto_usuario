package com.java.projeto_usuario.business;

import com.java.projeto_usuario.business.converter.UsuarioConverter;
import com.java.projeto_usuario.business.dto.EnderecoDTO;
import com.java.projeto_usuario.business.dto.TelefoneDTO;
import com.java.projeto_usuario.business.dto.UsuarioDTO;
import com.java.projeto_usuario.infrastructure.entity.Endereco;
import com.java.projeto_usuario.infrastructure.entity.Telefone;
import com.java.projeto_usuario.infrastructure.entity.Usuario;
import com.java.projeto_usuario.infrastructure.exceptions.ConflictException;
import com.java.projeto_usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.java.projeto_usuario.infrastructure.repository.EnderecoRepository;
import com.java.projeto_usuario.infrastructure.repository.TelefoneRepository;
import com.java.projeto_usuario.infrastructure.repository.UsuarioRepository;
import com.java.projeto_usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;


    //Metodos de Salvar, Verificar, Buscar, Deletar, Modificar (usuario), Modificar (endereco), Modificar (telefone)



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
    public UsuarioDTO buscarUsuarioPorEmail(String email){

        try {
            return usuarioConverter.paraUsuarioDTO(usuarioRepository.findByEmail(email).orElseThrow(
                    () -> new ResourceNotFoundException("Email não encontrado: " + email)));

        } catch (ResourceNotFoundException e){
            throw new ResourceNotFoundException("Email não encontrado: " + email);
        }
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
        Usuario usuarioEntityBusca = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email não localizado"));

        //Mescla dos dados da requisição DTO com os dados do banco de dados
        Usuario usuarioConversao = usuarioConverter.updateUsuario(dto, usuarioEntityBusca);

        //Foi salvo os dados do usuario convertido e depois foi capturado o retorno e convertido para UsarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuarioConversao));

    }



    //Metodo para Modificar Endereco
    public EnderecoDTO atualizarDadosEndereco(Long idEndereco, EnderecoDTO enderecoDTO) {

        Endereco EnderecoEntityBusca = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("Id não encontrado" + idEndereco));

        Endereco EnderecoConversaoAtualiza = usuarioConverter.updateEndereco(enderecoDTO, EnderecoEntityBusca);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(EnderecoConversaoAtualiza));
    }



    //Metodo para Modificar Telefone
    public TelefoneDTO atualizarDadosTelefone(Long idTelefone, TelefoneDTO telefoneDTO) {

        Telefone TelefoneEntityBusca = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("Id não encontrado" + idTelefone));

        Telefone telefoneConversao = usuarioConverter.updateTelefone(telefoneDTO, TelefoneEntityBusca);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefoneConversao));
    }



    //Metodo que faz o cadastro de Enderecos por meio do id do Usuario
    public EnderecoDTO cadastroEndereco(String token, EnderecoDTO dto) {

        // Extrai Email do token
        String emailExtrairToken = jwtUtil.extractUserEmail(token.substring(7));

        // Do email extrai o id do usuario
        Usuario usuarioBuscar = usuarioRepository.findByEmail(emailExtrairToken).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado" + emailExtrairToken));

        // Converte o dto do Endereco e o Id do Usuario em Endereco Entity
        Endereco EnderecoConversaoCadastra = usuarioConverter.paraEnderecoEntity(dto, usuarioBuscar.getId());

        // Salva o Endereco Entity e retorna para Controller
        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(EnderecoConversaoCadastra));


    }



    //Metodo que faz o cadastro de Telefones por meio do id do Usuario
    public TelefoneDTO cadastroTelefone(String token, TelefoneDTO dto) {

        // Extrai Email do token
        String emailExtrairToken = jwtUtil.extractUserEmail(token.substring(7));

        // Do email extrai o id do usuario
        Usuario usuarioBuscar = usuarioRepository.findByEmail(emailExtrairToken).orElseThrow(() ->
                new ResourceNotFoundException("Email não encontrado" + emailExtrairToken));

        // Converte o dto do Telefone e o Id do Usuario em Telefone Entity
        Telefone TelefoneConversaoCadastra = usuarioConverter.paraTelefoneEntity(dto, usuarioBuscar.getId());

        // Salva o Telefone Entity e retorna para Controller
        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(TelefoneConversaoCadastra));

    }

}


