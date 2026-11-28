package br.ifpb.edu.clai.controller;

import java.time.LocalTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ifpb.edu.clai.exception.AgendamentoInvalidoException;
import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.RelatorioAtendimento;
import br.ifpb.edu.clai.service.AgendamentoService;
import br.ifpb.edu.clai.service.AlunoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {

    private final AgendamentoService agendamentoService;
    private final AlunoService alunoService;

    public AgendamentoController(
            AgendamentoService agendamentoService,
            AlunoService alunoService) {

        this.agendamentoService = agendamentoService;
        this.alunoService = alunoService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("agendamento", new Agendamento());
        model.addAttribute("alunos", alunoService.listarTodos());

        return "agendamentos/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("agendamento") Agendamento agendamento,
            BindingResult resultado,
            Model model) {

        if (resultado.hasErrors()) {
            model.addAttribute("alunos", alunoService.listarTodos());
            return "agendamentos/formulario";
        }

        agendamentoService.salvar(agendamento);

        return "redirect:/atendimentos";
    }

    @GetMapping("/{id}")
    public String visualizar(
            @PathVariable String id,
            Model model) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("aluno",
                alunoService.buscarPorId(agendamento.getAlunoId()).orElse(null));

        return "agendamentos/detalhes";
    }

    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable String id,
            Model model) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("alunos", alunoService.listarTodos());

        return "agendamentos/formulario";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable String id,
            @Valid @ModelAttribute("agendamento") Agendamento agendamento,
            BindingResult resultado,
            Model model) {

        agendamento.setId(id);

        if (resultado.hasErrors()) {
            model.addAttribute("alunos", alunoService.listarTodos());
            return "agendamentos/formulario";
        }

        agendamentoService.atualizar(id, agendamento);

        return "redirect:/atendimentos";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable String id) {
        agendamentoService.cancelar(id);

        return "redirect:/atendimentos";
    }

    @GetMapping("/{id}/finalizar")
    public String exibirFormularioFinalizacao(
            @PathVariable String id,
            Model model) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        RelatorioAtendimento relatorio = new RelatorioAtendimento();
        relatorio.setAlunoId(agendamento.getAlunoId());
        relatorio.setDataAtendimento(agendamento.getData());
        relatorio.setProfissionalResponsavel(
                agendamento.getProfissionalResponsavel());
        relatorio.setModalidade(agendamento.getModalidade());

        model.addAttribute("agendamento", agendamento);
        model.addAttribute("aluno", alunoService.buscarPorId(agendamento.getAlunoId()).orElse(null));
        model.addAttribute("relatorio", relatorio);

        return "agendamentos/finalizar";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(
            @PathVariable String id,
            @Valid @ModelAttribute("relatorio") RelatorioAtendimento relatorio,
            BindingResult resultado,
            @RequestParam(defaultValue = "false") boolean adicionarAAgenda,
            @RequestParam(required = false) LocalTime horaInicio,
            @RequestParam(required = false) LocalTime horaFim,
            @RequestParam(required = false) String cor,
            Model model) {

        Agendamento agendamento = agendamentoService.buscarPorId(id);

        model.addAttribute("adicionarAAgenda", adicionarAAgenda);
        model.addAttribute("horaInicio", horaInicio);
        model.addAttribute("horaFim", horaFim);
        model.addAttribute("cor", cor);

        if (resultado.hasErrors()) {
            model.addAttribute("agendamento", agendamento);
            model.addAttribute("aluno",
                    alunoService.buscarPorId(agendamento.getAlunoId()).orElse(null));
            return "agendamentos/finalizar";
        }

        try {
            agendamentoService.finalizar(
                    id, relatorio, adicionarAAgenda, horaInicio, horaFim, cor);
            return "redirect:/atendimentos";

        } catch (AgendamentoInvalidoException exception) {
            model.addAttribute("agendamento", agendamento);
            model.addAttribute("aluno",
                    alunoService.buscarPorId(agendamento.getAlunoId()).orElse(null));
            model.addAttribute("mensagemErro", exception.getMessage());
            return "agendamentos/finalizar";
        }
    }

}
