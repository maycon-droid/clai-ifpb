package br.ifpb.edu.clai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.exception.PeaNaoEncontradoException;
import br.ifpb.edu.clai.model.Pea;
import br.ifpb.edu.clai.repository.PeaRepository;

@Service
public class PeaService {

    private final PeaRepository peaRepository;
    private final PeiService peiService;

    public PeaService(PeaRepository peaRepository, PeiService peiService) {
        this.peaRepository = peaRepository;
        this.peiService = peiService;
    }

    public Pea criar(String peiId, Pea pea) {
        if (peiId == null || peiId.isBlank()) {
            throw new IllegalArgumentException("O PEI vinculado é obrigatório.");
        }

        if (pea == null) {
            throw new IllegalArgumentException("Os dados do PEA são obrigatórios.");
        }

        peiService.buscarPorId(peiId);

        pea.setId(null);
        pea.setPeiId(peiId);

        return peaRepository.save(pea);
    }

    public Pea buscarPorId(String id) {
        return peaRepository.findById(id)
                .orElseThrow(() -> new PeaNaoEncontradoException(
                        "PEA não encontrado para o ID: " + id));
    }

    public List<Pea> listarTodos() {
        return peaRepository.findAll();
    }

    public List<Pea> listarPorPei(String peiId) {
        peiService.buscarPorId(peiId);
        return peaRepository.findByPeiId(peiId);
    }

    public Pea atualizar(String id, Pea dados) {
        if (dados == null) {
            throw new IllegalArgumentException(
                    "Os dados do PEA para atualização são obrigatórios.");
        }

        Pea existente = buscarPorId(id);

        atualizarSeEnviado(dados.getComponenteCurricular(),
                existente::setComponenteCurricular);
        atualizarSeEnviado(dados.getSemestreAno(),
                existente::setSemestreAno);
        atualizarSeEnviado(dados.getDocente(),
                existente::setDocente);
        atualizarSeEnviado(dados.getObjetivoGeral(),
                existente::setObjetivoGeral);
        atualizarSeEnviado(dados.getObjetivosEspecificos(),
                existente::setObjetivosEspecificos);
        atualizarSeEnviado(dados.getConteudosProgramaticos(),
                existente::setConteudosProgramaticos);
        atualizarSeEnviado(dados.getMetodologia(),
                existente::setMetodologia);
        atualizarSeEnviado(dados.getAvaliacao(),
                existente::setAvaliacao);
        atualizarSeEnviado(dados.getBibliografiaBasica(),
                existente::setBibliografiaBasica);
        atualizarSeEnviado(dados.getBibliografiaComplementar(),
                existente::setBibliografiaComplementar);
        atualizarSeEnviado(dados.getAssinaturaDocente(),
                existente::setAssinaturaDocente);

        return peaRepository.save(existente);
    }

    private void atualizarSeEnviado(String valor,
            java.util.function.Consumer<String> atualizador) {
        if (valor != null) {
            atualizador.accept(valor);
        }
    }
}
