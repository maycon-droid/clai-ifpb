package br.ifpb.edu.clai.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import lombok.*;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import org.hibernate.validator.constraints.br.CPF;

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
    @Pattern(
    regexp = "[0-9]{11}",
    message = "O CPF deve conter exatamente 11 dígitos"
    )
    @CPF(message = "O CPF informado é inválido")
    private String cpf;

    @NotNull(message = "A matrícula do aluno é obrigatória")
    @Valid
    private DadosMatricula matricula;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past(message = "A data de nascimento deve ser anterior à data atual")
    private LocalDate dataNascimento;

    @Valid
    @NotNull
    private Contato contatoAluno;

    @Valid
    @NotNull
    private DadosResponsavel dadosResponsavel;

    //classes auxiliares
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DadosMatricula {

        @Indexed(unique = true)
        @NotBlank(message = "A matrícula do aluno é obrigatória")
        private String numeroMatricula; // renomeado pra evitar erro com AlunoRepository

        private String curso;


        @NotBlank(message = "O período de ingresso do aluno é obrigatório")
        private String periodoIngresso;

        @NotBlank(message = "O período letivo do aluno é obrigatório")
        private String periodoLetivo;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contato {

    @NotBlank(message = "O telefone é obrigatório")
    @Pattern(
        regexp = "\\d{10,11}",
        message = "O telefone deve conter 10 ou 11 dígitos, incluindo o DDD"
    )
    private String telefone;

        @Email(message = "O email deve ser válido")
        @Pattern(
            regexp = "^$|^[^@\\s]+@[^@.\\s]+(\\.[^@.\\s]+)+$",
            message = "O email deve possuir um domínio válido, como aluno@email.com"
        )
        private String email;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DadosResponsavel {

        @NotBlank(message = "O nome do responsável é obrigatório")
        private String nome;

        @NotBlank(message = "O parentesco é obrigatório")
        private String parentesco;

        @Valid
        @NotNull(message = "O contato do responsável é obrigatório")
        private Contato contatoDoResponsavel;
    }

}