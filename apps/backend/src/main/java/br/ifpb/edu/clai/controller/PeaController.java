package br.ifpb.edu.clai.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.Pea;
import br.ifpb.edu.clai.model.Pei;
import br.ifpb.edu.clai.service.AlunoService;
import br.ifpb.edu.clai.service.PeaService;
import br.ifpb.edu.clai.service.PeiService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/peas")
public class PeaController {

    private static final String VIEW_LISTA = "peas/lista-peas";
    private static final String VIEW_DETALHES = "peas/detalhes-pea";
    private static final String VIEW_FORMULARIO = "peas/formulario-pea";

    private final PeaService peaService;
    private final PeiService peiService;
    private final AlunoService alunoService;

    public PeaController(
            PeaService peaService,
            PeiService peiService,
            AlunoService alunoService) {
        this.peaService = peaService;
        this.peiService = peiService;
        this.alunoService = alunoService;
    }

    @InitBinder
    public void configurarBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @GetMapping
    public String listar(Model model) {
        Map<String, Pei> peisPorId = new HashMap<>();
        Map<String, Aluno> alunosPorPei = new HashMap<>();

        for (Pei pei : peiService.listarTodos()) {
            peisPorId.put(pei.getId(), pei);
            alunosPorPei.put(pei.getId(), buscarAluno(pei.getAlunoId()));
        }

        model.addAttribute("peas", peaService.listarTodos());
        model.addAttribute("peisPorId", peisPorId);
        model.addAttribute("alunosPorPei", alunosPorPei);

        return VIEW_LISTA;
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable String id, Model model) {
        Pea pea = peaService.buscarPorId(id);
        Pei pei = peiService.buscarPorId(pea.getPeiId());

        model.addAttribute("pea", pea);
        model.addAttribute("pei", pei);
        model.addAttribute("aluno", buscarAluno(pei.getAlunoId()));

        return VIEW_DETALHES;
    }

    @GetMapping("/novo")
    public String exibirFormularioNovo(
            @RequestParam(required = false) String peiId,
            Model model) {

        Pea pea = new Pea();
        pea.setPeiId(peiId);

        model.addAttribute("pea", pea);
        prepararSelecaoPeis(model);

        return VIEW_FORMULARIO;
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("pea") Pea pea,
            BindingResult resultado,
            Model model) {

        if (!resultado.hasErrors()) {
            peaService.criar(pea.getPeiId(), pea);
            return "redirect:/peas";
        }

        prepararSelecaoPeis(model);
        return VIEW_FORMULARIO;
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(
            @PathVariable String id,
            Model model) {

        Pea pea = peaService.buscarPorId(id);
        Pei pei = peiService.buscarPorId(pea.getPeiId());

        model.addAttribute("pea", pea);
        model.addAttribute("pei", pei);
        model.addAttribute("aluno", buscarAluno(pei.getAlunoId()));

        return VIEW_FORMULARIO;
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable String id,
            @Valid @ModelAttribute("pea") Pea pea,
            BindingResult resultado,
            Model model) {

        Pea existente = peaService.buscarPorId(id);
        pea.setId(id);
        pea.setPeiId(existente.getPeiId());

        if (resultado.hasErrors()) {
            Pei pei = peiService.buscarPorId(existente.getPeiId());

            model.addAttribute("pei", pei);
            model.addAttribute("aluno", buscarAluno(pei.getAlunoId()));

            return VIEW_FORMULARIO;
        }

        peaService.atualizar(id, pea);

        return "redirect:/peas";
    }

    private void prepararSelecaoPeis(Model model) {
        Map<String, Aluno> alunosPorPei = new HashMap<>();

        for (Pei pei : peiService.listarTodos()) {
            alunosPorPei.put(pei.getId(), buscarAluno(pei.getAlunoId()));
        }

        model.addAttribute("peis", peiService.listarTodos());
        model.addAttribute("alunosPorPei", alunosPorPei);
    }

    private Aluno buscarAluno(String alunoId) {
        return alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(
                        "Aluno não encontrado para o ID: " + alunoId));
    }
}
