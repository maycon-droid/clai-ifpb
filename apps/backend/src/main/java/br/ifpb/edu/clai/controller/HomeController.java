package br.ifpb.edu.clai.controller;

import br.ifpb.edu.clai.repository.AlunoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final AlunoRepository alunoRepository;

    public HomeController(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        long totalAlunos = alunoRepository.count();

        model.addAttribute("totalAlunos", totalAlunos);

        return "home";
    }
}