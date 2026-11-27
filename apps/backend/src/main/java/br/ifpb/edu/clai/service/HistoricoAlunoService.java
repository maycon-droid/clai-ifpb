package br.ifpb.edu.clai.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.dto.EventoHistorico;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;

//1
@Service
public class HistoricoAlunoService {
    private final EstudoDeCasoService estudoDeCasoService;
    private final RelatorioAtendimentoService relatorioAtendimentoService;
    private final AlunoService alunoService;

    public HistoricoAlunoService(EstudoDeCasoService estudoDeCasoService,
            RelatorioAtendimentoService relatorioAtendimentoService, AlunoService alunoService) {
        this.estudoDeCasoService = estudoDeCasoService;
        this.relatorioAtendimentoService = relatorioAtendimentoService;
        this.alunoService = alunoService;
    }

    public List<EventoHistorico> buscarHistoricoAluno(String alunoId) {
        List<EventoHistorico> historico = new ArrayList<>();

        alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException("Aluno não encontrado"));

        var estudosDeCaso = estudoDeCasoService.listarPorAluno(alunoId);
        for (EstudoDeCaso estudo : estudosDeCaso) {
            if (estudo.getDatasAtendimentoAnamnese() != null) {
                estudo.getDatasAtendimentoAnamnese().forEach(data -> {
                    EventoHistorico evento = new EventoHistorico();
                    evento.setData(data);
                    evento.setTipo("estudos-de-caso");
                    evento.setTitulo("Atendimento de Anamnese");
                    evento.setDescricao("Atendimento de anamnese realizado no estudo de caso.");
                    evento.setOrigemId(estudo.getId());
                    evento.setResponsavel(estudo.getProfissionalResponsavel());
                    historico.add(evento);
                });
            }
        }

        var relatorios = relatorioAtendimentoService.listarPorAluno(alunoId);
        for (var relatorio : relatorios) {
            if (relatorio.getAlunoId() == null || !relatorio.getAlunoId().equals(alunoId)) {
                continue;
            }
            EventoHistorico evento = new EventoHistorico();
            evento.setData(relatorio.getDataAtendimento());
            evento.setTipo("Relatório de Atendimento");
            evento.setTitulo("Relatório de Atendimento");
            evento.setDescricao(relatorio.getObjetivo() != null ? relatorio.getObjetivo()
                    : "Relatório de atendimento sem objetivo definido.");
            evento.setOrigemId(relatorio.getId());
            evento.setResponsavel(relatorio.getProfissionalResponsavel());
            historico.add(evento);
        }
        historico.sort(
                Comparator.comparing(
                        EventoHistorico::getData,
                        Comparator.nullsLast(Comparator.reverseOrder())));
        return historico;
    }

    public String buscarNomeAluno(String alunoId) {
        Aluno aluno = alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        return aluno.getNome();
    }

    public String buscarMatriculaAluno(String alunoId) {
        Aluno aluno = alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado: " + alunoId));

        return aluno.getMatricula().getNumeroMatricula();
    }
}
