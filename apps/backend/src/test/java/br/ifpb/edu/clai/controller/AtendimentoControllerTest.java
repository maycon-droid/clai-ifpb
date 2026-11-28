package br.ifpb.edu.clai.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.StatusAgendamento;
import br.ifpb.edu.clai.service.AgendamentoService;
import br.ifpb.edu.clai.service.AlunoService;

class AtendimentoControllerTest {

    private AgendamentoService agendamentoService;
    private AlunoService alunoService;
    private Model model;
    private AtendimentoController controller;

    @BeforeEach
    void setUp() {
        agendamentoService = mock(AgendamentoService.class);
        alunoService = mock(AlunoService.class);
        model = mock(Model.class);

        controller = new AtendimentoController(
                agendamentoService,
                alunoService);

        when(alunoService.listarTodos()).thenReturn(List.of());
    }

    @Test
    void deveExibirCalendarioComIndicadores() {
        LocalDate hoje = LocalDate.now();

        Agendamento agendado = criarAgendamento(
                hoje, StatusAgendamento.AGENDADO);

        Agendamento realizado = criarAgendamento(
                hoje, StatusAgendamento.REALIZADO);

        Agendamento cancelado = criarAgendamento(
                hoje, StatusAgendamento.CANCELADO);

        when(agendamentoService.listarPorPeriodo(
                YearMonth.now().atDay(1),
                YearMonth.now().atEndOfMonth()))
                .thenReturn(List.of(agendado, realizado, cancelado));

        String view = controller.calendario(
                YearMonth.now().toString(), model);

        assertEquals("atendimentos/calendario", view);

        verify(model).addAttribute("totalAgendados", 1L);
        verify(model).addAttribute("totalRealizados", 1L);
        verify(model).addAttribute("totalPendentes", 0L);
        verify(model).addAttribute("totalCancelados", 1L);
    }

    @Test
    void deveContarAgendamentoAtrasadoComoPendente() {
        LocalDate ontem = LocalDate.now().minusDays(1);
        YearMonth periodo = YearMonth.from(ontem);

        Agendamento pendente = criarAgendamento(
                ontem, StatusAgendamento.AGENDADO);

        when(agendamentoService.listarPorPeriodo(
                periodo.atDay(1),
                periodo.atEndOfMonth()))
                .thenReturn(List.of(pendente));

        String view = controller.calendario(
                periodo.toString(), model);

        assertEquals("atendimentos/calendario", view);

        verify(model).addAttribute("totalAgendados", 0L);
        verify(model).addAttribute("totalPendentes", 1L);
    }

    private Agendamento criarAgendamento(
            LocalDate data, StatusAgendamento status) {

        Agendamento agendamento = new Agendamento();
        agendamento.setData(data);
        agendamento.setStatus(status);

        return agendamento;
    }
}
