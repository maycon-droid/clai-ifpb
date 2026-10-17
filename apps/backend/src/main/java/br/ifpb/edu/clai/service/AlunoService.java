package br.ifpb.edu.clai.service;

import br.ifpb.edu.clai.model.Aluno;
import br.ifpb.edu.clai.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public Aluno salvar(Aluno aluno) {
        if (aluno.getMatricula() == null || aluno.getMatricula().getNumeroMatricula() == null || aluno.getMatricula().getNumeroMatricula().isBlank()) {
            throw new IllegalArgumentException("A matrícula do aluno é obrigatória.");
        }

        if (alunoRepository.existsByMatriculaNumeroMatricula(aluno.getMatricula().getNumeroMatricula())) {
            throw new IllegalStateException("Já existe um aluno cadastrado com esta matrícula.");
        }

        return alunoRepository.save(aluno);
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAll();
    }

    public Optional<Aluno> buscarPorId(String id) {
        return alunoRepository.findById(id);
    }

    public Optional<Aluno> buscarPorMatricula(String numeroMatricula) {
        return alunoRepository.findByMatriculaNumeroMatricula(numeroMatricula);
    }

    public Aluno atualizar(String id, Aluno dadosAtualizados) {
        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado para o ID: " + id));

        // pra valida se a nova matrícula já está em uso por outro aluno
        String novaMatricula = dadosAtualizados.getMatricula() != null ? dadosAtualizados.getMatricula().getNumeroMatricula() : null;

        //Correção da validação da matrícula para verificar se é nula ou em branco(change request)
        if(novaMatricula == null || novaMatricula.isBlank()) {
            throw new IllegalArgumentException("A nova matrícula informada é inválida.");
        }
            String matriculaAtual = alunoExistente.getMatricula() != null ? alunoExistente.getMatricula().getNumeroMatricula() : null;

        if (novaMatricula != null && !novaMatricula.equals(matriculaAtual)) {
            if (alunoRepository.existsByMatriculaNumeroMatricula(novaMatricula)) {
                throw new IllegalStateException("A nova matrícula informada já está em uso.");
            }
        }

        alunoExistente.setNome(dadosAtualizados.getNome());
        alunoExistente.setCpf(dadosAtualizados.getCpf());
        alunoExistente.setEmail(dadosAtualizados.getEmail());
        alunoExistente.setMatricula(dadosAtualizados.getMatricula());
        alunoExistente.setTipoAluno(dadosAtualizados.getTipoAluno());

        return alunoRepository.save(alunoExistente);
    }

    public void remover(String id) {
        if (!alunoRepository.existsById(id)) {
            throw new IllegalArgumentException("Aluno não encontrado para o ID: " + id);
        }
        alunoRepository.deleteById(id);
    }
}
