package com.java.projeto_usuario.infrastructure.exceptions;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String mensagem){
        super(mensagem);
    }

    public ResourceNotFoundException(String mensagem, Throwable throwable){
        super(mensagem, throwable);
    }
}


// Exception para mostrar o erro de Usuario não encontrado pel email deste Usuario.