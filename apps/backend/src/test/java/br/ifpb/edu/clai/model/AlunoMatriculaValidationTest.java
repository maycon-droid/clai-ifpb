package br.ifpb.edu.clai.model;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlunoMatriculaValidationTest {

    @Test
    void deveRejeitarMatriculaEmBranco() {
        Aluno.DadosMatricula matricula = novaMatriculaValida();

        matricula.setNumeroMatricula("   ");

        assertTrue(possuiErro(matricula, "numeroMatricula"));
    }

    @Test
    void deveRejeitarPeriodoIngressoVazio() {
        Aluno.DadosMatricula matricula = novaMatriculaValida();

        matricula.setPeriodoIngresso("");

        assertTrue(possuiErro(matricula, "periodoIngresso"));
    }

    @Test
    void deveRejeitarPeriodoLetivoNulo() {
        Aluno.DadosMatricula matricula = novaMatriculaValida();

        matricula.setPeriodoLetivo(null);

        assertTrue(possuiErro(matricula, "periodoLetivo"));
    }

    @Test
    void deveAceitarDadosAcademicosPreenchidos() {
        Aluno.DadosMatricula matricula = novaMatriculaValida();

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var erros = factory.getValidator().validate(matricula);

            assertTrue(erros.isEmpty());
        }
    }

    @Test
    void deveValidarMatriculaDentroDoAluno() {
        Aluno aluno = new Aluno();

        Aluno.DadosMatricula matricula = novaMatriculaValida();
        matricula.setNumeroMatricula("");

        aluno.setMatricula(matricula);

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            var erros = factory.getValidator().validate(aluno);

            boolean encontrouErro = erros.stream()
                    .anyMatch(erro -> erro.getPropertyPath()
                            .toString()
                            .equals("matricula.numeroMatricula"));

            assertTrue(encontrouErro);
        }
    }

    private Aluno.DadosMatricula novaMatriculaValida() {
        Aluno.DadosMatricula matricula = new Aluno.DadosMatricula();

        matricula.setNumeroMatricula("202600123");
        matricula.setCurso("Sistemas para Internet");
        matricula.setPeriodoIngresso("2026.1");
        matricula.setPeriodoLetivo("2026.2");

        return matricula;
    }

    private boolean possuiErro(
            Aluno.DadosMatricula matricula,
            String campo) {

        try (ValidatorFactory factory =
                Validation.buildDefaultValidatorFactory()) {

            return !factory.getValidator()
                    .validateProperty(matricula, campo)
                    .isEmpty();
        }
    }
}