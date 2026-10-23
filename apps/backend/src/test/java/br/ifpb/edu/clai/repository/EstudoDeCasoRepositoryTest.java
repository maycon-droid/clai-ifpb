
package br.ifpb.edu.clai.repository;

import br.ifpb.edu.clai.model.EstudoDeCaso;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest(properties =
    "spring.mongodb.uri=mongodb://localhost:27017/clai_issue35_teste")
class EstudoDeCasoRepositoryTest {

    @Autowired
    private EstudoDeCasoRepository repository;

    @Test
    void deveSalvarEConsultarEstudoPorAluno() {

        String alunoId = "aluno-" + UUID.randomUUID();

        EstudoDeCaso estudo = new EstudoDeCaso();
        estudo.setAlunoId(alunoId);
        estudo.setProfissionalResponsavel("Profissional ficticio");
        estudo.setDatasAtendimentoAnamnese(
            List.of(LocalDate.of(2026, 10, 23))
        );

        EstudoDeCaso.EncaminhamentoDemanda encaminhamento =
            new EstudoDeCaso.EncaminhamentoDemanda();

        encaminhamento.setMotivoPrincipal("Teste de persistencia");
        estudo.setEncaminhamento(encaminhamento);

        EstudoDeCaso salvo = repository.save(estudo);

        try {
            assertNotNull(salvo.getId());

            EstudoDeCaso recuperado =
                repository.findById(salvo.getId()).orElseThrow();

            assertEquals(alunoId, recuperado.getAlunoId());

            assertEquals(
                "Teste de persistencia",
                recuperado.getEncaminhamento().getMotivoPrincipal()
            );

            List<EstudoDeCaso> encontrados =
                repository.findByAlunoId(alunoId);

            assertEquals(1, encontrados.size());
            assertEquals(salvo.getId(), encontrados.get(0).getId());

        } finally {
            repository.deleteById(salvo.getId());
        }
    }
}
