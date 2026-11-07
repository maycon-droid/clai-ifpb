package br.ifpb.edu.clai.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.service.AlunoService;
import br.ifpb.edu.clai.service.EstudoDeCasoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/estudos-de-caso")
public class EstudoDeCasoController {
 
    private final EstudoDeCasoService estudoService;
    private final AlunoService alunoService;

    public EstudoDeCasoController(EstudoDeCasoService estudoService,
                                  AlunoService alunoService) {
        this.estudoService = estudoService;
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listar(Model model) {
        Map<String, String> nomesAlunos = new HashMap<>();
        for (Aluno aluno : alunoService.listarTodos()) {
            nomesAlunos.put(aluno.getId(), aluno.getNome());
        }

        model.addAttribute("estudos", estudoService.listarTodos());
        model.addAttribute("nomesAlunos", nomesAlunos);
        return "estudos-de-caso/consulta-estudos-de-caso";
    }

    @GetMapping("/{id}")
    public String visualizar(@PathVariable String id, Model model) {
        EstudoDeCaso estudo = estudoService.buscarPorId(id);

        model.addAttribute("estudo", estudo);
        model.addAttribute("aluno", buscarAluno(estudo.getAlunoId()));
        return "estudos-de-caso/detalhes-estudo-de-caso";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovo(
            @RequestParam(required = false) String alunoId,
            Model model) {

        if (alunoId == null || alunoId.isBlank()) {
            return "redirect:/alunos";
        }

        EstudoDeCaso estudo = new EstudoDeCaso();
        estudo.setAlunoId(alunoId);
        inicializarAninhados(estudo);

        model.addAttribute("estudo", estudo);
        model.addAttribute("aluno", buscarAluno(alunoId));
        return "estudos-de-caso/form";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("estudo") EstudoDeCaso estudo,
            BindingResult resultado,
            Model model) {

        if (resultado.hasErrors()) {
            // sem aluno vinculado não há como reexibir o formulário
            if (estudo.getAlunoId() == null || estudo.getAlunoId().isBlank()) {
                return "redirect:/alunos";
            }
            inicializarAninhados(estudo);
            model.addAttribute("aluno", buscarAluno(estudo.getAlunoId()));
            return "estudos-de-caso/form";
        }

        estudoService.criar(estudo.getAlunoId(), estudo);
        return "redirect:/estudos-de-caso";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable String id, Model model) {
        EstudoDeCaso estudo = estudoService.buscarPorId(id);
        inicializarAninhados(estudo);

        model.addAttribute("estudo", estudo);
        model.addAttribute("aluno", buscarAluno(estudo.getAlunoId()));
        return "estudos-de-caso/form";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable String id,
            @Valid @ModelAttribute("estudo") EstudoDeCaso estudo,
            BindingResult resultado,
            Model model) {

        estudo.setId(id);

        if (resultado.hasErrors()) {
            String alunoIdOriginal = estudoService.buscarPorId(id).getAlunoId();
            estudo.setAlunoId(alunoIdOriginal);
            inicializarAninhados(estudo);
            model.addAttribute("aluno", buscarAluno(alunoIdOriginal));
            return "estudos-de-caso/form";
        }

        estudoService.atualizar(id, estudo);
        return "redirect:/estudos-de-caso";
    }

    private Aluno buscarAluno(String alunoId) {
        return alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(
                        "Aluno não encontrado para o ID: " + alunoId));
    }

    // Os objetos aninhados vêm nulos; sem isso o Thymeleaf quebra em *{encaminhamento.motivoPrincipal}
    private void inicializarAninhados(EstudoDeCaso e) {
        if (e.getEncaminhamento() == null)
            e.setEncaminhamento(new EstudoDeCaso.EncaminhamentoDemanda());
        if (e.getNecessidadeDocumentacao() == null)
            e.setNecessidadeDocumentacao(new EstudoDeCaso.NecessidadeDocumentacao());
        if (e.getHistoricoVidaEscolar() == null)
            e.setHistoricoVidaEscolar(new EstudoDeCaso.HistoricoVidaEscolar());
        if (e.getPerfilAprendizagem() == null)
            e.setPerfilAprendizagem(new EstudoDeCaso.PerfilAprendizagem());
        if (e.getContextoPsicossocial() == null)
            e.setContextoPsicossocial(new EstudoDeCaso.ContextoPsicossocial());
        if (e.getPotencialidadesInteresses() == null)
            e.setPotencialidadesInteresses(new EstudoDeCaso.PotencialidadesInteresses());
        if (e.getSintesePsicopedagogica() == null)
            e.setSintesePsicopedagogica(new EstudoDeCaso.SintesePsicopedagogica());
        if (e.getApoiosAdaptacoes() == null)
            e.setApoiosAdaptacoes(new EstudoDeCaso.ApoiosAdaptacoes());
    }

}
