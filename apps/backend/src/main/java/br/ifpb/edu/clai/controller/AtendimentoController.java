package br.ifpb.edu.clai.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.StatusAgendamento;
import br.ifpb.edu.clai.service.AgendamentoService;
import br.ifpb.edu.clai.service.AlunoService;

@Controller
@RequestMapping("/atendimentos")
public class AtendimentoController {

    private final AgendamentoService agendamentoService;
    private final AlunoService alunoService;

    public AtendimentoController(
            AgendamentoService agendamentoService,
            AlunoService alunoService) {

        this.agendamentoService = agendamentoService;
        this.alunoService = alunoService;
    }

    @GetMapping
    public String calendario(
            @RequestParam(required = false) String mes,
            Model model) {

        YearMonth periodo = obterPeriodo(mes);

        LocalDate hoje = LocalDate.now();
        List<Agendamento> agendaHoje =
                agendamentoService.listarPorData(hoje);

        LocalDate inicio = periodo.atDay(1);
        LocalDate fim = periodo.atEndOfMonth();

        List<Agendamento> agendamentos =
                agendamentoService.listarPorPeriodo(inicio, fim);

        Map<String, String> nomesAlunos = new HashMap<>();

        for (Aluno aluno : alunoService.listarTodos()) {
            nomesAlunos.put(aluno.getId(), aluno.getNome());
        }

        long totalAgendados = agendamentos.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.AGENDADO)
                .filter(a -> !a.getData().isBefore(LocalDate.now()))
                .count();

        long totalRealizados = agendamentos.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.REALIZADO)
                .count();

        long totalPendentes = agendamentos.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.AGENDADO)
                .filter(a -> a.getData().isBefore(LocalDate.now()))
                .count();

        long totalCancelados = agendamentos.stream()
                .filter(a -> a.getStatus() == StatusAgendamento.CANCELADO)
                .count();

        List<LocalDate> diasCalendario = gerarDiasCalendario(periodo);

        model.addAttribute("agendamentos", agendamentos);
        model.addAttribute("agendaHoje", agendaHoje);
        model.addAttribute("hoje", hoje);
        model.addAttribute("nomesAlunos", nomesAlunos);
        model.addAttribute("diasCalendario", diasCalendario);
        model.addAttribute("mesAtual", periodo);
        model.addAttribute("mesAnterior", periodo.minusMonths(1));
        model.addAttribute("mesSeguinte", periodo.plusMonths(1));

        model.addAttribute("totalAgendados", totalAgendados);
        model.addAttribute("totalRealizados", totalRealizados);
        model.addAttribute("totalPendentes", totalPendentes);
        model.addAttribute("totalCancelados", totalCancelados);

        return "atendimentos/calendario";
    }

    private YearMonth obterPeriodo(String mes) {
        if (mes == null || mes.isBlank()) {
            return YearMonth.now();
        }

        try {
            return YearMonth.parse(mes);
        } catch (DateTimeParseException exception) {
            return YearMonth.now();
        }
    }

    private List<LocalDate> gerarDiasCalendario(YearMonth periodo) {
        LocalDate primeiroDia = periodo.atDay(1);
        LocalDate ultimoDia = periodo.atEndOfMonth();

        LocalDate inicioGrade = primeiroDia.minusDays(
                primeiroDia.getDayOfWeek().getValue() - 1);

        LocalDate fimGrade = ultimoDia.plusDays(
                7 - ultimoDia.getDayOfWeek().getValue());

        List<LocalDate> dias = new ArrayList<>();

        for (LocalDate dia = inicioGrade;
                !dia.isAfter(fimGrade);
                dia = dia.plusDays(1)) {

            dias.add(dia);
        }

        return dias;
    }
}
