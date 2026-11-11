package br.ifpb.edu.clai.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.exception.EstudoDeCasoNaoEncontradoException;
import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.repository.EstudoDeCasoRepository;

public class EstudoDeCasoServiceTest {
    
    private EstudoDeCasoRepository repository;
    private AlunoService alunoService;
    private EstudoDeCasoService service;

    @BeforeEach
    void preparar() {
        repository = mock(EstudoDeCasoRepository.class);
        alunoService = mock(AlunoService.class);
        service = new EstudoDeCasoService(repository, alunoService);
        when(repository.save(any(EstudoDeCaso.class)))
                .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void deveCriarEstudoVinculadoAoAluno() {
        when(alunoService.buscarPorId("a1")).thenReturn(Optional.of(new Aluno()));
        EstudoDeCaso entrada = new EstudoDeCaso();
        entrada.setId("lixo");

        EstudoDeCaso salvo = service.criar("a1", entrada);

        assertNull(salvo.getId());
        assertEquals("a1", salvo.getAlunoId());
        verify(repository).save(entrada);
    }

    @Test
    void naoDeveCriarParaAlunoInexistente() {
        when(alunoService.buscarPorId("x")).thenReturn(Optional.empty());

        assertThrows(AlunoNaoEncontradoException.class,
                () -> service.criar("x", new EstudoDeCaso()));
        verify(repository, never()).save(any());
    }

    @Test
    void naoDeveCriarSemAluno() {
        assertThrows(IllegalArgumentException.class,
                () -> service.criar(" ", new EstudoDeCaso()));
    }

    @Test
    void deveLancarExcecaoQuandoEstudoNaoExiste() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(EstudoDeCasoNaoEncontradoException.class,
                () -> service.buscarPorId("x"));
    }

    @Test
    void atualizarDevePreservarIdEAlunoOriginais() {
        EstudoDeCaso existente = new EstudoDeCaso();
        existente.setId("e1");
        existente.setAlunoId("a1");
        when(repository.findById("e1")).thenReturn(Optional.of(existente));

        EstudoDeCaso dados = new EstudoDeCaso();
        dados.setAlunoId("OUTRO");
        dados.setProfissionalResponsavel("Novo");

        EstudoDeCaso salvo = service.atualizar("e1", dados);

        assertEquals("e1", salvo.getId());
        assertEquals("a1", salvo.getAlunoId());
        assertEquals("Novo", salvo.getProfissionalResponsavel());
    }

    @Test
    void atualizarInexistenteDeveFalhar() {
        when(repository.findById("x")).thenReturn(Optional.empty());

        assertThrows(EstudoDeCasoNaoEncontradoException.class,
                () -> service.atualizar("x", new EstudoDeCaso()));
    }

}
