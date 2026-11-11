package br.ifpb.edu.clai.integration;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.repository.AlunoRepository;
import br.ifpb.edu.clai.repository.EstudoDeCasoRepository;
import br.ifpb.edu.clai.service.AlunoService;
import br.ifpb.edu.clai.service.EstudoDeCasoService;

@SpringBootTest(properties =
    "spring.mongodb.uri=mongodb://localhost:27017/clai_issue10_teste")
class AlunoEstudoDeCasoIntegracaoTest {

    @Autowired private AlunoService alunoService;
    @Autowired private EstudoDeCasoService estudoService;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private EstudoDeCasoRepository estudoRepository;

    private final List<String> alunosCriados = new ArrayList<>();

    @AfterEach
    void limpar() {
        alunosCriados.forEach(id -> {
            estudoRepository.findByAlunoId(id).forEach(estudoRepository::delete);
            alunoRepository.deleteById(id);
        });
        alunosCriados.clear();
    }

    private Aluno criarAluno() {
        Aluno aluno = new Aluno();
        aluno.setNome("Aluno ficticio");
        aluno.setCpf("12345678901");

        Aluno.DadosMatricula matricula = new Aluno.DadosMatricula();
        matricula.setNumeroMatricula("MAT-" + UUID.randomUUID());
        matricula.setPeriodoIngresso("2026.1");
        aluno.setMatricula(matricula);

        Aluno salvo = alunoService.salvar(aluno);
        alunosCriados.add(salvo.getId());
        return salvo;
    }

    @Test
    void deveCriarEstudoParaAlunoExistenteEEncontrarPorAluno() {
        Aluno aluno = criarAluno();
        EstudoDeCaso estudo = new EstudoDeCaso();
        estudo.setProfissionalResponsavel("Profissional ficticio");

        EstudoDeCaso salvo = estudoService.criar(aluno.getId(), estudo);

        assertNotNull(salvo.getId());
        List<EstudoDeCaso> doAluno = estudoService.listarPorAluno(aluno.getId());
        assertEquals(1, doAluno.size());
        assertEquals(salvo.getId(), doAluno.get(0).getId());
    }

    @Test
    void naoDeveCriarEstudoParaAlunoInexistente() {
        assertThrows(AlunoNaoEncontradoException.class,
                () -> estudoService.criar("aluno-que-nao-existe", new EstudoDeCaso()));
    }

    @Test
    void atualizarNaoDeveTrocarOAlunoVinculado() {
        Aluno a1 = criarAluno();
        Aluno a2 = criarAluno();
        EstudoDeCaso salvo = estudoService.criar(a1.getId(), new EstudoDeCaso());

        EstudoDeCaso dados = new EstudoDeCaso();
        dados.setAlunoId(a2.getId());
        dados.setProfissionalResponsavel("Atualizado");
        estudoService.atualizar(salvo.getId(), dados);

        EstudoDeCaso recarregado = estudoService.buscarPorId(salvo.getId());
        assertEquals(a1.getId(), recarregado.getAlunoId());
        assertEquals("Atualizado", recarregado.getProfissionalResponsavel());
        assertTrue(estudoService.listarPorAluno(a2.getId()).isEmpty());
    }

    @Test
    void alunoPodeTerMaisDeUmEstudo() {
        Aluno aluno = criarAluno();
        estudoService.criar(aluno.getId(), new EstudoDeCaso());
        estudoService.criar(aluno.getId(), new EstudoDeCaso());

        assertEquals(2, estudoService.listarPorAluno(aluno.getId()).size());
    }
}