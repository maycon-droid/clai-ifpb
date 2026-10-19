package br.ifpb.edu.clai.controller;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.service.AlunoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(
        @PathVariable String id,
        Model model) {

    Aluno aluno = alunoService.buscarPorId(id)
        .orElseThrow(() ->
            new AlunoNaoEncontradoException(
                "Aluno não encontrado para o ID: " + id
            )
        );

    model.addAttribute("aluno", aluno);

    return "alunos/form";
    }

    @PostMapping("/salvar")
    public String salvarAluno(@ModelAttribute Aluno aluno) {
        alunoService.salvar(aluno); 
        return "redirect:/alunos";
    }
    
    @PostMapping("/{id}/editar")
    public String atualizarAluno(@PathVariable String id, @ModelAttribute Aluno aluno) {
        alunoService.atualizar(id, aluno);
        return "redirect:/alunos";
}
}
