package br.ifpb.edu.clai.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ifpb.edu.clai.model.Agendamento;
import br.ifpb.edu.clai.model.StatusAgendamento;

public interface AgendamentoRepository extends MongoRepository<Agendamento, String> {

    List<Agendamento> findByDataAndProfissionalResponsavelAndStatusNot(
            LocalDate data,
            String profissionalResponsavel,
            StatusAgendamento status);

    List<Agendamento> findByDataAndAlunoIdAndStatusNot(
            LocalDate data,
            String alunoId,
            StatusAgendamento status);

    List<Agendamento> findByDataBetweenOrderByDataAscHoraInicioAsc(
            LocalDate inicio,
            LocalDate fim);

    List<Agendamento> findByDataOrderByHoraInicioAsc(LocalDate data);
}
