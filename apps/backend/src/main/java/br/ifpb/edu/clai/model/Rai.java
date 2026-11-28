package br.ifpb.edu.clai.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "rais")
public class Rai {

    @Id
    private String id;

    @NotBlank(message = "O PEA vinculado é obrigatório")
    private String peaId;

    // Aspectos observados: relacionamento
    @Size(max = 20)
    private List<String> relacionamento;

    @Size(max = 500)
    private String outrosRelacionamento;

    @Size(max = 5000)
    private String comentarioRelacionamento;

    // Aspectos observados: habilidades cognitivas
    @Size(max = 20)
    private List<String> habilidadesCognitivas;

    @Size(max = 500)
    private String outrasHabilidadesCognitivas;

    @Size(max = 5000)
    private String comentarioHabilidadesCognitivas;

    // Aspectos observados: habilidades socioemocionais
    @Size(max = 20)
    private List<String> habilidadesSocioemocionais;

    @Size(max = 500)
    private String outrasHabilidadesSocioemocionais;

    @Size(max = 5000)
    private String comentarioHabilidadesSocioemocionais;

    // Acompanhamento geral
    @Size(max = 5000)
    private String avancosGerais;

    @Size(max = 5000)
    private String dificuldadesAprendizagem;

    @Size(max = 5000)
    private String fatoresExternos;

    // Habilidades e competências desenvolvidas
    @Valid
    @Size(max = 7, message = "O relatório permite até sete habilidades desenvolvidas")
    private List<HabilidadeDesenvolvida> habilidadesDesenvolvidas;

    // Parecer e finalização
    @Size(max = 10000)
    private String parecer;

    @NotNull(message = "A data do relatório é obrigatória")
    @PastOrPresent(message = "A data do relatório não pode ser futura")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataRelatorio;

    @Size(max = 255)
    private String assinaturaDocente;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class HabilidadeDesenvolvida {

        @NotBlank(message = "A habilidade desenvolvida é obrigatória")
        @Size(max = 1000)
        private String habilidade;

        @NotBlank(message = "O nível de aprendizado é obrigatório")
        @Pattern(
            regexp = "Satisfatório|Bom|Ótimo",
            message = "O nível deve ser Satisfatório, Bom ou Ótimo"
        )
        private String nivelAprendizado;

        @Size(max = 5000)
        private String observacoes;
    }
}
