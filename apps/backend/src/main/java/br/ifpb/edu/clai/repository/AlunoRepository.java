package br.ifpb.edu.clai.repository;
import java.util.Optional;
import br.ifpb.edu.clai.model.Aluno;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlunoRepository extends MongoRepository<Aluno, String> {
    
    Optional<Aluno> findByMatriculaNumeroMatricula(String numeroMatricula);

    boolean existsByMatriculaNumeroMatricula(String numeroMatricula);
}
