
package br.ifpb.edu.clai.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "estudos_de_caso")
public class EstudoDeCaso {

    @Id
    private String id;

    @NotBlank(message = "O aluno vinculado é obrigatório")
    private String alunoId;

    private List<LocalDate> datasAtendimentoAnamnese;

    private String profissionalResponsavel;

    private EncaminhamentoDemanda encaminhamento;

    private NecessidadeDocumentacao necessidadeDocumentacao;

    private HistoricoVidaEscolar historicoVidaEscolar;

    private PerfilAprendizagem perfilAprendizagem;

    private ContextoPsicossocial contextoPsicossocial;

    private PotencialidadesInteresses potencialidadesInteresses;

    private List<BarreiraEducacional> barreirasEducacionais;

    private SintesePsicopedagogica sintesePsicopedagogica;

    private ApoiosAdaptacoes apoiosAdaptacoes;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EncaminhamentoDemanda {

        private String origemEncaminhamento;

        private String outraOrigem;

        private String motivoPrincipal;

        private String demandaEstudante;

        private String demandaFamilia;

        private String demandaEquipeEscolar;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class NecessidadeDocumentacao {

        private String necessidadeEspecificaInformada;

        private String diagnosticos;

        private Boolean documentosComprobatoriosApresentados;

        private String descricaoDocumentos;

        private String dataProfissionalDocumentos;

        private String comorbidades;

        private String medicacaoUsoContinuo;

        private String encaminhamentosAnteriores;

        private String servicosApoioAnteriores;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HistoricoVidaEscolar {

        private String historicoEscolar;

        private String experienciasEscolaresPositivas;

        private String experienciasEscolaresNegativas;

        private String adaptacoesRecursosUtilizados;

        private String objetivosConhecimentosConsolidados;

        private String areasMaiorFacilidade;

        private String areasMaiorDificuldade;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PerfilAprendizagem {

        private String comunicacaoOrientacoes;

        private String leituraEscritaLinguagem;

        private String raciocinioLogicoMatematico;

        private String atencaoMemoriaFuncoesExecutivas;

        private String organizacaoPlanejamentoTarefas;

        private String interacaoSocialParticipacao;

        private String aspectosSensoriais;

        private String mobilidadeAcessoTecnologias;

        private String autonomiaFamiliar;

        private String autonomiaEscolar;

        private String estrategiasFavoraveisAprendizagem;

        private String situacoesQueDificultamAprendizagem;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ContextoPsicossocial {

        private String relacaoCursoExpectativas;

        private String redeApoioFamiliarSocial;

        private String fatoresPsicossociais;

        private String frequenciaRotinaPermanencia;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PotencialidadesInteresses {

        private String potencialidadesAcademicas;

        private String interessesHabilidades;

        private String formasPreferenciaisAprendizagem;

        private String recursosQueFavorecemDesempenho;
    }

    public enum PrioridadeBarreira {
        ALTA,
        MEDIA,
        BAIXA
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class BarreiraEducacional {

        private String dimensao;

        private String barreiraNecessidadeIdentificada;

        private String evidenciasImpactosAprendizagem;

        private PrioridadeBarreira prioridade;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class SintesePsicopedagogica {

        private String necessidadesEspecificas;

        private String avancosObjetivosAlcancados;

        private String dificuldadesBarreirasEducacionais;

        private String potencialidadesCondicoesFacilitadoras;

        private String principaisDemandas;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ApoiosAdaptacoes {

        private List<String> acessibilidadeAmbiente;

        private List<String> comunicacaoMateriais;

        private List<String> metodologia;

        private List<String> avaliacao;

        private List<String> curriculo;

        private List<String> apoiosInstitucionais;

        private String apoiosExternosEncaminhamentos;
    }

}
