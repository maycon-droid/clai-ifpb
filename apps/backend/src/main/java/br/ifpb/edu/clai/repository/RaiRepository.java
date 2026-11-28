package br.ifpb.edu.clai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ifpb.edu.clai.model.Rai;

public interface RaiRepository extends MongoRepository<Rai, String> {

    List<Rai> findByPeaId(String peaId);
}
