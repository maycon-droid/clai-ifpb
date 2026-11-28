package br.ifpb.edu.clai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ifpb.edu.clai.exception.AgendamentoInvalidoException;
import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.RelatorioAtendimento;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.StatusAgendamento;
import br.ifpb.edu.clai.repository.AgendamentoRepository;

public class AgendamentoServiceTest {

    private AgendamentoRepository repository;
    private AlunoService alunoService;
    private RelatorioAtendimentoService relatorioService;
    private AgendamentoService service;

    @BeforeEach
    void preparar() {
        repository = mock(AgendamentoRepository.class);
        alunoService = mock(AlunoService.class);
        relatorioService = mock(RelatorioAtendimentoService.class);

        service = new AgendamentoService(
                repository, alunoService, relatorioService);

        when(repository.save(any(Agendamento.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    private Agendamento novoAgendamento() {
        Agendamento agendamento = new Agendamento();
        agendamento.setAlunoId("a1");
        agendamento.setProfissionalResponsavel("Profissional 1");
        agendamento.setData(LocalDate.of(2026, 12, 10));
        agendamento.setHoraInicio(LocalTime.of(8, 0));
        agendamento.setHoraFim(LocalTime.of(9, 0));
        agendamento.setModalidade("Presencial");
        agendamento.setCor("#3498DB");
        return agendamento;
    }

    @Test
    void deveCadastrarAgendamento() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        Agendamento entrada = novoAgendamento();
        entrada.setId("id-inadequado");

        Agendamento salvo = service.salvar(entrada);

        assertNull(salvo.getId());
        assertEquals(StatusAgendamento.AGENDADO, salvo.getStatus());
        assertNull(salvo.getRelatorioId());

        verify(repository).save(entrada);
    }

    @Test
    void naoDeveCadastrarParaAlunoInexistente() {
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.empty());

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.salvar(novoAgendamento()));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void naoDevePermitirConflitoDeHorarioDoProfissional() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(repository
                .findByDataAndProfissionalResponsavelAndStatusNot(
                        any(), any(), any()))
                .thenReturn(List.of(existente));

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.salvar(novoAgendamento()));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void naoDevePermitirConflitoDeHorarioDoAluno() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");
        existente.setProfissionalResponsavel("Outro profissional");

        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(repository
                .findByDataAndAlunoIdAndStatusNot(
                        any(), any(), any()))
                .thenReturn(List.of(existente));

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.salvar(novoAgendamento()));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void devePermitirHorariosConsecutivos() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        Agendamento novo = novoAgendamento();
        novo.setHoraInicio(LocalTime.of(9, 0));
        novo.setHoraFim(LocalTime.of(10, 0));

        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));

        when(repository
                .findByDataAndProfissionalResponsavelAndStatusNot(
                        any(), any(), any()))
                .thenReturn(List.of(existente));

        when(repository
                .findByDataAndAlunoIdAndStatusNot(
                        any(), any(), any()))
                .thenReturn(List.of(existente));

        assertDoesNotThrow(() -> service.salvar(novo));

        verify(repository).save(novo);
    }

    @Test
    void deveCancelarAgendamento() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        Agendamento cancelado = service.cancelar("ag1");

        assertEquals(StatusAgendamento.CANCELADO, cancelado.getStatus());
        verify(repository).save(existente);
    }

    @Test
    void naoDeveCancelarAgendamentoJaCancelado() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");
        existente.setStatus(StatusAgendamento.CANCELADO);

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.cancelar("ag1"));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveFinalizarAgendamentoComRelatorio() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        RelatorioAtendimento dados = new RelatorioAtendimento();
        dados.setTitulo("Atendimento realizado");
        dados.setTipoAtendimento("Acompanhamento");
        dados.setAtividadesRealizadas("Atividades desenvolvidas");
        dados.setNecessitaAcompanhamento(false);

        RelatorioAtendimento relatorioSalvo = new RelatorioAtendimento();
        relatorioSalvo.setId("rel1");

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        when(relatorioService.registrar(
                eq("a1"), any(RelatorioAtendimento.class)))
                .thenReturn(relatorioSalvo);

        Agendamento finalizado = service.finalizar("ag1", dados);

        assertEquals(StatusAgendamento.REALIZADO, finalizado.getStatus());
        assertEquals("rel1", finalizado.getRelatorioId());
        assertEquals(existente.getData(), dados.getDataAtendimento());
        assertEquals(
                existente.getProfissionalResponsavel(),
                dados.getProfissionalResponsavel());
        assertEquals(existente.getModalidade(), dados.getModalidade());

        verify(relatorioService).registrar("a1", dados);
        verify(repository).save(existente);
    }

    @Test
    void naoDeveFinalizarSemDadosObrigatoriosDoRelatorio() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        RelatorioAtendimento dados = new RelatorioAtendimento();
        dados.setTitulo("Atendimento");

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.finalizar("ag1", dados));

        verify(relatorioService, never())
                .registrar(any(), any());
        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void naoDeveFinalizarAgendamentoCancelado() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");
        existente.setStatus(StatusAgendamento.CANCELADO);

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.finalizar("ag1", new RelatorioAtendimento()));

        verify(relatorioService, never())
                .registrar(any(), any());
        verify(repository, never()).save(any(Agendamento.class));
    }


    @Test
    void deveAtualizarAgendamentoSemConflitoComEleMesmo() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        Agendamento dados = novoAgendamento();
        dados.setHoraInicio(LocalTime.of(10, 0));
        dados.setHoraFim(LocalTime.of(11, 0));
        dados.setCor("#FF0000");

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));
        when(alunoService.buscarPorId("a1"))
                .thenReturn(Optional.of(new Aluno()));
        when(repository.findByDataAndProfissionalResponsavelAndStatusNot(
                any(), any(), any()))
                .thenReturn(List.of(existente));
        when(repository.findByDataAndAlunoIdAndStatusNot(
                any(), any(), any()))
                .thenReturn(List.of(existente));

        Agendamento atualizado = service.atualizar("ag1", dados);

        assertEquals(LocalTime.of(10, 0), atualizado.getHoraInicio());
        assertEquals("#FF0000", atualizado.getCor());
        assertEquals(StatusAgendamento.AGENDADO, atualizado.getStatus());

        verify(repository).save(existente);
    }

    @Test
    void naoDeveEditarAgendamentoRealizado() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");
        existente.setStatus(StatusAgendamento.REALIZADO);

        when(repository.findById("ag1"))
                .thenReturn(Optional.of(existente));

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.atualizar("ag1", novoAgendamento()));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveListarAgendamentosPorPeriodo() {
        LocalDate inicio = LocalDate.of(2026, 12, 1);
        LocalDate fim = LocalDate.of(2026, 12, 31);

        when(repository.findByDataBetweenOrderByDataAscHoraInicioAsc(
                inicio, fim))
                .thenReturn(List.of(novoAgendamento()));

        List<Agendamento> resultado =
                service.listarPorPeriodo(inicio, fim);

        assertEquals(1, resultado.size());
        verify(repository)
                .findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim);
    }

    @Test
    void naoDeveListarPeriodoInvalido() {
        LocalDate inicio = LocalDate.of(2026, 12, 31);
        LocalDate fim = LocalDate.of(2026, 12, 1);

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.listarPorPeriodo(inicio, fim));

        verify(repository, never())
                .findByDataBetweenOrderByDataAscHoraInicioAsc(any(), any());
    }

    @Test
    void naoDeveCadastrarComHorarioFinalInvalido() {
        Agendamento agendamento = novoAgendamento();
        agendamento.setHoraFim(agendamento.getHoraInicio());

        assertThrows(
                AgendamentoInvalidoException.class,
                () -> service.salvar(agendamento));

        verify(repository, never()).save(any(Agendamento.class));
    }

    @Test
    void deveCriarProximoAgendamentoAoFinalizar() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        RelatorioAtendimento dados = new RelatorioAtendimento();
        dados.setTitulo("Atendimento realizado");
        dados.setTipoAtendimento("Acompanhamento");
        dados.setAtividadesRealizadas("Orientação ao aluno");
        dados.setNecessitaAcompanhamento(true);
        dados.setDataProximoAcompanhamento(LocalDate.of(2026, 12, 15));

        RelatorioAtendimento relatorioSalvo = new RelatorioAtendimento();
        relatorioSalvo.setId("rel1");

        when(repository.findById("ag1")).thenReturn(Optional.of(existente));
        when(alunoService.buscarPorId("a1")).thenReturn(Optional.of(new Aluno()));
        when(repository.findByDataAndProfissionalResponsavelAndStatusNot(any(), any(), any()))
                .thenReturn(List.of());
        when(repository.findByDataAndAlunoIdAndStatusNot(any(), any(), any()))
                .thenReturn(List.of());
        when(relatorioService.registrar(eq("a1"), any(RelatorioAtendimento.class)))
                .thenReturn(relatorioSalvo);

        Agendamento finalizado = service.finalizar(
                "ag1", dados, true,
                LocalTime.of(10, 0), LocalTime.of(11, 0), "#FF0000");

        assertEquals(StatusAgendamento.REALIZADO, finalizado.getStatus());
        assertEquals("rel1", finalizado.getRelatorioId());

        org.mockito.ArgumentCaptor<Agendamento> captor =
                org.mockito.ArgumentCaptor.forClass(Agendamento.class);
        verify(repository, times(2)).save(captor.capture());

        Agendamento proximo = captor.getAllValues().get(1);
        assertEquals("a1", proximo.getAlunoId());
        assertEquals("Profissional 1", proximo.getProfissionalResponsavel());
        assertEquals(LocalDate.of(2026, 12, 15), proximo.getData());
        assertEquals(LocalTime.of(10, 0), proximo.getHoraInicio());
        assertEquals(LocalTime.of(11, 0), proximo.getHoraFim());
        assertEquals("Presencial", proximo.getModalidade());
        assertEquals("#FF0000", proximo.getCor());
        assertEquals(StatusAgendamento.AGENDADO, proximo.getStatus());
        verify(relatorioService).registrar("a1", dados);
    }

    @Test
    void naoDeveFinalizarNemRegistrarRelatorioQuandoProximoAgendamentoTemConflito() {
        Agendamento existente = novoAgendamento();
        existente.setId("ag1");

        Agendamento conflito = novoAgendamento();
        conflito.setId("outro");
        conflito.setData(LocalDate.of(2026, 12, 15));
        conflito.setHoraInicio(LocalTime.of(10, 0));
        conflito.setHoraFim(LocalTime.of(11, 0));

        RelatorioAtendimento dados = new RelatorioAtendimento();
        dados.setTitulo("Atendimento realizado");
        dados.setTipoAtendimento("Acompanhamento");
        dados.setAtividadesRealizadas("Orientação ao aluno");
        dados.setNecessitaAcompanhamento(true);
        dados.setDataProximoAcompanhamento(LocalDate.of(2026, 12, 15));

        when(repository.findById("ag1")).thenReturn(Optional.of(existente));
        when(alunoService.buscarPorId("a1")).thenReturn(Optional.of(new Aluno()));
        when(repository.findByDataAndProfissionalResponsavelAndStatusNot(any(), any(), any()))
                .thenReturn(List.of(conflito));

        assertThrows(AgendamentoInvalidoException.class, () -> service.finalizar(
                "ag1", dados, true,
                LocalTime.of(10, 0), LocalTime.of(11, 0), "#FF0000"));

        verify(relatorioService, never()).registrar(any(), any());
        verify(repository, never()).save(any(Agendamento.class));
    }

}
