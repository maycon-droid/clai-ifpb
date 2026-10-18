package br.ifpb.edu.clai.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlunoNaoEncontradoException.class)
    public String tratarAlunoNaoEncontrado(
            AlunoNaoEncontradoException exception,
            Model model) {

        model.addAttribute("mensagemErro", exception.getMessage());
        return "erro/aluno-nao-encontrado";
    }

    @ExceptionHandler(MatriculaDuplicadaException.class)
    public String tratarMatriculaDuplicada(
            MatriculaDuplicadaException exception,
            Model model) {

        model.addAttribute("mensagemErro", exception.getMessage());
        return "erro/erro-negocio";
    }
}
