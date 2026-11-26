package br.ifpb.edu.clai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ifpb.edu.clai.dto.EventoHistorico;
import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.model.RelatorioAtendimento;

class HistoricoAlunoServiceTest {

    private EstudoDeCasoService estudoDeCasoService;
    private RelatorioAtendimentoService relatorioAtendimentoService;
    private AlunoService alunoService;
    private HistoricoAlunoService service;

    @BeforeEach
    void preparar() {
        estudoDeCasoService = mock(EstudoDeCasoService.class);
        relatorioAtendimentoService = mock(RelatorioAtendimentoService.class);
        alunoService = mock(AlunoService.class);

        service = new HistoricoAlunoService(
                estudoDeCasoService,
                relatorioAtendimentoService,
                alunoService);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoExiste() {
        when(alunoService.buscarPorId("x"))
                .thenReturn(Optional.empty());

        assertThrows(
                AlunoNaoEncontradoException.class,
                () -> service.buscarHistoricoAluno("x"));

        verifyNoInteractions(estudoDeCasoService, relatorioAtendimentoService);
    }

    @Test
    void deveRetornarHistoricoVazioQuandoAlunoNaoPossuiRegistros() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(estudoDeCasoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        when(relatorioAtendimentoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        List<EventoHistorico> historico = service.buscarHistoricoAluno("a1");

        assertNotNull(historico);
        assertTrue(historico.isEmpty());
    }

    @Test
    void deveAdicionarEventosDeAnamneseAoHistorico() {
        Aluno aluno = new Aluno();
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(aluno));

        EstudoDeCaso estudo = new EstudoDeCaso();
        estudo.setId("e1");
        estudo.setDatasAtendimentoAnamnese(
                List.of(
                        LocalDate.of(2026, 10, 10),
                        LocalDate.of(2026, 10, 15)));

        when(estudoDeCasoService.listarPorAluno("a1"))
                .thenReturn(List.of(estudo));

        when(relatorioAtendimentoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        List<EventoHistorico> historico = service.buscarHistoricoAluno("a1");

        assertEquals(2, historico.size());
        assertEquals("Estudo de Caso", historico.get(0).getTipo());
        assertEquals("e1", historico.get(0).getOrigemId());
    }

    @Test
    void deveAdicionarRelatorioDeAtendimentoAoHistorico() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(estudoDeCasoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        RelatorioAtendimento relatorio = new RelatorioAtendimento();
        relatorio.setId("r1");
        relatorio.setAlunoId("a1");
        relatorio.setDataAtendimento(LocalDate.of(2026, 10, 20));
        relatorio.setObjetivo("Acompanhar o desenvolvimento do aluno");

        when(relatorioAtendimentoService.listarPorAluno("a1"))
                .thenReturn(List.of(relatorio));

        List<EventoHistorico> historico = service.buscarHistoricoAluno("a1");

        assertEquals(1, historico.size());
        assertEquals("Relatório de Atendimento", historico.get(0).getTipo());
        assertEquals("r1", historico.get(0).getOrigemId());
        assertEquals("Acompanhar o desenvolvimento do aluno",
                historico.get(0).getDescricao());
    }

    @Test
    void deveOrdenarHistoricoDoMaisRecenteParaOMaisAntigo() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(estudoDeCasoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        RelatorioAtendimento antigo = new RelatorioAtendimento();
        antigo.setId("r1");
        antigo.setAlunoId("a1");
        antigo.setDataAtendimento(LocalDate.of(2026, 10, 10));

        RelatorioAtendimento recente = new RelatorioAtendimento();
        recente.setId("r2");
        recente.setAlunoId("a1");
        recente.setDataAtendimento(LocalDate.of(2026, 10, 20));

        when(relatorioAtendimentoService.listarPorAluno("a1"))
                .thenReturn(List.of(antigo, recente));

        List<EventoHistorico> historico = service.buscarHistoricoAluno("a1");

        assertEquals(2, historico.size());
        assertEquals("r2", historico.get(0).getOrigemId());
        assertEquals("r1", historico.get(1).getOrigemId());
    }

    @Test
    void deveColocarEventosSemDataNoFinalDoHistorico() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(estudoDeCasoService.listarPorAluno("a1"))
                .thenReturn(List.of());

        RelatorioAtendimento semData = new RelatorioAtendimento();
        semData.setId("r1");
        semData.setAlunoId("a1");
        semData.setDataAtendimento(null);

        RelatorioAtendimento comData = new RelatorioAtendimento();
        comData.setId("r2");
        comData.setAlunoId("a1");
        comData.setDataAtendimento(LocalDate.of(2026, 10, 20));

        when(relatorioAtendimentoService.listarPorAluno("a1"))
                .thenReturn(List.of(semData, comData));

        List<EventoHistorico> historico = service.buscarHistoricoAluno("a1");

        assertEquals("r2", historico.get(0).getOrigemId());
        assertEquals("r1", historico.get(1).getOrigemId());
    }
    
}