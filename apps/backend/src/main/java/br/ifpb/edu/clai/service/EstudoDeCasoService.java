package br.ifpb.edu.clai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.exception.EstudoDeCasoNaoEncontradoException;
import br.ifpb.edu.clai.model.EstudoDeCaso;
import br.ifpb.edu.clai.repository.EstudoDeCasoRepository;

@Service
public class EstudoDeCasoService {

    private final EstudoDeCasoRepository estudoRepository;
    private final AlunoService alunoService;

    public EstudoDeCasoService(EstudoDeCasoRepository estudoRepository,
                               AlunoService alunoService) {
        this.estudoRepository = estudoRepository;
        this.alunoService = alunoService;
    }

    public EstudoDeCaso criar(String alunoId, EstudoDeCaso estudo) {
        if (alunoId == null || alunoId.isBlank()) {
            throw new IllegalArgumentException("O aluno vinculado é obrigatório.");
        }

        alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(
                        "Aluno não encontrado para o ID: " + alunoId));

        estudo.setId(null);
        estudo.setAlunoId(alunoId);

        return estudoRepository.save(estudo);
    }

    public EstudoDeCaso buscarPorId(String id) {
        return estudoRepository.findById(id)
                .orElseThrow(() -> new EstudoDeCasoNaoEncontradoException(
                        "Estudo de caso não encontrado para o ID: " + id));
    }

    public List<EstudoDeCaso> listarTodos() {
        return estudoRepository.findAll();
    }

    public List<EstudoDeCaso> listarPorAluno(String alunoId) {
        return estudoRepository.findByAlunoId(alunoId);
    }

    public EstudoDeCaso atualizar(String id, EstudoDeCaso dados) {
        EstudoDeCaso existente = buscarPorId(id);

        // o aluno vinculado nunca muda na edição
        dados.setId(existente.getId());
        dados.setAlunoId(existente.getAlunoId());

        return estudoRepository.save(dados);
    }
    
}
