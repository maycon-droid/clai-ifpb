package br.ifpb.edu.clai.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {

    public AgendamentoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
