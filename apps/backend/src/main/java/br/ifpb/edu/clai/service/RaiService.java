package br.ifpb.edu.clai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.exception.RaiNaoEncontradoException;
import br.ifpb.edu.clai.model.Rai;
import br.ifpb.edu.clai.repository.RaiRepository;

@Service
public class RaiService {

    private final RaiRepository raiRepository;
    private final PeaService peaService;

    public RaiService(RaiRepository raiRepository, PeaService peaService) {
        this.raiRepository = raiRepository;
        this.peaService = peaService;
    }

    public Rai criar(String peaId, Rai rai) {
        if (peaId == null || peaId.isBlank()) {
            throw new IllegalArgumentException("O PEA vinculado é obrigatório.");
        }

        if (rai == null) {
            throw new IllegalArgumentException("Os dados do RAI são obrigatórios.");
        }

        peaService.buscarPorId(peaId);

        rai.setId(null);
        rai.setPeaId(peaId);

        return raiRepository.save(rai);
    }

    public Rai buscarPorId(String id) {
        return raiRepository.findById(id)
                .orElseThrow(() -> new RaiNaoEncontradoException(
                        "RAI não encontrado para o ID: " + id));
    }

    public List<Rai> listarTodos() {
        return raiRepository.findAll();
    }

    public List<Rai> listarPorPea(String peaId) {
        peaService.buscarPorId(peaId);
        return raiRepository.findByPeaId(peaId);
    }

    public Rai atualizar(String id, Rai dados) {
        if (dados == null) {
            throw new IllegalArgumentException(
                    "Os dados do RAI para atualização são obrigatórios.");
        }

        Rai existente = buscarPorId(id);

        existente.setRelacionamento(dados.getRelacionamento());
        existente.setOutrosRelacionamento(dados.getOutrosRelacionamento());
        existente.setComentarioRelacionamento(dados.getComentarioRelacionamento());

        existente.setHabilidadesCognitivas(dados.getHabilidadesCognitivas());
        existente.setOutrasHabilidadesCognitivas(dados.getOutrasHabilidadesCognitivas());
        existente.setComentarioHabilidadesCognitivas(dados.getComentarioHabilidadesCognitivas());

        existente.setHabilidadesSocioemocionais(dados.getHabilidadesSocioemocionais());
        existente.setOutrasHabilidadesSocioemocionais(dados.getOutrasHabilidadesSocioemocionais());
        existente.setComentarioHabilidadesSocioemocionais(dados.getComentarioHabilidadesSocioemocionais());

        existente.setAvancosGerais(dados.getAvancosGerais());
        existente.setDificuldadesAprendizagem(dados.getDificuldadesAprendizagem());
        existente.setFatoresExternos(dados.getFatoresExternos());
        existente.setHabilidadesDesenvolvidas(dados.getHabilidadesDesenvolvidas());

        existente.setParecer(dados.getParecer());
        existente.setDataRelatorio(dados.getDataRelatorio());
        existente.setAssinaturaDocente(dados.getAssinaturaDocente());

        return raiRepository.save(existente);
    }

}
