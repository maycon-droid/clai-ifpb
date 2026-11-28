package br.ifpb.edu.clai.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "agendamentos")
public class Agendamento {

    @Id
    private String id;

    @NotBlank(message = "O aluno vinculado é obrigatório")
    private String alunoId;

    @NotBlank(message = "O profissional responsável é obrigatório")
    @Size(max = 255, message = "O nome do profissional deve ter no máximo 255 caracteres")
    private String profissionalResponsavel;

    @NotNull(message = "A data do agendamento é obrigatória")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate data;

    @NotNull(message = "O horário inicial é obrigatório")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaInicio;

    @NotNull(message = "O horário final é obrigatório")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime horaFim;

    @NotBlank(message = "A modalidade é obrigatória")
    private String modalidade;

    @NotNull(message = "O status do agendamento é obrigatório")
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @NotBlank(message = "A cor do agendamento é obrigatória")
    @Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "A cor deve estar no formato hexadecimal, como #3498DB"
    )
    private String cor;

    @Size(max = 2000, message = "As observações devem ter no máximo 2000 caracteres")
    private String observacoes;

    private String relatorioId;
}
