package br.ifpb.edu.clai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ifpb.edu.clai.model.EstudoDeCaso;

public interface EstudoDeCasoRepository
        extends MongoRepository<EstudoDeCaso, String> {

    List<EstudoDeCaso> findByAlunoId(String alunoId);
}
