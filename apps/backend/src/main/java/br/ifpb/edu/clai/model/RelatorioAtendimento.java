package br.ifpb.edu.clai.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "relatorios_atendimento")
public class RelatorioAtendimento {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigoAtendimento;

    @NotBlank(message = "O aluno vinculado é obrigatório")
    private String alunoId;

    @NotBlank(message = "O título é obrigatório")
    private String titulo;

    @NotNull(message = "A data do atendimento é obrigatória")
    private LocalDate dataAtendimento;

    @NotBlank(message = "O profissional responsável é obrigatório")
    private String profissionalResponsavel;

    @NotBlank(message = "A modalidade é obrigatória")
    private String modalidade;

    @NotBlank(message = "O tipo de atendimento é obrigatório")
    private String tipoAtendimento;

    private String objetivo;

    @NotBlank(message = "As atividades realizadas são obrigatórias")
    private String atividadesRealizadas;

    private String observacoes;

    private String encaminhamentos;

    @NotNull(message = "Informe se necessita de acompanhamento")
    private Boolean necessitaAcompanhamento;

    private LocalDate dataProximoAcompanhamento;
}
