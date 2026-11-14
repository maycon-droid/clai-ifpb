package br.ifpb.edu.clai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ifpb.edu.clai.model.RelatorioAtendimento;

public interface RelatorioAtendimentoRepository
        extends MongoRepository<RelatorioAtendimento, String> {

    List<RelatorioAtendimento> findByAlunoId(String alunoId);
}
