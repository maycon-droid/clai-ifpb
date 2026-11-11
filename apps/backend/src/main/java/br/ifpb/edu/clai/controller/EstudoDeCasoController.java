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
import java.util.ArrayList;
import java.util.List;

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
public String exibirFormularioNovo(Model model) {

    EstudoDeCaso estudo = new EstudoDeCaso();
    inicializarAninhados(estudo);

    model.addAttribute("estudo", estudo);
    model.addAttribute("alunos", listarAlunosParaFormulario());

    return "estudos-de-caso/formulario_estudo_de_caso";
}

    @PostMapping("/salvar")
public String salvar(
        @Valid @ModelAttribute("estudo") EstudoDeCaso estudo,
        BindingResult resultado,
        Model model) {

    if (resultado.hasErrors()) {
        inicializarAninhados(estudo);
        model.addAttribute("alunos", listarAlunosParaFormulario());
        return "estudos-de-caso/formulario_estudo_de_caso";
    }

    estudoService.criar(estudo.getAlunoId(), estudo);
    return "redirect:/estudos-de-caso";
}

    @GetMapping("/{id}/editar")
public String exibirFormularioEdicao(
        @PathVariable String id,
        Model model) {

    EstudoDeCaso estudo = estudoService.buscarPorId(id);
    inicializarAninhados(estudo);

    Aluno aluno = buscarAluno(estudo.getAlunoId());

    model.addAttribute("estudo", estudo);
    model.addAttribute("aluno", aluno);

    return "estudos-de-caso/formulario_estudo_de_caso";
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

        return "estudos-de-caso/formulario_estudo_de_caso";
    }

    estudoService.atualizar(id, estudo);
    return "redirect:/estudos-de-caso";
}
   private List<Map<String, Object>> listarAlunosParaFormulario() {
    List<Map<String, Object>> resultado = new ArrayList<>();

    for (Aluno aluno : alunoService.listarTodos()) {
        Map<String, Object> matricula = new HashMap<>();

        if (aluno.getMatricula() != null) {
            matricula.put("numeroMatricula",
                    aluno.getMatricula().getNumeroMatricula());
            matricula.put("curso", aluno.getMatricula().getCurso());
            matricula.put("periodoIngresso",
                    aluno.getMatricula().getPeriodoIngresso());
            matricula.put("periodoLetivo",
                    aluno.getMatricula().getPeriodoLetivo());
        }

        Map<String, Object> dados = new HashMap<>();
        dados.put("id", aluno.getId());
        dados.put("nome", aluno.getNome());
        dados.put("cpf", aluno.getCpf());
        dados.put("matricula", matricula);

        resultado.add(dados);
    }

    return resultado;
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
