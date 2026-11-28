package br.ifpb.edu.clai.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

// Handlers dos módulos de planejamento educacional (Plano de Ação e Relatório de Observação).
// Ficam numa classe própria para não gerar conflito de merge no GlobalExceptionHandler.
@ControllerAdvice
public class PlanejamentoExceptionHandler {

    @ExceptionHandler({
            PlanoAcaoNaoEncontradaException.class,
            PeaNaoEncontradoException.class,
            PlanoAcaoDuplicadoException.class,
            RelatorioObservacaoNaoEncontradoException.class
    })
    public String tratarErroDeNegocio(RuntimeException exception, Model model) {
        model.addAttribute("mensagemErro", exception.getMessage());
        return "erro/erro-negocio";
    }
}
