package br.ifpb.edu.clai.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AgendamentoExceptionHandler {

    @ExceptionHandler({
        AgendamentoNaoEncontradoException.class,
        AgendamentoInvalidoException.class
    })
    public String tratarErroDeAgendamento(
            RuntimeException exception,
            Model model) {

        model.addAttribute("mensagemErro", exception.getMessage());
        return "erro/erro-negocio";
    }
}
