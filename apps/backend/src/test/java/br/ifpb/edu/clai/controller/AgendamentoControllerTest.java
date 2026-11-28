package br.ifpb.edu.clai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.RelatorioAtendimento;
import br.ifpb.edu.clai.service.AgendamentoService;
import br.ifpb.edu.clai.service.AlunoService;

class AgendamentoControllerTest {

    private AgendamentoService agendamentoService;
    private AlunoService alunoService;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        agendamentoService = mock(AgendamentoService.class);
        alunoService = mock(AlunoService.class);

        mvc = MockMvcBuilders
                .standaloneSetup(new AgendamentoController(
                        agendamentoService, alunoService))
                .build();
    }

    private Agendamento agendamentoValido() {
        Agendamento agendamento = new Agendamento();
        agendamento.setId("ag1");
        agendamento.setAlunoId("a1");
        agendamento.setProfissionalResponsavel("Carlos Lima");
        agendamento.setData(LocalDate.now().plusDays(1));
        agendamento.setHoraInicio(java.time.LocalTime.of(9, 0));
        agendamento.setHoraFim(java.time.LocalTime.of(10, 0));
        agendamento.setModalidade("Presencial");
        agendamento.setCor("#3498DB");
        return agendamento;
    }

    private org.springframework.test.web.servlet.request
            .MockHttpServletRequestBuilder comDadosValidos(
                    org.springframework.test.web.servlet.request
                            .MockHttpServletRequestBuilder req) {

        return req
                .param("alunoId", "a1")
                .param("profissionalResponsavel", "Carlos Lima")
                .param("data", LocalDate.now().plusDays(1).toString())
                .param("horaInicio", "09:00")
                .param("horaFim", "10:00")
                .param("modalidade", "Presencial")
                .param("cor", "#3498DB");
    }

    @Test
    void novoDeveAbrirFormularioComAlunos() throws Exception {
        when(alunoService.listarTodos()).thenReturn(List.of());

        mvc.perform(get("/agendamentos/novo"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/formulario"))
                .andExpect(model().attributeExists("agendamento", "alunos"));
    }

    @Test
    void salvarValidoDeveCriarERedirecionar() throws Exception {
        mvc.perform(comDadosValidos(post("/agendamentos/salvar")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atendimentos"));

        verify(agendamentoService).salvar(any(Agendamento.class));
    }

    @Test
    void salvarSemCamposObrigatoriosDeveReexibirFormulario()
            throws Exception {

        when(alunoService.listarTodos()).thenReturn(List.of());

        mvc.perform(post("/agendamentos/salvar"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/formulario"))
                .andExpect(model().attributeExists("alunos"))
                .andExpect(model().attributeHasFieldErrors(
                        "agendamento",
                        "alunoId",
                        "profissionalResponsavel",
                        "data",
                        "horaInicio",
                        "horaFim",
                        "modalidade",
                        "cor"));

        verify(agendamentoService, never()).salvar(any());
    }

    @Test
    void visualizarDeveExibirAgendamentoEAluno() throws Exception {
        Agendamento agendamento = agendamentoValido();

        when(agendamentoService.buscarPorId("ag1"))
                .thenReturn(agendamento);
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        mvc.perform(get("/agendamentos/ag1"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/detalhes"))
                .andExpect(model().attributeExists("agendamento", "aluno"));
    }

    @Test
    void editarGetDeveAbrirFormulario() throws Exception {
        when(agendamentoService.buscarPorId("ag1"))
                .thenReturn(agendamentoValido());
        when(alunoService.listarTodos()).thenReturn(List.of());

        mvc.perform(get("/agendamentos/ag1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/formulario"))
                .andExpect(model().attributeExists("agendamento", "alunos"));
    }

    @Test
    void editarValidoDeveAtualizarERedirecionar() throws Exception {
        mvc.perform(comDadosValidos(post("/agendamentos/ag1/editar")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atendimentos"));

        verify(agendamentoService)
                .atualizar(eq("ag1"), any(Agendamento.class));
    }

    @Test
    void cancelarDeveCancelarERedirecionar() throws Exception {
        mvc.perform(post("/agendamentos/ag1/cancelar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atendimentos"));

        verify(agendamentoService).cancelar("ag1");
    }

    @Test
    void finalizarGetDeveAbrirFormularioComDadosDoAgendamento()
            throws Exception {

        Agendamento agendamento = agendamentoValido();

        when(agendamentoService.buscarPorId("ag1"))
                .thenReturn(agendamento);

        mvc.perform(get("/agendamentos/ag1/finalizar"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/finalizar"))
                .andExpect(model().attributeExists(
                        "agendamento", "relatorio"))
                .andExpect(model().attribute(
                        "relatorio",
                        org.hamcrest.Matchers.hasProperty(
                                "alunoId",
                                org.hamcrest.Matchers.is("a1"))));
    }

    @Test
    void finalizarValidoDeveRegistrarRelatorioERedirecionar()
            throws Exception {

        mvc.perform(post("/agendamentos/ag1/finalizar")
                .param("alunoId", "a1")
                .param("titulo", "Atendimento individual")
                .param("dataAtendimento", LocalDate.now().toString())
                .param("profissionalResponsavel", "Carlos Lima")
                .param("modalidade", "Presencial")
                .param("tipoAtendimento", "Acompanhamento")
                .param("atividadesRealizadas", "Orientação ao aluno")
                .param("necessitaAcompanhamento", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/atendimentos"));

        verify(agendamentoService)
                .finalizar(eq("ag1"), any(RelatorioAtendimento.class),
                eq(false), isNull(), isNull(), isNull());
    }

    @Test
    void finalizarSemDadosObrigatoriosDeveReexibirFormulario()
            throws Exception {

        when(agendamentoService.buscarPorId("ag1"))
                .thenReturn(agendamentoValido());

        mvc.perform(post("/agendamentos/ag1/finalizar"))
                .andExpect(status().isOk())
                .andExpect(view().name("agendamentos/finalizar"))
                .andExpect(model().attributeExists("agendamento"))
                .andExpect(model().attributeHasFieldErrors(
                        "relatorio",
                        "titulo",
                        "tipoAtendimento",
                        "atividadesRealizadas",
                        "necessitaAcompanhamento"));

        verify(agendamentoService, never())
                .finalizar(eq("ag1"), any(RelatorioAtendimento.class),
                eq(false), isNull(), isNull(), isNull());
    }
}
