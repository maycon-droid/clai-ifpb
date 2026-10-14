package br.ifpb.edu.clai.controller;

import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.service.AlunoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listarAlunos(Model model) {
        model.addAttribute("alunos", alunoService.listarTodos());
        return "alunos/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "alunos/form";
    }
}