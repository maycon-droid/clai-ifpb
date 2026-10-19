package br.ifpb.edu.clai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "alunos")
public class Aluno {

    @Id
    private String id;

    @NotBlank(message = "O  nome do aluno é obrigatório")
    private String nome;

    @NotBlank(message = "O CPF do aluno é obrigatório")
    @Pattern(regexp = "[0-9]{11}", message = "O CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @Email(message = "O email do aluno deve ser válido")
    @Pattern(
        regexp = "^$|^[^@\\s]+@[^@.\\s]+(\\.[^@.\\s]+)+$",
        message = "O email deve possuir um domínio válido, como aluno@email.com"
    )
    private String email;

    @NotNull(message = "A matrícula do aluno é obrigatória")
    @Valid
    private DadosMatricula matricula;

    @NotBlank(message = "O tipo do aluno é obrigatório")
    private String tipoAluno;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DadosMatricula {

        @Indexed(unique = true)
        @NotBlank(message = "A matrícula do aluno é obrigatória")
        private String numeroMatricula; // renomeado pra evitar erro com AlunoRepository

        @NotBlank(message = "O período de ingresso do aluno é obrigatório")
        private String periodoIngresso;

        private String status = "Ativo";
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoInfo {

        @NotBlank(message = "O nome do curso é obrigatório")
        private String nomeCurso;

        @NotBlank(message = "O código do curso é obrigatório")
        private String codigoCurso;

        @NotBlank(message = "O nível de ensino do curso é obrigatório")
        private String nivelEnsino;

        @NotBlank(message = "O eixo tecnológico do curso é obrigatório")
        private String eixoTecnologico;
    }

    public static class SerieInfo {

    }
}