package br.ifpb.edu.clai.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import br.ifpb.edu.clai.model.Pea;

public interface PeaRepository extends MongoRepository<Pea, String> {

    List<Pea> findByPeiId(String peiId);
}
