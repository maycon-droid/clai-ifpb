package br.ifpb.edu.clai.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.exception.AgendamentoInvalidoException;
import br.ifpb.edu.clai.exception.AgendamentoNaoEncontradoException;
import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.StatusAgendamento;
import br.ifpb.edu.clai.repository.AgendamentoRepository;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final AlunoService alunoService;
    private final RelatorioAtendimentoService relatorioAtendimentoService;

    public AgendamentoService(
            AgendamentoRepository agendamentoRepository,
            AlunoService alunoService,
            RelatorioAtendimentoService relatorioAtendimentoService) {

        this.agendamentoRepository = agendamentoRepository;
        this.alunoService = alunoService;
        this.relatorioAtendimentoService = relatorioAtendimentoService;
    }

    public Agendamento salvar(Agendamento agendamento) {
        validarDados(agendamento);
        validarAluno(agendamento.getAlunoId());
        validarConflitos(agendamento, null);

        agendamento.setId(null);
        agendamento.setStatus(StatusAgendamento.AGENDADO);
        agendamento.setRelatorioId(null);

        return agendamentoRepository.save(agendamento);
    }

    public Agendamento buscarPorId(String id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new AgendamentoNaoEncontradoException(
                        "Agendamento não encontrado para o ID: " + id));
    }

    public List<Agendamento> listarTodos() {
        return agendamentoRepository.findAll();
    }

    public List<Agendamento> listarPorData(LocalDate data) {
        if (data == null) {
            throw new AgendamentoInvalidoException(
                    "A data informada é inválida.");
        }

        return agendamentoRepository.findByDataOrderByHoraInicioAsc(data);
    }

    public List<Agendamento> listarPorPeriodo(
            LocalDate inicio, LocalDate fim) {

        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new AgendamentoInvalidoException(
                    "O período informado é inválido.");
        }

        return agendamentoRepository
                .findByDataBetweenOrderByDataAscHoraInicioAsc(inicio, fim);
    }

    public Agendamento atualizar(String id, Agendamento dados) {
        Agendamento existente = buscarPorId(id);

        if (existente.getStatus() != StatusAgendamento.AGENDADO) {
            throw new AgendamentoInvalidoException(
                    "Somente agendamentos ainda agendados podem ser editados.");
        }

        validarDados(dados);
        validarAluno(dados.getAlunoId());
        validarConflitos(dados, id);

        existente.setAlunoId(dados.getAlunoId());
        existente.setProfissionalResponsavel(dados.getProfissionalResponsavel());
        existente.setData(dados.getData());
        existente.setHoraInicio(dados.getHoraInicio());
        existente.setHoraFim(dados.getHoraFim());
        existente.setModalidade(dados.getModalidade());
        existente.setCor(dados.getCor());
        existente.setObservacoes(dados.getObservacoes());

        return agendamentoRepository.save(existente);
    }

    public Agendamento cancelar(String id) {
        Agendamento existente = buscarPorId(id);

        if (existente.getStatus() != StatusAgendamento.AGENDADO) {
            throw new AgendamentoInvalidoException(
                    "Somente agendamentos ainda agendados podem ser cancelados.");
        }

        existente.setStatus(StatusAgendamento.CANCELADO);
        return agendamentoRepository.save(existente);
    }

    public Agendamento finalizar(
            String id,
            br.ifpb.edu.clai.model.RelatorioAtendimento dadosRelatorio) {
        return finalizar(id, dadosRelatorio, false, null, null, null);
    }

    public Agendamento finalizar(
            String id,
            br.ifpb.edu.clai.model.RelatorioAtendimento dadosRelatorio,
            boolean adicionarAAgenda,
            LocalTime horaInicio,
            LocalTime horaFim,
            String cor) {

        Agendamento existente = buscarPorId(id);

        if (existente.getStatus() != StatusAgendamento.AGENDADO) {
            throw new AgendamentoInvalidoException(
                    "Somente agendamentos ainda agendados podem ser finalizados.");
        }

        if (dadosRelatorio == null
                || dadosRelatorio.getTitulo() == null
                || dadosRelatorio.getTitulo().isBlank()
                || dadosRelatorio.getTipoAtendimento() == null
                || dadosRelatorio.getTipoAtendimento().isBlank()
                || dadosRelatorio.getAtividadesRealizadas() == null
                || dadosRelatorio.getAtividadesRealizadas().isBlank()
                || dadosRelatorio.getNecessitaAcompanhamento() == null) {
            throw new AgendamentoInvalidoException(
                    "Preencha os dados obrigatórios do relatório para finalizar o atendimento.");
        }

        Agendamento proximo = null;

        if (adicionarAAgenda) {
            if (dadosRelatorio.getDataProximoAcompanhamento() == null) {
                throw new AgendamentoInvalidoException(
                        "Informe a data do próximo acompanhamento.");
            }

            proximo = new Agendamento();
            proximo.setAlunoId(existente.getAlunoId());
            proximo.setProfissionalResponsavel(existente.getProfissionalResponsavel());
            proximo.setData(dadosRelatorio.getDataProximoAcompanhamento());
            proximo.setHoraInicio(horaInicio);
            proximo.setHoraFim(horaFim);
            proximo.setModalidade(existente.getModalidade());
            proximo.setCor(cor);

            validarDados(proximo);
            validarAluno(proximo.getAlunoId());
            validarConflitos(proximo, null);

            proximo.setStatus(StatusAgendamento.AGENDADO);
            proximo.setRelatorioId(null);
        }

        dadosRelatorio.setDataAtendimento(existente.getData());
        dadosRelatorio.setProfissionalResponsavel(
                existente.getProfissionalResponsavel());
        dadosRelatorio.setModalidade(existente.getModalidade());

        br.ifpb.edu.clai.model.RelatorioAtendimento relatorioSalvo =
                relatorioAtendimentoService.registrar(
                        existente.getAlunoId(), dadosRelatorio);

        existente.setRelatorioId(relatorioSalvo.getId());
        existente.setStatus(StatusAgendamento.REALIZADO);
        Agendamento finalizado = agendamentoRepository.save(existente);

        if (proximo != null) {
            agendamentoRepository.save(proximo);
        }

        return finalizado;
    }

    private void validarDados(Agendamento agendamento) {
        if (agendamento == null) {
            throw new AgendamentoInvalidoException(
                    "Os dados do agendamento são obrigatórios.");
        }

        if (agendamento.getAlunoId() == null
                || agendamento.getAlunoId().isBlank()
                || agendamento.getProfissionalResponsavel() == null
                || agendamento.getProfissionalResponsavel().isBlank()
                || agendamento.getData() == null
                || agendamento.getHoraInicio() == null
                || agendamento.getHoraFim() == null
                || agendamento.getModalidade() == null
                || agendamento.getModalidade().isBlank()
                || agendamento.getCor() == null
                || !agendamento.getCor().matches("^#[0-9A-Fa-f]{6}$")) {

            throw new AgendamentoInvalidoException(
                    "Preencha corretamente todos os campos obrigatórios.");
        }

        if (!agendamento.getHoraFim().isAfter(agendamento.getHoraInicio())) {
            throw new AgendamentoInvalidoException(
                    "O horário final deve ser posterior ao horário inicial.");
        }
    }

    private void validarAluno(String alunoId) {
        alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AgendamentoInvalidoException(
                        "O aluno selecionado não foi encontrado."));
    }

    private void validarConflitos(Agendamento novo, String idIgnorado) {
        List<Agendamento> doProfissional =
                agendamentoRepository
                        .findByDataAndProfissionalResponsavelAndStatusNot(
                                novo.getData(),
                                novo.getProfissionalResponsavel(),
                                StatusAgendamento.CANCELADO);

        List<Agendamento> doAluno =
                agendamentoRepository
                        .findByDataAndAlunoIdAndStatusNot(
                                novo.getData(),
                                novo.getAlunoId(),
                                StatusAgendamento.CANCELADO);

        for (Agendamento existente : doProfissional) {
            if (idIgnorado != null && idIgnorado.equals(existente.getId())) {
                continue;
            }

            if (sobrepoeHorario(novo, existente)) {
                throw new AgendamentoInvalidoException(
                        "O profissional já possui um agendamento nesse horário.");
            }
        }

        for (Agendamento existente : doAluno) {
            if (idIgnorado != null && idIgnorado.equals(existente.getId())) {
                continue;
            }

            if (sobrepoeHorario(novo, existente)) {
                throw new AgendamentoInvalidoException(
                        "O aluno já possui um agendamento nesse horário.");
            }
        }
    }

    private boolean sobrepoeHorario(
            Agendamento primeiro, Agendamento segundo) {

        return primeiro.getHoraInicio().isBefore(segundo.getHoraFim())
                && primeiro.getHoraFim().isAfter(segundo.getHoraInicio());
    }
}
