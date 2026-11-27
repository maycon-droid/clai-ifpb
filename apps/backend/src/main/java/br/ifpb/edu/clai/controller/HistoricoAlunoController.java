package br.ifpb.edu.clai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import br.ifpb.edu.clai.dto.EventoHistorico;
import br.ifpb.edu.clai.service.HistoricoAlunoService;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/historico")
public class HistoricoAlunoController {
    private final HistoricoAlunoService historicoAlunoService;

    public HistoricoAlunoController(HistoricoAlunoService historicoAlunoService) {
        this.historicoAlunoService = historicoAlunoService;
    }

    @GetMapping("{alunoId}")
    public String buscarHistoricoAluno(@PathVariable String alunoId, Model model) {
        List<EventoHistorico> historico = historicoAlunoService.buscarHistoricoAluno(alunoId);
        String nomeAluno = historicoAlunoService.buscarNomeAluno(alunoId);
        String matriculaAluno = historicoAlunoService.buscarMatriculaAluno(alunoId);
        model.addAttribute("matriculaAluno", matriculaAluno);
        model.addAttribute("historico", historico);
        model.addAttribute("nomeAluno", nomeAluno);
        return "historico";
    }
}