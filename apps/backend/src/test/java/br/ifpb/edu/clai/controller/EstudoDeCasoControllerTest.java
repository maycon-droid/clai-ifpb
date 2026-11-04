package br.ifpb.edu.clai.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.service.AlunoService;
import br.ifpb.edu.clai.service.EstudoDeCasoService;

public class EstudoDeCasoControllerTest {
    
    private EstudoDeCasoService estudoService;
    private AlunoService alunoService;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        estudoService = mock(EstudoDeCasoService.class);
        alunoService = mock(AlunoService.class);
        mvc = MockMvcBuilders
                .standaloneSetup(new EstudoDeCasoController(estudoService, alunoService))
                .build();
    }

    @Test
    void listarDeveExporEstudosENomesDosAlunos() throws Exception {
        when(estudoService.listarTodos()).thenReturn(List.of(new EstudoDeCaso()));
        when(alunoService.listarTodos()).thenReturn(List.of());

        mvc.perform(get("/estudos-de-caso"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudos-de-caso/lista"))
                .andExpect(model().attributeExists("estudos", "nomesAlunos"));
    }

    @Test
    void novoSemAlunoDeveRedirecionarParaAlunos() throws Exception {
        mvc.perform(get("/estudos-de-caso/novo"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos"));
    }

    @Test
    void novoComAlunoDeveAbrirFormularioComAninhadosInicializados() throws Exception {
        when(alunoService.buscarPorId("a1")).thenReturn(Optional.of(new Aluno()));

        mvc.perform(get("/estudos-de-caso/novo").param("alunoId", "a1"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudos-de-caso/form"))
                .andExpect(model().attributeExists("estudo", "aluno"))
                .andExpect(model().attribute("estudo",
                        org.hamcrest.Matchers.hasProperty("encaminhamento",
                                org.hamcrest.Matchers.notNullValue())));
    }

    @Test
    void salvarValidoDeveCriarERedirecionar() throws Exception {
        mvc.perform(post("/estudos-de-caso/salvar").param("alunoId", "a1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudos-de-caso"));

        verify(estudoService).criar(eq("a1"), any(EstudoDeCaso.class));
    }

        @Test
    void salvarSemAlunoDeveRedirecionarParaAlunosSemChamarOServico() throws Exception {
        mvc.perform(post("/estudos-de-caso/salvar"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/alunos"));

        verify(estudoService, never()).criar(any(), any());
        verify(alunoService, never()).buscarPorId(any());
    }

    @Test
    void editarComErroDeveReexibirFormularioComAlunoOriginal() throws Exception {
        EstudoDeCaso existente = new EstudoDeCaso();
        existente.setId("e1");
        existente.setAlunoId("a1");
        when(estudoService.buscarPorId("e1")).thenReturn(existente);
        when(alunoService.buscarPorId("a1")).thenReturn(Optional.of(new Aluno()));

        mvc.perform(post("/estudos-de-caso/e1/editar"))
                .andExpect(status().isOk())
                .andExpect(view().name("estudos-de-caso/form"));

        verify(estudoService, never()).atualizar(any(), any());
    }

    @Test
    void editarPostDeveAtualizarERedirecionar() throws Exception {
        mvc.perform(post("/estudos-de-caso/e1/editar").param("alunoId", "a1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/estudos-de-caso"));

        verify(estudoService).atualizar(eq("e1"), any(EstudoDeCaso.class));
    }

}
