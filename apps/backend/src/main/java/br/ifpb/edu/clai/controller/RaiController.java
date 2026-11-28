package br.ifpb.edu.clai.controller;

import java.util.HashMap;
import java.util.List;
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
import br.ifpb.edu.clai.model.Rai;
import br.ifpb.edu.clai.service.AlunoService;
import br.ifpb.edu.clai.service.PeaService;
import br.ifpb.edu.clai.service.PeiService;
import br.ifpb.edu.clai.service.RaiService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/rais")
public class RaiController {

    private static final String VIEW_LISTA = "rais/lista-rais";
    private static final String VIEW_DETALHES = "rais/detalhes-rai";
    private static final String VIEW_FORMULARIO = "rais/formulario-rai";

    private final RaiService raiService;
    private final PeaService peaService;
    private final PeiService peiService;
    private final AlunoService alunoService;

    public RaiController(
            RaiService raiService,
            PeaService peaService,
            PeiService peiService,
            AlunoService alunoService) {
        this.raiService = raiService;
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
        List<Rai> rais = raiService.listarTodos();
        Map<String, Pea> peasPorRai = new HashMap<>();
        Map<String, Pei> peisPorRai = new HashMap<>();
        Map<String, Aluno> alunosPorRai = new HashMap<>();

        for (Rai rai : rais) {
            Pea pea = peaService.buscarPorId(rai.getPeaId());
            Pei pei = peiService.buscarPorId(pea.getPeiId());

            peasPorRai.put(rai.getId(), pea);
            peisPorRai.put(rai.getId(), pei);
            alunosPorRai.put(rai.getId(), buscarAluno(pei.getAlunoId()));
        }

        model.addAttribute("rais", rais);
        model.addAttribute("peasPorRai", peasPorRai);
        model.addAttribute("peisPorRai", peisPorRai);
        model.addAttribute("alunosPorRai", alunosPorRai);

        return VIEW_LISTA;
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable String id, Model model) {
        Rai rai = raiService.buscarPorId(id);
        Pea pea = peaService.buscarPorId(rai.getPeaId());
        Pei pei = peiService.buscarPorId(pea.getPeiId());

        model.addAttribute("rai", rai);
        model.addAttribute("pea", pea);
        model.addAttribute("pei", pei);
        model.addAttribute("aluno", buscarAluno(pei.getAlunoId()));

        return VIEW_DETALHES;
    }

    @GetMapping("/novo")
    public String exibirFormularioNovo(
            @RequestParam(required = false) String peaId,
            Model model) {

        Rai rai = new Rai();
        rai.setPeaId(peaId);

        model.addAttribute("rai", rai);
        prepararSelecaoPeas(model);

        if (peaId != null && !peaId.isBlank()) {
            prepararIdentificacaoPea(peaId, model);
        }

        return VIEW_FORMULARIO;
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("rai") Rai rai,
            BindingResult resultado,
            Model model) {

        if (!resultado.hasErrors()) {
            raiService.criar(rai.getPeaId(), rai);
            return "redirect:/rais";
        }

        prepararSelecaoPeas(model);

        if (rai.getPeaId() != null && !rai.getPeaId().isBlank()) {
            prepararIdentificacaoPea(rai.getPeaId(), model);
        }

        return VIEW_FORMULARIO;
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(
            @PathVariable String id,
            Model model) {

        Rai rai = raiService.buscarPorId(id);

        model.addAttribute("rai", rai);
        prepararSelecaoPeas(model);
        prepararIdentificacaoPea(rai.getPeaId(), model);

        return VIEW_FORMULARIO;
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable String id,
            @Valid @ModelAttribute("rai") Rai rai,
            BindingResult resultado,
            Model model) {

        Rai existente = raiService.buscarPorId(id);
        rai.setId(id);
        rai.setPeaId(existente.getPeaId());

        if (resultado.hasErrors()) {
            model.addAttribute("rai", rai);
            prepararSelecaoPeas(model);
            prepararIdentificacaoPea(existente.getPeaId(), model);

            return VIEW_FORMULARIO;
        }

        raiService.atualizar(id, rai);

        return "redirect:/rais";
    }

    private void prepararSelecaoPeas(Model model) {
        List<Pea> peas = peaService.listarTodos();
        Map<String, Pei> peisPorPea = new HashMap<>();
        Map<String, Aluno> alunosPorPea = new HashMap<>();

        for (Pea pea : peas) {
            Pei pei = peiService.buscarPorId(pea.getPeiId());

            peisPorPea.put(pea.getId(), pei);
            alunosPorPea.put(pea.getId(), buscarAluno(pei.getAlunoId()));
        }

        model.addAttribute("peas", peas);
        model.addAttribute("peisPorPea", peisPorPea);
        model.addAttribute("alunosPorPea", alunosPorPea);
    }

    private void prepararIdentificacaoPea(String peaId, Model model) {
        Pea pea = peaService.buscarPorId(peaId);
        Pei pei = peiService.buscarPorId(pea.getPeiId());

        model.addAttribute("pea", pea);
        model.addAttribute("pei", pei);
        model.addAttribute("aluno", buscarAluno(pei.getAlunoId()));
    }

    private Aluno buscarAluno(String alunoId) {
        return alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(
                        "Aluno não encontrado para o ID: " + alunoId));
    }
}
