package br.ifpb.edu.clai.service;

import java.time.LocalDate;
import java.util.List;

import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import br.ifpb.edu.clai.exception.AlunoNaoEncontradoException;
import br.ifpb.edu.clai.model.RelatorioAtendimento;
import br.ifpb.edu.clai.repository.RelatorioAtendimentoRepository;

@Service
public class RelatorioAtendimentoService {

    private final RelatorioAtendimentoRepository relatorioRepository;
    private final AlunoService alunoService;
    private final MongoTemplate mongoTemplate;

    public RelatorioAtendimentoService(
            RelatorioAtendimentoRepository relatorioRepository,
            AlunoService alunoService,
            MongoTemplate mongoTemplate) {

        this.relatorioRepository = relatorioRepository;
        this.alunoService = alunoService;
        this.mongoTemplate = mongoTemplate;
    }

    public RelatorioAtendimento registrar(
            String alunoId, RelatorioAtendimento relatorio) {

        if (alunoId == null || alunoId.isBlank()) {
            throw new IllegalArgumentException(
                    "O aluno vinculado é obrigatório.");
        }

        if (relatorio == null) {
            throw new IllegalArgumentException(
                    "Os dados do atendimento são obrigatórios.");
        }

        alunoService.buscarPorId(alunoId)
                .orElseThrow(() -> new AlunoNaoEncontradoException(
                        "Aluno não encontrado para o ID: " + alunoId));

        relatorio.setId(null);
        relatorio.setAlunoId(alunoId);
        relatorio.setCodigoAtendimento(gerarCodigoAtendimento());

        return relatorioRepository.save(relatorio);
    }

    public List<RelatorioAtendimento> listarPorAluno(String alunoId) {
        return relatorioRepository.findByAlunoId(alunoId);
    }

    private String gerarCodigoAtendimento() {

        int ano = LocalDate.now().getYear();

        String contadorId = "relatorio_atendimento_" + ano;

        Query query = Query.query(
                Criteria.where("_id").is(contadorId));

        Update update = new Update().inc("sequencia", 1);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .upsert(true)
                .returnNew(true);

        Document contador = mongoTemplate.findAndModify(
                query,
                update,
                options,
                Document.class,
                "contadores");

        Number sequencia = contador.get("sequencia", Number.class);

        return String.format(
                "ATD-%d-%06d",
                ano,
                sequencia.longValue());
    }
}
